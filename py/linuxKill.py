'''
Created on Dec 19, 2012

@author: emmhssh
'''

import subprocess
import signal
import os
import sys

port = sys.argv[1]
#print port
output = subprocess.Popen(['lsof', '-t', '-i:'+port], stdout=subprocess.PIPE)

pids = output.communicate()[0]
#print pids

for pid in pids.splitlines():
    print "processing : " + pid
    subOutput = subprocess.Popen(['pgrep', '-P', pid], stdout=subprocess.PIPE)
    pPids = subOutput.communicate()[0]
    dsf = pPids.splitlines()
    if len(dsf) > 0:
        continue
    else:
        print "Killing : " + pid
        try: 
            os.kill(int(pid), signal.SIGKILL)
        except OSError as ex:
            print ex
            continue
        
