#!/usr/bin/env python3
from sense_emu import SenseHat
import sys
import getopt
sense=SenseHat()
import json
import time
import signal

temperature = 0
humidity = 0
pressure = 0
x = 0
y = 0
mid = 0
# data point
class DataPoint:
	def __init__(self, data):
		self.data = data


try:
	while True:
		
		humidity = sense.get_humidity()
		pressure = sense.get_pressure()
		temperature = sense.get_temperature()
		o = sense.get_orientation()
		pitch = o["pitch"]
		roll = o["roll"]
		yaw = o["yaw"]

		# get json string from joystick
		for event in sense.stick.get_events():
			if event.action in ('pressed', 'held'): 
				x += {'left': -1, 'right': 1}.get(event.direction,0)
				y += {'up': +1, 'down': -1}.get(event.direction, 0)
				mid += {'middle': +1}.get(event.direction, 0)
		
		
		# get json string
		#jsonStr = json.dumps(dp.__dict__)
		jsonStr = json.dumps({"temperature":float(temperature),"pressure":float(pressure),"humidity":float(humidity),"roll":float(roll),"pitch":float(pitch),"yaw":float(yaw),"x":int(x),"y":int(y),"mid":int(mid)})
		#save to file
		try:
			datafile = open("chartdata.json","w")
			datafile.write(jsonStr)
		except:
			print("Write Error")
		finally:
			datafile.close()
		
		#print(jsonStr)
		
		time.sleep(0.1)
		
except KeyboardInterrupt:
	pass
