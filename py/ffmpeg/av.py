# pytest -s av.py -k test_ffmpeg_incrementVolume

import sys
# sys.path.insert(0, 'utils')
# print(*sys.path, sep = "\nPYTHONPATH:- ")

import pytest

from utils import directoryUtils
from utils.utils import execCmd, execCmd, isLinux, replaceFileExt, dealWithSpacesInFilePathNames, getModifiedTimeStampFromFile


# def getFFmpeg(): return "ffmpeg" if isLinux() else "C:/mani/dev/opt/ffmpeg-20190219-ff03418-win64-static/bin/ffmpeg.exe"
def getFFmpeg(): return "ffmpeg"


def userInput(x, *y):
    # [print(idx, " : ", v) for idx, v in enumerate(y)]
    [print("{idx:<3} : {v:>3}".format(idx=idx, v=v)) for idx, v in enumerate(y)]
    x1 = input(x + ": ")
    if not y:
        return x1
    return y[int(x1)]


def getFileOrFolderList(ext):
    fileOrFolder = input("Enter file(s) [multiple files can be space seperated] or folder path: ")
    if directoryUtils.isDir(fileOrFolder):
        filesList = directoryUtils.findFiles(fileOrFolder, ext)
    else:
        filesList = fileOrFolder.split()
    return filesList


def m4aWithLibfdkAAC():
        return "-c:a libfdk_aac -profile:a aac_he_v2 -b:a 48k"


def toM4aWithLibfdkAAC(iFile, oFile):
    return ' '.join([getFFmpeg(), inFile(iFile), m4aWithLibfdkAAC(), outFile(oFile)])


def inFile(inFile):
    return "-i {}".format(dealWithSpacesInFilePathNames(inFile))


def outFile(outFile):
    return "{}".format(dealWithSpacesInFilePathNames(outFile))


def copyAllStream():
    return "-map 0 -c copy"


def incVolume(db):
    return "-c:a aac -af \"volume={}dB\"".format(db)


def incVolumeFfmpeg(iFile, oFile, db):
    return ' '.join([getFFmpeg(), inFile(iFile), copyAllStream(), incVolume(db), outFile(oFile)])


def m4aWithBuildInAACFfmpeg():
    return "-c:a aac -b:a 48k"


def toM4aWithBuildInAACFfmpeg(iFile, oFile):
    return ' '.join([getFFmpeg(), inFile(iFile), m4aWithBuildInAACFfmpeg(), outFile(oFile)])


def toWavFfmpeg(iFile, oFile):
    return ' '.join([getFFmpeg(), inFile(iFile), outFile(oFile)])


def scaleFfmpeg(resolution):
    return "-vf scale={}".format(resolution)


def to8bitVideo():
    return "-pix_fmt yuv420p"


def encodeFfmpeg(iFile, oFile, encoder, crf, resolution, is8bit):
    #" -i {} -vf scale={} -map 0 -c copy -c:v {} -preset medium -crf {} -c:a aac -strict experimental -b:a 96k {}"
    to8bit = to8bitVideo() if is8bit == 'y' else ""
    return ' '.join([getFFmpeg(), inFile(iFile), "-vf scale={}".format(resolution), copyAllStream(), 
                    "-c:v {} -preset medium -crf {}".format(encoder, crf), to8bit, outFile(oFile)])


def concatFfmpeg(iFile, oFile):
    return ' '.join([getFFmpeg(), "-f concat", inFile(iFile), "-c copy", outFile(oFile)])


def importFfmpeg(inFile, outFile, srtFile):
    inFile = dealWithSpacesInFilePathNames(inFile)
    outFile = dealWithSpacesInFilePathNames(outFile)
    options = {1: getFFmpeg() + " -i {} -sub_charenc UTF-8 -i {} -map 0:v -map 0:a -c copy -map 1 -c:s:0 srt -metadata:s:s:0 language=en {}",
               2: getFFmpeg() + " -i {} -c:a aac -vf subtitles={} {}"}
    if inFile == srtFile or srtFile == "":
        cmd = options[2].format(inFile, inFile, outFile)
    else:
        cmd = options[1].format(inFile, dealWithSpacesInFilePathNames(srtFile), outFile)
    return cmd


