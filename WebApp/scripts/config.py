#!/usr/bin/python3
import cgi, cgitb
import json
import sys
import os


content_len = int(os.environ["CONTENT_LENGTH"])
req_body = sys.stdin.read()
myjson = json.loads(req_body)


#data modification
myjson["ip"] = "do sth"


print('Content-Type: application/json\r\n\r\n')

open with("config.json", "w") as file:
	file.write(json.dumps(myjson))
print(json.dumps(myjson))




 

