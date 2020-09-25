#!/usr/bin/python3
import json
from sense_hat import SenseHat

sense = SenseHat()

filename = "led_display.json";


if filename:
	with open(filename, 'r') as f:
		ledDisplayArray = json.load(f)


#DEBUG: print results
for led in ledDisplayArray:
	sense.set_pixel(led[0], led[1], led[2], led[3], led[4])
	#print("(x:"+str(led[0])+ "y:"+str(led[1])+r:"+str(led[2])+"g:"+str(led[3])+"b:"+str(led[4]))
