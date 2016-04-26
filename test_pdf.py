#refer to https://pythontips.com/2016/02/25/ocr-on-pdf-files-using-python/

from wand.image import Image
from PIL import Image as PI
import pyocr
import pyocr.builders
import io
import codecs

tool = pyocr.get_available_tools()[0]

req_image = []
final_text = []

image_pdf = Image(filename="/Users/xiaoshi/Dropbox/Stanford/cs341/test2.pdf", resolution=500)
image_jpeg = image_pdf.convert('png')

for img in image_jpeg.sequence:
    img_page = Image(image=img)
    req_image.append(img_page.make_blob('png'))

for img in req_image: 
    txt = tool.image_to_string(
        PI.open(io.BytesIO(img)),
        lang="eng",
        builder=pyocr.builders.TextBuilder()
    )
    final_text.append(txt)

#print txt
target = codecs.open("/Users/xiaoshi/Dropbox/Stanford/cs341/test_out2",'w','utf-8')

for item in final_text:
	target.write(item)
target.close()