from threading import Thread
import time
import random
from Queue import Queue
from Queue import Empty
import utils.directoryUtils

queue = Queue(10)
producerFinished = False

def execCmd(cmd):
    import subprocess
    #subprocess.call(cmd)
    p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print(err)
    
def convertToFormat(file1, file2, cmd):
    formedCmd = cmd%(file1, file2)
    print(formedCmd)
    execCmd(formedCmd.split(" "))
    
def getOutputFileName(inputFile, fExt, tExt):
    name, ext = inputFile.split(fExt)
    outputFile = name+tExt
    #print outputFile, inputFile
    return outputFile

def convertMp3ToWav(fileList, mp3_wavDec):
    for f in fileList:
        convertToFormat(getOutputFileName(f, ".mp3", ".wav"), f, mp3_wavDec)
        
def convertWavToM4a(fileList, wav_m4aEnc):
    for f in fileList:
        convertToFormat(getOutputFileName(f, ".wav", ".m4a"), f, wav_m4aEnc)
        
def convertWavToMp3(fileList, wav_mp3):
    for f in fileList:
        convertToFormat(f, getOutputFileName(f, ".wav", ".mp3"), wav_mp3)
        
class WAVProducer(Thread):
    def run(self):
        global queue, producerFinished
        fileList = directoryUtils.recursivelyFindFiles([], self.dirPath, ['mp3'], directoryUtils.toRecurse())
        for f in fileList:
            outputFile = getOutputFileName(f, ".mp3", ".wav")
            convertToFormat(outputFile, f, mp3_wavDec)
            queue.put(outputFile)
        
        producerFinished = True;

    def main(self, dirPath, mp3_wavDec):
        self.dirPath = dirPath
        self.mp3_wavDec = mp3_wavDec
        self.start()


class WAVConsumer(Thread):
    def run(self):
        global queue, producerFinished
        while True:
            if producerFinished == True and queue.empty():
                break;
            else:
                try:
                    f = queue.get(block=True, timeout=5)
                except Empty:
                    continue
                if f is not None:
                    convertToFormat(getOutputFileName(f, ".wav", ".m4a"), f, wav_m4aEnc)
                    directoryUtils.removeFile(f)
                queue.task_done()

    def main(self, wav_m4aEnc):
        self.wav_m4aEnc = wav_m4aEnc
        self.start()

if __name__ == "__main__":
    wav_m4aEnc = "D:/mani/dev/opt/NeroAACCodec-1.5.1/win32/neroAacEnc.exe -q 0.65 -hev2 -of %s -if %s"
    mp3_wavDec = "D:/mani/dev/opt/mpg123-1.21.0-x86-64/mpg123.exe -w %s %s"
    wav_mp3 = "lame -b 224 %s %s"
    dirPath = raw_input('Welcome to Song Convertor\n Please provide source directory for songs - must end with / : ')
    print("Convert : \n 1) To WAV \n 2) To M4A \n 3) Both 1 and 2 \n 4) To MP3")
    choice = input("Choose your option: ")
    if choice == 1:
        fileList = directoryUtils.recursivelyFindFiles([], dirPath, ['mp3'], directoryUtils.toRecurse())
        convertMp3ToWav(fileList, mp3_wavDec)
    elif choice == 2:
        fileList = directoryUtils.recursivelyFindFiles([], dirPath, ['wav'], directoryUtils.toRecurse())
        convertWavToM4a(fileList, wav_m4aEnc)
    elif choice == 3:
        WAVProducer().main(dirPath, mp3_wavDec)
        WAVConsumer().main(wav_m4aEnc)
    elif choice == 4:
        fileList = directoryUtils.recursivelyFindFiles([], dirPath, ['wav'], directoryUtils.toRecurse())
        convertWavToMp3(fileList, wav_mp3)
    
#/home/emmhssh/man/Music/test/
