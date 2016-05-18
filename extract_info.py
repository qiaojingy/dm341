import codecs
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

target = codecs.open("/Users/xiaoshi/Dropbox/Stanford/cs341/final/info/49157Political File2014FederalUS SenateKay HagenWCCB-TV_NCSEN14_647_8020 1 (14098586445415)_out2",'w','utf-8')
count = 0
rep_count = 1000
candidate_org = ""
org_find = False
cand_find = False
cur_line = ""
prev_line = ""
rep_find = False
with open('/Users/xiaoshi/Dropbox/Stanford/cs341/final/target/49157Political File2014FederalUS SenateKay HagenWCCB-TV_NCSEN14_647_8020 1 (14098586445415)_out2','r') as txtFile:
	for line in txtFile:
		prev_line = cur_line
		cur_line = line
		if (line.startswith("I.") or line.startswith("I,") or line.startswith("1,") or line.startswith("1.") or line.startswith("I ")) and rep_find==False:
			target.write("representative: "+line[2:])
			rep_count = count
			rep_find = True
		elif count > rep_count and re.search('[a-zA-Z]', line) and cand_find == False:
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
				line_final = ""
				line_p2 = line[true_start:]
				#deal with ", a legally" NAB form PB-17
				res_p2 = line_p2.search("a leg",line_p2)
				if res_p2:
					str_p2 = res_p2.group(0)
					line_p2 = line_p2[:line_p2.find(str_p2)]
					res_p22 = line_p2.search(".*,",line_p2)
					if res_p22:
						line_p2 = res_p22.group(0)
				# For color pdf, the name of organization may be splited into two lines
				if re.search('[a-zA-Z]', prev_line):
					line_p1 = prev_line
					line_final = merge_org(line_p1,line_p2)
				else:
					line_final = line_p2
				target.write("organization: "+line_final)
				org_find = True
		count = count+1
	if org_find == False:
		#judge this is a name of a org
		target.write("candidate organization: "+candidate_org)
txtFile.close()
target.close()