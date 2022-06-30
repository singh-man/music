import sys


def replaceFileExt(inputFile, tExt):
    import re
    name, ext = re.split("\\.(?=[^\\.]+$)", inputFile)
    outputFile = name + tExt
    # print(outputFile, inputFile)
    return outputFile


def isLinux(): return "linux" in sys.platform


def printList(myList, decorator):
    print(*list, sep="\n{0}: ".format(decorator))


def execCmd(cmd):
    import subprocess
    # subprocess.call(cmd.split(" "))
    p = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print(err)


def execCmd2(cmd):
    import subprocess
    p = subprocess.run(cmd.split(" "), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    print("Err: " + p.stderr.decode('utf-8'))
    print("Out: " + p.stdout.decode('utf-8'))


def dealWithSpacesInFilePathNames(name):
    return "".join(["\"", name, "\""])
    

'''
@params file path in string format
Get file modifie date-time from file metadata in 
YYYYMMDD_HHMMSS format.
'''
def getModifiedTimeStampFromFile(file):
    # import pathlib, datetime
    # file = pathlib.Path(file)
    # assert file.exists(), f'No such file: {file}'
    # mtime = datetime.datetime.fromtimestamp(file.stat().st_mtime)
    # return mtime.strftime(("%Y%m%d_%H%M%S"))
    import os, time
    assert os.path.exists(file)
    modificationTime = time.strftime('%Y%m%d_%H%M%S', time.localtime(os.path.getmtime(file)))
    return modificationTime

def ackermann(m, n):
    # I assume that you check that n and m are non-negative before you run this
    if m == 0:
        return n + 1
    elif n == 0:
        return ackermann(m - 1, 1)
    else:
        return ackermann(m - 1, ackermann(m, n - 1))


def test_ackermann():
    print(ackermann(4, 0))
