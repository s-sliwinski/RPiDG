#!/usr/bin/python3
import cgi, cgitb
import json

print("Content-Type: application/json\r\n\r\n")

storage = cgi.FieldStorage()
jsonStr = storage.getvalue('jsonStr')

f = open("config-data.json", "w")
f.write(str(jsonStr))
f.close()
