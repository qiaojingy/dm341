from PIL import Image
from PIL import ImageFilter
img = Image.open("/Users/xiaoshi/Dropbox/Stanford/cs341/fil_test_nab3.png")
#img  = img.point(lambda x:0 if x<254 else 255)
img = img.convert('L')

#setting 1
#basewidth = 700
#wpercent = (basewidth / float(img.size[0]))
#hsize = int((float(img.size[1]) * float(wpercent)))
#img = img.resize((basewidth,hsize),Image.ANTIALIAS)
#img  = img.point(lambda x: x>195 and 255)

#cur best setting
#img  = img.point(lambda x: x>190 and 255)
#img = img.filter(ImageFilter.GaussianBlur(radius = 1.5))

img  = img.point(lambda x: x>190 and 255)
img = img.filter(ImageFilter.GaussianBlur(radius = 1.5))
img.save("/Users/xiaoshi/Dropbox/Stanford/cs341/fil_test_nab3_out.png")

#generate several texts with different gaussian filter raidus and extract info from these files.