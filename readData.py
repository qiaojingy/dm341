PATH = "docs.txt"
data = []
with open(PATH, 'rb') as f:
    fields = str(f.readline().replace('\x00', ''))
    fields = fields[2:-2]
    fields = fields.split("|")
    fieldsDict = dict()
    for i, val in enumerate(fields):
        fieldsDict[val] = i
    data = []
    datum = ""
    cur = 0
    for line in f:
        datum += str(line).replace('\x00', '')
        new = len(str(line).replace('\x00', '').split('|'))
        cur += new - 1
        """
        if cur == (len(fields) - 1):
            data.append(datum.split('|'))
            print datum.split('|')[-1]
            cur = 0
            datum = ""
        """
        if datum.split('|')[-1] == "\r\n":
            data.append(datum.split('|'))
            cur = 0
            datum = ""
    f.close()
names = []

def compare(item1, item2):
    if item1[0].lower() < item2[0].lower():
        return -1
    elif item1[0].lower() > item2[0].lower():
        return 1
    else:
        return 0

name_url = []
for d in data:
    name_url.append([d[fieldsDict['url']].split('/')[-2], d[fieldsDict['url']]])
name_url.sort(compare)
for name in name_url:
    print name[0] + '\t' + name[1]
