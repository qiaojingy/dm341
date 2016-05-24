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

def merge_org(line_p1,line_p2):
	str_org = merge_org_name(line_p1,line_p2,0)
	ex = re.search("[a-zA-Z]\s[a-z]",str_org)
	while ex:
		str_temp = ex.group(0)
		str_org = str_org.replace(str_temp,str_temp[0]+str_temp[2])
		ex = re.search("[a-zA-Z]\s[a-z]",str_org)
	return str_org


def merge_org_name(line_p1,line_p2,order):
	#detect first capital letter in two lines(There should be one letter in first letters of two lines that are initial)
	res1 = re.search('[a-zA-Z]', line_p1)
	res2 = re.search('[a-zA-Z]',line_p2)
	if not(res1) and not(res2):
		return ""
	elif not(res1):
		return line_p2
	elif not(res2):
		return line_p1
	elif res1.group(0).isupper() and res2.group(0).isupper():
		res_p2 = re.search('[A-Z][a-z]*',line_p2)
		str_p2 = res_p2.group(0)
		pos_p2 = line_p2.find(str_p2)+len(str_p2)
		return str_p2 + " "+merge_org_name(line_p1,line_p2[pos_p2:],0)
	elif res1.group(0).islower() and res2.group(0).islower():
		#line1 is first
		str_f = ""
		res_p1 = re.search('[a-z]+',line_p1)
		str_p1 = res_p1.group(0)
		pos_p1 = line_p1.find(str_p1)+len(str_p1)
		res_p2 = re.search('[a-z]+',line_p2)
		str_p2 = res_p2.group(0)
		pos_p2 = line_p2.find(str_p2)+len(str_p2)
		if order == 1:
			str_f = str_p1 + str_p2
		elif order == 2:
			str_f = str_p2 + str_p1
		return str_f+" "+merge_org_name(line_p1[pos_p1:],line_p2[pos_p2:],0)
	elif res1.group(0).isupper():
		res1_1 = re.search('[A-Z][a-z]*',line_p1)
		str1 = res1_1.group(0)
		pos1 = line_p1.find(str1)+len(str1)
		res2 = re.search('[a-z]+',line_p2)
		str2 = res2.group(0)
		pos2 = line_p2.find(str2)+len(str2)
		str_f = str1+str2
		print str_f
		return str_f + " "+merge_org_name(line_p1[pos1:],line_p2[pos2:],1)
	else:
		res1_2 = re.search('[A-Z][a-z]*',line_p2)
		str_p2 = res1_2.group(0)
		pos_p2 = line_p2.find(str_p2)+len(str_p2)
		res_p1 = re.search('[a-z]+',line_p1)
		str_p1 = res_p1.group(0)
		pos_p1 = line_p1.find(str_p1)+len(str_p1)
		str_f = str_p2+str_p1
		print str_f
		return str_f + " "+ merge_org_name(line_p1[pos_p1:],line_p2[pos_p2:],2)



