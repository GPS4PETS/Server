#!/usr/bin/env python3

import sys
import math
import urllib
import http.client as httplib
import time
import random
import datetime

id = '123456789'
server = 'localhost:5055'

step = 0.001

file_path = '/opt/traccar-git/tools/track.pos'

with open(file_path, 'r') as file:
    file_content = file.read()

file_split = file_content.splitlines();

timest = [];
waypoints = [()];
altitude = [];
speed = [];
attributes = [];
battery = [];
steps = [];
distance = [];
totalDistance = [];
motion = [];
charge = [];

for temp in file_split:
    tmp = temp.split(";")
    timest.append(tmp[1]);
    waypoints.append((float(tmp[2]), float(tmp[3])))
    altitude.append(float(tmp[4].split(" ")[0]));
    speed.append(float(tmp[5].split(" ")[0]));
    attributes = tmp[6];
    atttmp = attributes.split("  ");
    for atmp in atttmp:
        atspl = atmp.split("=");
        if atspl[0] == "steps":
            steps.append(atspl[1]);
        if atspl[0] == "batteryLevel":
            battery.append(atspl[1]);
        if atspl[0] == "distance":
            distance.append(atspl[1]);
        if atspl[0] == "totalDistance":
            totalDistance.append(atspl[1]);
        if atspl[0] == "motion":
            motion.append(atspl[1]);
        if atspl[0] == "charge":
            charge.append(atspl[1]);


points = []

for i in range(0, len(waypoints)):
    if len(waypoints[i]) == 2 and len(waypoints[(i + 1) % len(waypoints)]) == 2:
        (lat1, lon1) = waypoints[i]
        (lat2, lon2) = waypoints[(i + 1) % len(waypoints)]
        length = math.sqrt((lat2 - lat1) ** 2 + (lon2 - lon1) ** 2)
        count = int(math.ceil(length / step))
        for j in range(0, count):
            lat = lat1 + (lat2 - lat1) * j / count
            lon = lon1 + (lon2 - lon1) * j / count
            points.append((lat, lon))

def send(conn, lat, lon, altitude, course, speed, battery, steps, charge, distance, totalDistance):
    params = (('id', id), ('timestamp', int(time.time())), ('lat', lat), ('lon', lon), ('altitude', altitude), 
                ('bearing', course), ('speed', speed), ('batt', battery), ('steps', steps), ('charge', charge),
                ('distance', distance), ('totalDistance', totalDistance));

    conn.request('GET', '?' + urllib.parse.urlencode(params));
    conn.getresponse().read();
    print('GET', '?' + urllib.parse.urlencode(params));

def course(lat1, lon1, lat2, lon2):
    lat1 = lat1 * math.pi / 180
    lon1 = lon1 * math.pi / 180
    lat2 = lat2 * math.pi / 180
    lon2 = lon2 * math.pi / 180
    y = math.sin(lon2 - lon1) * math.cos(lat2)
    x = math.cos(lat1) * math.sin(lat2) - math.sin(lat1) * math.cos(lat2) * math.cos(lon2 - lon1)
    return (math.atan2(y, x) % (2 * math.pi)) * 180 / math.pi

index = 0

conn = httplib.HTTPConnection(server)

while True:
    print(str(index) + " from " + str(len(points)))
    if len(points[index]) == 2 and len(points[(index + 1)]) == 2:
        (lat1, lon1) = points[index % len(points)]
        (lat2, lon2) = points[(index + 1) % len(points)];
        send(conn, lat1, lon1, altitude[index], course(lat1, lon1, lat2, lon2), speed[index], battery[index], steps[index], charge[index], distance[index], totalDistance[index])
    if index < (len(points) - 1):
        zeit = time.mktime(datetime.datetime.strptime(timest[index], "%Y-%m-%d %H:%M:%S").timetuple());
        nextzeit = time.mktime(datetime.datetime.strptime(timest[index + 1], "%Y-%m-%d %H:%M:%S").timetuple())
        zeitdiff = nextzeit - zeit;
        print (zeitdiff);
        time.sleep(zeitdiff);
    index += 1
