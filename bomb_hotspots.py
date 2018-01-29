#! /usr/bin/python3
import os, sys, time
from random import randint
while True:
	hotspot = "{\"mac\":\"" +':'.join(['%02x' %x for x in map(lambda x:randint(0,255),range(6))]) +"\"," + "\"count\":\"" +str(randint(0,100)) +"\",\"firstSeen\":\"" +str(int(round(time.time() *1000))) +"\",\"lastSeen\":\"" +str(int(round(time.time() *1000))) +"\",\"signalDB\":\"" + str(-randint(0,200)) +"\"}"
	#print(hotspot)
	os.system("curl -H \"Content-Type: application/json\" -X POST -d '{\"hostname\":\"gihs-0000\",\"systemDateTime\":\"\",\"data\":[" +hotspot +"]}' -k http://r2d2.getin.mx:" +sys.argv[1] +"/appv2/reportaphs >> /dev/null 2>&1")
	time.sleep(1)

