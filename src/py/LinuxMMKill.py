'''
Created on Jan 17, 2013

@author: emmhssh
'''

import subprocess
import sys
from LinuxPortKill import LinuxPortKill


class LinuxMMKill(LinuxPortKill):
	
	PROP = '-Dcmg.component.name='
	
	def main(self):
		try:
			value = sys.argv[1]
			int(value)
			pidSet = self.findPidsByPort(value)
			self.killPids(pidSet)
		except ValueError:
			print 'Either provide and int value or nothing'
		except IndexError:
			selection = raw_input('Kill \n\t 1. Manager\n\t 2. Online Server\n\t 3. Tracer \nChoice: ')
			options = {
			0 : self.findRunningApps,
			1 : self.manager,
			2 : self.onlineServer,
			3 : self.tracer}
			try:
				options[int(selection)]()
			except KeyError:
				print 'Check Choice'
		
	def findPidByPropAndValue(self, prop, value):
		'''
		cmd = 'ps -ef | grep '+value
		#output = subprocess.call(cmd, shell=True)
		#output = os.check_output(cmd)
		print output
		'''
		
		p1 = subprocess.Popen(["ps", "-ef"], stdout=subprocess.PIPE)
		p2 = subprocess.Popen(["grep", value], stdin=p1.stdout, stdout=subprocess.PIPE)
		#print p2
		pids = p2.communicate()[0]
		pidSet = set()
		for pid in pids.splitlines():
			if prop in pid and value in pid:
					pidSet.add(pid.split()[1])
					
		return pidSet
		
	def findRunningApps(self):
		print 'Wrong Choice for finding running apps'
	
	def killProcess(self, prop, value):
		pidSet = self.findPidByPropAndValue(prop, value)
		self.killPids(pidSet)
		
	def manager(self):
		value = raw_input('Manager name<MANAGER>: ')
		if len(value) == 0:
			value = 'MANAGER'
		self.killProcess(self.PROP, value)
	
	def onlineServer(self):
		value = raw_input('Online Server name<SERVER>: ')
		if len(value) == 0:
			value = 'SERVER'
		self.killProcess(self.PROP, value)
	
	def tracer(self):
		value = raw_input('Tracer name<TRACER>: ')
		if len(value) == 0:
			value = 'TRACER'
		self.killProcess(self.PROP, value)

if __name__ == "__main__":
	LinuxMMKill().main()
