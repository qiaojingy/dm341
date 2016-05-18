#input: record id, url
#output: record id, which type
import requests
import time
import xlsxwriter
import xlrd

from wand.image import Image
from PIL import Image as PI
import pyocr
import pyocr.builders
import io
import codecs
from PIL import ImageFilter
tool = pyocr.get_available_tools()[0]
import re

def check_format(txt):
	#url is for request detection
	NAB_bank = ["AGREEMENT","FORM","POLITICAL","CANDIDATE","ADVERTISEMENTS","NON-CANDIDATE"]
	NAB_word ="NAB Form"
	#is order worksheet contract or request?
	contract_bank = ["CONTRACT","Commission"]
	contract_word = "Contract"
	contract_word_neg = "REQUEST"
	invoice_bank = ["INVOICE"]
	request_bank = ["REQUEST","ORDER WORKSHEET"]
	#check NAB
	if txt.find(NAB_word) >=0:
		return 0
	NAB_count = 0
	for word in NAB_bank:
		if txt.find(word) >=0:
			NAB_count = NAB_count+1
	if NAB_count >=4:
		return 0
	Invoice_count = 0
	for word in invoice_bank:
		if txt.find(word) >=0:
			Invoice_count = Invoice_count+1
	if Invoice_count>=1:
		return 1
	Contract_count = 0
	for word in contract_bank:
		if txt.find(word) >=0:
			Contract_count = Contract_count+1
	if txt.upper().find(contract_word_neg) >=0:
		Contract_count = Contract_count-2;
	if Contract_count>=1:
		return 2
	# multiple 'Contract' word appears
	if len(re.findall(contract_word,txt))>=2:
		return 2
	Request_count = 0
	#Request form is variable and hard to detect
	txt = txt.upper()
	for word in request_bank:
		if txt.find(word) >=0:
			Request_count = Request_count+1
	if Request_count>=1:
		return 3
	#nothing detected
	return 4


read_book = xlrd.open_workbook('/Users/xiaoshi/Dropbox/Stanford/cs341/classify/test.xlsx')
#currently only read first sheet
read_sheet = read_book.sheet_by_index(0)
offset = 1

write_book = xlsxwriter.Workbook('/Users/xiaoshi/Dropbox/Stanford/cs341/classify/c_results.xlsx')
write_sheet = write_book.add_worksheet()



for i, row in enumerate(range(read_sheet.nrows)):
	if i< offset:
		continue
	#for col in enumerate(range(read_sheet.ncols)):
	#print "pos2"
	record_id = read_sheet.cell_value(i,0)
	record_url = read_sheet.cell_value(i,2)
	type_contain = [False,False,False,False]
	type_meaning = ['NAB','Invoice','Contracts','Request']
	try:
		#print record_url
		req = requests.get(record_url)
		if req.status_code != 200:
			write_sheet.write(i,0,record_id)
			write_sheet.write(i,1,record_url)
			write_sheet.write(i,2,"CANNOT DOWNLOAD FILE")
			break
		image_pdf = Image(blob=req.content,resolution=450)
		image_bmp = image_pdf.convert('bmp')
		#currently we only detect first page for efficiency
		for img in image_bmp.sequence:
			write_sheet.write(i,0,record_id)
			write_sheet.write(i,1,record_url)
			img_page = Image(image=img)
			img_page =img_page.make_blob('bmp')
			tmp_img = PI.open(io.BytesIO(img_page))
			tmp_img = tmp_img.convert('L')
			tmp_img = tmp_img.point(lambda x: x>190 and 255)
			tmp_img = tmp_img.filter(ImageFilter.GaussianBlur(radius = 1.5))
			txt = tool.image_to_string(tmp_img,lang="eng",builder=pyocr.builders.TextBuilder())
			val = check_format(txt)
			print "round 1"
			if val <4:
				#print type_meaning[val]
				write_sheet.write(i,2,type_meaning[val])
				# add breaks to only detect first page of each file
				break;
			tmp_img2 = tmp_img.rotate(90)
			txt2 = tool.image_to_string(tmp_img2,lang="eng",builder=pyocr.builders.TextBuilder())
			val2 = check_format(txt2)
			print "round 2"
			if val2 <4:
				write_sheet.write(i,2,type_meaning[val2])
				break
			tmp_img3 = tmp_img.rotate(-90)
			txt3 = tool.image_to_string(tmp_img3,lang="eng",builder=pyocr.builders.TextBuilder())
			val3 = check_format(txt3)
			print "round 3"
			if val3 <4:
				write_sheet.write(i,2,type_meaning[val3])
				break
			#the formats of request are complicating and hard to find pattern
			if record_url.upper().find("REQUEST") >=0:
				write_sheet.write(i,2,type_meaning[3])
				break
			write_sheet.write(i,2,"NO FORMAT DETECTED")
			break
	except requests.exceptions.RequestException as e:
		print e
		

#read_book.close()
write_book.close()
