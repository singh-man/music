'''
Created on Dec 15, 2012

@author: emmhssh
'''

'hello'

def bad_delete_head(t):
  print t
  print _name
  t = t[1:]
  print t
  
def app(l):
  l.append(5)
  
_name = 'manish'
l = [1,2,3,4]

bad_delete_head(l)
print l
app(l)
print l

def modd(i):
  i = 10
  
i = 5
print i
modd(i)
print i
print __name__
if __name__ == "__main__":
  print 'first'