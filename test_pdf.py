#refer to https://pythontips.com/2016/02/25/ocr-on-pdf-files-using-python/

from wand.image import Image
from PIL import Image as PI
import pyocr
import pyocr.builders
import io
import codecs
from PIL import ImageFilter
tool = pyocr.get_available_tools()[0]

req_image = []
#final_text = []
final_text = "NOT NAB"
image_pdf = Image(filename="/Users/xiaoshi/Dropbox/Stanford/cs341/final/source/49157Political File2014Non-Candidate Issue Adsnational education assocSCAN-14100711320 (14127116222163)_.pdf", resolution=450)
image_png = image_pdf.convert('bmp')

for img in image_png.sequence:
	img_page = Image(image=img)
	#img_page.type='grayscale'
	#img_page = img_page.point(lambda x:0 if x<143 else 255)
	req_image.append(img_page.make_blob('bmp'))
	#break

count = 0
for img in req_image:
	tmp_img = PI.open(io.BytesIO(img))
	tmp_img = tmp_img.convert('L')
	#tmp_img = tmp_img.point(lambda x: x>190 and 255)
	tmp_img = tmp_img.point(lambda x: x>190 and 255)
	tmp_img = tmp_img.filter(ImageFilter.GaussianBlur(radius = 1.5))
	print tmp_img.size
	#address = '/Users/xiaoshi/Dropbox/Stanford/cs341/ww_1pic'+str(count)+'.png'
	#tmp_img = tmp_img.point(lambda x:0 if x<143 else 255)
	#tmp_img.save('/Users/xiaoshi/Dropbox/Stanford/cs341/fil_test_nab2.png')
	#tmp_img.save(address)
	#txt = tool.image_to_string(PI.open(io.BytesIO(img)),lang="eng",builder=pyocr.builders.TextBuilder())

	#tmp_img = tmp_img.rotate(90)
	txt = tool.image_to_string(tmp_img,lang="eng",builder=pyocr.builders.TextBuilder())
	

	final_text = txt
	break;
'''
	word_count = 0
	word_bank = ["AGREEMENT","FORM","POLITICAL","CANDIDATE","ADVERTISEMENTS","NON-CANDIDATE"]
	for word in word_bank:
		if txt.find(word) >=0:
			word_count = word_count+1
	if word_count >=3:
		final_text = txt
		break
	
'''


	# txt = tool.image_to_string(
	#     PI.open(io.BytesIO(img)),
	#     lang="eng",
	#     builder=pyocr.builders.TextBuilder()
	# )
	#final_text.append(txt)
	#count = count+1


target = codecs.open("/Users/xiaoshi/Dropbox/Stanford/cs341/final/target/49157Political File2014Non-Candidate Issue Adsnational education assocSCAN-14100711320 (14127116222163)_out",'w','utf-8')
target.write(final_text)
target.close()
# for item in final_text:
# 	target.write(item)
# target.close()