def cutFfmpeg(iFile, oFile, startTime, endTime):
    import datetime, time, re
    sh, sm, ss = splitTime(startTime)
    sSeconds = int(datetime.timedelta(hours=int(sh), minutes=int(sm), seconds=int(ss)).total_seconds())
    eh, em, es = splitTime(endTime)
    eSeconds = int(datetime.timedelta(hours=int(eh), minutes=int(em), seconds=int(es)).total_seconds())
    duration = time.strftime('%H:%M:%S', time.gmtime(eSeconds - sSeconds))
    return ' '.join([getFFmpeg(), "-ss {}".format(":".join([sh, sm, ss])), inFile(iFile), 
                     "-t {} -c copy -avoid_negative_ts make_zero".format(duration), outFile(oFile)])


def splitTime(time_):
    """
    If time contains :, it splits by :

    else it splits by group of 2 characters
    """
    import re
    if ":" in time_:
        return re.split(":", time_)
    return re.findall('..?', time_)


def toGifFfmpeg(iFile, oFile):
    return ' '.join([getFFmpeg(), inFile(iFile), "-f gif", outFile(oFile)])


def changeContainer(iFile, oFile):
    return ' '.join([getFFmpeg(), inFile(iFile), "-vcodec copy -acodec copy", outFile(oFile)])


def extractAudio(iFile, oFile):
    return ' '.join([getFFmpeg(), inFile(iFile), "-vn -acodec copy", outFile(oFile)])


def removeAudioTrack(iFile, oFile):
    """
    removes the specified audio track; remember -map -0:a:2 means 0:1 track is removed!!
    """
    return ' '.join([getFFmpeg(), inFile(iFile), "-vcodec copy -acodec copy -map 0 -map -0:a:?", outFile(oFile)])


def ffmpeg_mp3ToM4a_libfdk_aac():
    filesList = getFileOrFolderList("mp3")
    fileMap = {f: replaceFileExt(f, ".m4a") for f in filesList}
    # for f in filesList: fileMap[f] = getOutputFileName(f, "mp3", "m4a") # assigning value to a key
    cmdList = [toM4aWithLibfdkAAC(f1, f2) for f1, f2 in fileMap.items()]
    return cmdList


def ffmpeg_mp3ToM4a():
    filesList = getFileOrFolderList("mp3")
    cmdList = []
    for f in filesList:
        wavFile = replaceFileExt(f, ".wav")
        m4aFile = replaceFileExt(f, ".m4a")
        cmdList.append(toWavFfmpeg(f, wavFile))
        cmdList.append(toM4aWithBuildInAACFfmpeg(wavFile, m4aFile))
    directoryUtils.printList(cmdList)
    print([execCmd(cmd) for cmd in cmdList])
    [directoryUtils.removeFile(replaceFileExt(f, ".wav")) for f in filesList]


def ffmpeg_incVolume():
    filesList = getFileOrFolderList("")
    db = input("Provide db: ")
    fileMap = {f: replaceFileExt(f, "_fv" + db + ".mkv") for f in filesList}
    cmdList = [incVolumeFfmpeg(f1, f2, db) for f1, f2 in fileMap.items()]
    return cmdList


def ffmpeg_encode():
    filesList = getFileOrFolderList("")
    # encoders = {1: "libx264", 2: "libx265", 3: "libaom-av1", 4: "libvpx-vp9"}
    # [print(k, v) for k, v in encoders.items()]
    # encoder = encoders[int(input("Provide encoder: "))]
    encoder = userInput("Provide encoder", "libx264", "libx265", "libaom-av1", "libvpx-vp9")
    crf = userInput("Provide CRF : [0-23-51 least]")
    resolution = userInput("Provide vertical resolution", "-1:-1", "426:240", "-1:360", "852:480", "-1:720", "-1:1080")
    to8bit = userInput("Should convert to 8 bit video [y/n]")
    fileMap = {f: replaceFileExt(f, "_" + encoder + ".mkv") for f in filesList}
    cmdList = [encodeFfmpeg(f1, f2, encoder, crf, resolution, to8bit) for f1, f2 in fileMap.items()]
    return cmdList


def ffmpeg_cut():
    inFile = input("Enter file: ")
    outFile, ext = inFile.rsplit('.', 1)
    sTime = input("Enter start time <hh:mm:ss | hhmmss>: ")
    eTime = input("Enter end time <hh:mm:ss | hhmmss>: ")
    cmd = cutFfmpeg(inFile, outFile + "_cut." + ext, sTime, eTime)
    return cmd


