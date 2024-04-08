'''
Created on Dec 19, 2012

@author: emmhssh
'''

import signal
import os

class OsOpps(object):
	
	def killPids(self, pidSet):
		for pid in pidSet:
			self.killPid(pid)
			
	def killPid(self, pid):
		#print pid
		print "killing : " + pid
		os.kill(int(pid), signal.SIGKILL)
