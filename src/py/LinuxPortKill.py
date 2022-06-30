'''
Created on Dec 19, 2012

@author: emmhssh
'''

import subprocess
import sys
from OsOpps import OsOpps

'''
'lsof -i:8080 | grep java | awk '{print $2}'
Has a potential to kill app with status CLOSE_WAIT may be an eclipse application 
'''

class LinuxPortKill(OsOpps):
	
	def main(self):
		try:
			port = sys.argv[1]
			self.userData(port)
		except IndexError:
			port = raw_input("Enter Port:  ")
			self.userData(port)
			
	
	def userData(self, port):
		pidSet = self.findPidsByPort(port)
		self.killPids(pidSet)
			
	def findPidsByPort(self, port):
		#print port
		cmd = ['lsof', '-i:'+port]
		output = subprocess.Popen(cmd, stdout=subprocess.PIPE)
	
		pids = output.communicate()[0]
		#print pids + "\ndone"
	
		pidSet = set()
		for pid in pids.splitlines():
			if 'java' in pid:
				if 'ESTABLISHED' in pid or 'LISTEN' in pid:
					pidSet.add(pid.split()[1]) # split by space and take second string in array
				
		return pidSet
	
if __name__ == "__main__":
	LinuxPortKill().main()