def ffmpeg_concat():
    inFile = input("Enter txt file: ")
    outFile = input("Enter ouput file: ")
    cmd = concatFfmpeg(inFile, outFile)
    return cmd


def ffmpeg_concat2():
    """ffmpeg concat used here doesn't take absolute path in the file so run in the same directory.
    also it removes the generated file.txt i.e. it prints the command to run but run the command as well
    Note: execCmd doesn't like cmds array in ".
    """
    temp_file = "file.txt"
    inFiles = input("Enter space seperated input files name only!: ").split(" ")
    inFiles = ["file \'" + e + "\'" for e in inFiles]
    print(inFiles) 
    directoryUtils.writeToFile(inFiles, temp_file, "w")
    outFile = input("Enter ouput file: ")
    cmd = concatFfmpeg(temp_file, outFile)
    directoryUtils.printList([cmd])
    execCmd(cmd)
    directoryUtils.removeFile(temp_file)


def ffmpeg_import():
    inFile = input("Enter file: ")
    srtFile = input("Provide srt file or leave blank if same as input file: ")
    if srtFile == "" and directoryUtils.isFile(replaceFileExt(inFile, ".srt")):
        srtFile = replaceFileExt(inFile, ".srt")
    outFile = replaceFileExt(inFile, "_en.mkv")
    cmd = importFfmpeg(inFile, outFile, srtFile)
    return cmd


def ffmpeg_toGif():
    filesList = getFileOrFolderList("")
    fileMap = {f: replaceFileExt(f, ".gif") for f in filesList}
    cmdList = [toGifFfmpeg(f1, f2) for f1, f2 in fileMap.items()]
    return cmdList


def ffmpeg_changeContainer():
    filesList = getFileOrFolderList("")
    container = userInput("Chose conatiner to use", "mkv", "mp4")
    fileMap = {f: replaceFileExt(f, "."+container) for f in filesList}
    cmdList = [changeContainer(f1, f2) for f1, f2 in fileMap.items()]
    return cmdList


def ffmpeg_extractAudio():
    filesList = getFileOrFolderList("")
    fileMap = {f: replaceFileExt(f, ".aac") for f in filesList}
    cmdList = [extractAudio(f1, f2) for f1, f2 in fileMap.items()]
    return cmdList


def ffmpeg_removeAudioTrack():
    filesList = getFileOrFolderList("")
    fileMap = {f: replaceFileExt(f, ".mkv") for f in filesList}
    cmdList = [removeAudioTrack(f1, f2) for f1, f2 in fileMap.items()]
    return cmdList


def rename_filesAsPerModifiedTimestamp():
    """"Mainly to be used to rename the iOS camera image and video filenames."""
    files = getFileOrFolderList("jpg JPG MOV HEIC mov heic mp4")
    import os
    cmdList = []
    mSecs = "000"
    previousUsedFileName = {}
    for f in files:
        fname, ext = directoryUtils.getFileNameAndExt(f)
        newFileName = "IMG" + "_" + getModifiedTimeStampFromFile(f) + mSecs
        count = 0
        if newFileName in previousUsedFileName:
            count = int(previousUsedFileName[newFileName]) + 1
        previousUsedFileName[newFileName] = count
        newFileName = newFileName[0:-1] + str(count)
        newFileName = newFileName + ext
        cmdList.append(f + " -> renamed -> " + newFileName)
        # os.rename(f, newFileName)
    return cmdList


def testMp3TpM4a():
    ffmpeg_mp3ToM4a()


def listAllUsefullFunctions():
    """
    works on tuple like (func_name, actual_funtion)
    returns <index of the funtion>:<tuple consisting of (function name and actual funtion)>
    """
    import inspect, sys
    all_functions = inspect.getmembers(sys.modules[__name__], inspect.isfunction)
    # print(all_functions)
    # all_functions = [e for e in all_functions if e[0].find("ffmpeg_") >= 0 or e[0].find("handbrake_") >= 0]
    all_functions = [e for e in all_functions if e[0].find("_") >= 0]
    # print(all_functions)
    funcMap = {all_functions.index(e): e for e in all_functions}
    return funcMap


if __name__ == "__main__":
    filesList = directoryUtils.findFiles("./", ".mp3")
