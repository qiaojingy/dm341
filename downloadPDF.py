import requests
import time
with open('/Users/xiaoshi/Dropbox/Stanford/cs341/url.txt','r') as inputFile:
	for line in inputFile:
		req = requests.get(line[:-1])
		line2 = line.replace("https://stations.fcc.gov//collect/files/","")
		line2 = line2.replace("/","")
		print line2
		outPath = "/Users/xiaoshi/Dropbox/Stanford/cs341/pdfout/"+line2[:-1]
		outFile = open(outPath,'w')
		outFile.write(req.content)
		outFile.close()
