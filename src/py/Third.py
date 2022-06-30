'''
Created on Dec 15, 2012

@author: emmhssh
'''

'hello'

def myMeth1(l):
  return 1, 2, 3
  
def sumAll(*p):
  temp = 0
  for i in p:
    temp += i
  print temp
  return temp
  
def zipExam():
  print 'm'
  
if __name__ == '__main__':
  l = [1,2,5]
  a,b,c = myMeth1(l)
  print a,b,c
  print sumAll(1,2,3)
  zipExam()