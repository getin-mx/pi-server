#! /usr/bin/python3
import os, time, argparse, sys, ast, re
from random import randint

CONTENT_TYPE = "-H \"Content-Type: application/json\""
DEFAULT_APDEVICES = ["gihs-0000","test-0000"]
FAKE_HOSTNAME_PREFIX = "test-"
DEFAULT_ENDPOINT = "/appv2/reportaphs"
DEFAULT_PORT = 0

parser = argparse.ArgumentParser(description='Bombs APHotspots to the given server')
parser.add_argument("-s", "--host",
	help="The IP address or domain to bomb with fake APHotspots reports. Address can include a port number",
	default="staging.getin.mx")
parser.add_argument("-v", "--verbose", help="Output debugging messages", default=False)
parser.add_argument("-p", "--port", help="The port to bomb", default="8080")
parser.add_argument("-e", "--endpoint", help="The endpoint to bomb", default=DEFAULT_ENDPOINT)
parser.add_argument("-d", "--apdevice", help="The hostname to use", default=DEFAULT_APDEVICES[0])
parser.add_argument("-n", "--apdevice_number_range", help="A number of test device to fake for hostname",
	type=int, nargs='+', default=DEFAULT_PORT)

args = parser.parse_args()

if(args.apdevice_number_range == DEFAULT_PORT and (args.apdevice == DEFAULT_APDEVICES[1])) :
	devs = DEFAULT_APDEVICES
else :
	if(not args.apdevice == DEFAULT_APDEVICES[0]) :
		devs = [args.apdevice]
		if(not devs[0].startswith(FAKE_HOSTNAME_PREFIX) or devs[0] in DEFAULT_APDEVICES) :
			sys.exit("Devices names must begin with " +FAKE_HOSTNAME_PREFIX)
	else :
		devs = []
		for p in range(args.apdevice_number_range[0],args.apdevice_number_range[1]) :
			devs.append( "test-" +"{:04d}".format(p))
if(args.port) :
	port = int(args.port)
if(re.search(r':\d+$', args.host)) :
	port = ""
else :
	port = args.port

while True:
	for h in devs :
		hotspot = "{\"mac\":\"" +':'.join(['%02x' %x for x in map(lambda x:randint(0,255),range(6))]) +"\"," + "\"count\":\""
		hotspot = hotspot +str(randint(0,60)) +"\",\"firstSeen\":\"" +str(int(round(time.time() *1000))) +"\",\"lastSeen\":\""
		hotspot = hotspot +str(int(round(time.time() *1000))) +"\",\"signalDB\":\"" + str(-randint(0,200)) +"\"}"
		cmd = "curl " +CONTENT_TYPE +" -X POST -d '{\"hostname\":\"" +h +"\",\"systemDateTime\":\"" +str(int(round(time.time() *1000)))
		cmd = cmd + "\",\"data\":[" +hotspot +"]}' -k http://" +args.host
		if(port) : cmd = cmd +":" +port
		cmd = cmd +args.endpoint
		if(not args.verbose) : cmd = cmd +" >> /dev/null 2>&1"
		else : print(cmd)
		os.system(cmd)
		if(args.verbose) : os.system("echo ")
	time.sleep(1)

