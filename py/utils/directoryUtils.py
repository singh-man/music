# pytest -s directoryUtils.py -k test_printFiles

import sys
# print(*sys.path, sep = "\nPYTHONPATH:- ")

import os

from utils.utils import execCmd, execCmd2

# import pytest

def getFileNameAndExt(fName):
    fileName, fileExtension = os.path.splitext(fName)
    return fileName, fileExtension


def toRecurse():
    recurse = input('Include Subdirectories Y/N : ')
    choice = {"Y": True, "y": True}
    return choice.__contains__(recurse)


def dumpCmdToScript(cmdList, toPath, fileName):
    # toPath = toPath.replace("/", ("/" if "linux" in sys.platform else "\\"))
    option = input("Should I write to file or execute them - yes, append, no, execute - !... (y/a/n/e) : ")
    script = toPath + fileName
    options = {"y" : "w",
                "a" : "a+"}
    if option in options:
        writeToFile(cmdList, script, options[option])
    elif option == "e":
        for c in cmdList: execCmd2(c)
    else:
        return
    print("Run this in shell: " + script)


def writeToFile(data, toFile, mode):
    fo = open(toFile, mode)
    for c in data: fo.write("{0}\n".format(c))


def removeFile(f): os.remove(f)


def removeFilesFromDir(dirPath):
    ext = input('File extension(s) (space seperated) to remove or press enter if you want to remove all files. : ')
    exts = ext.split(" ")
    fileList = recursivelyFindFiles([], dirPath, exts, toRecurse())
    map(lambda f: removeFile(f), fileList)


def recursivelyFindFiles(fileList, dirPath, exts, toRecurse=False):
    dirlist = os.listdir(dirPath)
    for f in dirlist:
        inputFilePath = dirPath + "/" + f
        if toRecurse and isDir(inputFilePath):
            recursivelyFindFiles(fileList, inputFilePath + "/", exts, toRecurse)
        elif os.path.isfile(inputFilePath):
            fName, fExt = os.path.splitext(inputFilePath)  # fExt is like .wav, .mp3; hence removing .
            if fExt[1:] in exts or '' in exts:
                fileList.append(inputFilePath)
    return fileList


def findFiles(dirPath, ext=""):
    return recursivelyFindFiles([], dirPath, ext.split(" "), True)


def isDir(path):
    return os.path.isdir(path)


def isFile(path):
    return os.path.isfile(path)


def printList(fileList):
    print("Printing list below::->")
    print('\n'.join(fileList))


def test_findFiles():
    print("test_printList")
    files = findFiles("/mnt/d/mani/video/", "mkv")
    printList(files)


def test_printMe():
    print("test_printMe")


if __name__ == "__main__":
    dirPath = input('Please provide directory path - must end with / : ')
    '''
    print (" 1) To List all files in a directory or any specifc files in a directory \n 2) Remove files from directory/subdirectory")
    choice = input("Choose your option: ")
    if choice == 1:
        printList(findFiles())
    elif choice == 2:
        removeFilesFromDir(dirPath)
    '''
