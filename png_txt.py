from wand.image import Image
from PIL import Image as PI
import pyocr
import pyocr.builders
import io
import codecs

tool = pyocr.get_available_tools()[0]

img = PI.open("/Users/xiaoshi/Dropbox/Stanford/cs341/fil_test_nab3.png")
txt = tool.image_to_string(img,lang="eng",builder=pyocr.builders.TextBuilder())
target = codecs.open("/Users/xiaoshi/Dropbox/Stanford/cs341/fil_test_nab3_out_text2",'w','utf-8')
target.write(txt)
target.close()