def extract_info(txt,val):
	ret_val = ["","",""]
	if val == 1:
		print "Candidate"
		count = 0
		rep_count = 1000
		candidate_org = ""
		org_find = False
		cand_find = False
		cur_line = ""
		prev_line = ""
		rep_find = False
		for line in txt:
			#print line
			prev_line = cur_line
			cur_line = line
			if (line.startswith("I.") or line.startswith("I,") or line.startswith("1,") or line.startswith("1.") or line.startswith("I ") or line.startswith("l,") or line.startswith("l.")) and rep_find==False:
				#target.write("representative: "+line[2:])
				ret_val[0]=line[2:]
				print "representative is: "+ret_val[0]
				rep_count = count
				rep_find = True
			elif count > rep_count and re.search('[a-zA-Z]', line) and cand_find == False:
				#print "candidate "+ line
				candidate_org = line
				cand_find = True
			if "behalf" in line:
				start1 = line.find(":")
				if start1 == -1:
					start1 = start1+len(line)
				start2 = line.find(";")
				if start2 == -1:
					start2 = start2+len(line)
				start = min(start1, start2)
				true_start = start+1
				#
				if re.search('[a-zA-Z]', line[true_start:]):
					#print "current line: "+line[true_start:]
					line_final = ""
					line_p2 = line[true_start:]
					#deal with ", a legally" NAB form PB-17
					res_p2 = re.search("a leg",line_p2)
					if res_p2:
						str_p2 = res_p2.group(0)
						line_p2 = line_p2[:line_p2.find(str_p2)]
						res_p22 = re.search(".*,",line_p2)
						if res_p22:
							line_p2 = res_p22.group(0)
					# For color pdf, the name of organization may be splited into two lines
					if re.search('[a-zA-Z]', prev_line):
						line_p1 = prev_line
						line_final = merge_org(line_p1,line_p2)
					else:
						line_final = line_p2
					#target.write("organization: "+line_final)
					ret_val[1] = line_final
					org_find = True
			count = count+1
		if org_find == False:
			#judge this is a name of a org
			#target.write("candidate organization: "+candidate_org)
			ret_val[2] = candidate_org
	else:
		print "Non Candidate"
		rep_find = False
		org_find = False
		for line in txt:
			#print "hello"
			#print line
			if rep_find == True and org_find == False and re.search('[a-zA-Z]', line):
				print "org_name is: "+line
				ret_val[1] = line
				org_find = True
			elif (line.startswith("I.") or line.startswith("I,") or line.startswith("1,") or line.startswith("1.") or line.startswith("I ") or line.startswith("l,") or line.startswith("l.")) and rep_find==False:
				ret_val[0]=line[2:]
				print "representative is: "+ret_val[0]
			elif line.startswith("do hereby request") and rep_find==False:
				print "do hereby request"
				rep_find = True
			elif line.startswith("This broadcast time"):
				start = line.find(":")
				print "org_name is: "+line[start+1:]
				ret_val[1] = line[start+1:]
				break
	return ret_val


def check_NAB(txt):
	#url is for request detection
	NAB_bank = ["AGREEMENT","FORM","POLITICAL","CANDIDATE","ADVERTISEMENTS","NON-CANDIDATE"]
	NAB_Non_bank = ["NON","Issues","SSUE"]
	NAB_Non_word = "do hereby request"
	NAB_word ="NAB Form"
	NAB_count = 0
	for word in NAB_bank:
		if txt.find(word) >=0:
			NAB_count = NAB_count+1
	if NAB_count >=4 or txt.find(NAB_word) >=0:
		non_count = 0;
		for word in NAB_Non_bank:
			if txt.find(word)>=0:
				non_count = non_count+1
		if non_count >= 2:
			return 2#non-candidate
		return 1#candidate
	return 0





read_book = xlrd.open_workbook('/Users/xiaoshi/Dropbox/Stanford/cs341/final/test.xlsx')
#currently only read first sheet
read_sheet = read_book.sheet_by_index(0)
offset = 1

write_book = xlsxwriter.Workbook('/Users/xiaoshi/Dropbox/Stanford/cs341/final/e_results.xlsx')
write_sheet = write_book.add_worksheet()

row_count = 1

for i, row in enumerate(range(read_sheet.nrows)):
	print i
	if i< offset:
		write_sheet.write(i,0,"record id")
		write_sheet.write(i,1,"representative name")
		write_sheet.write(i,2,"org name")
		write_sheet.write(i,3,"candidate org name")
		continue
	record_id = read_sheet.cell_value(i,0)
	record_url = read_sheet.cell_value(i,2)
	try:
		#print record_url
		req = requests.get(record_url)
		if req.status_code != 200:
			break
		image_pdf = Image(blob=req.content,resolution=450)
		image_bmp = image_pdf.convert('bmp')
		#currently we only detect first page for efficiency
		for img in image_bmp.sequence:
			img_page = Image(image=img)
			img_page =img_page.make_blob('bmp')
			tmp_img = PI.open(io.BytesIO(img_page))
			tmp_img = tmp_img.convert('L')
			tmp_img = tmp_img.point(lambda x: x>190 and 255)
			tmp_img = tmp_img.filter(ImageFilter.GaussianBlur(radius = 1.5))
			txt = tool.image_to_string(tmp_img,lang="eng",builder=pyocr.builders.TextBuilder())
			#print txt
			val = check_NAB(txt)
			#print val
			
			#print txt
			if val >0:
				txt2 = txt.split("\n")
				info = extract_info(txt2,val)
				if info[0]+info[1]+info[2] !="":
					write_sheet.write(row_count,0,record_id)
					write_sheet.write(row_count,1,info[0])
					write_sheet.write(row_count,2,info[1])
					write_sheet.write(row_count,3,info[2])
					row_count = row_count+1
			break
	except requests.exceptions.RequestException as e:
		print e

write_book.close()