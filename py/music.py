import sys
# print(*sys.path, sep = "\nPYTHONPATH:- ")

# from ffmpeg import av or from ffmpeg.av import * or import ffmpeg.av as av
# the below can be used insted
import ffmpeg.av as av
import utils.directoryUtils as directoryUtils

def finishNow():
    scriptName = "./ffmpeg" + (".sh" if "linux" in sys.platform else ".bat")
    directoryUtils.dumpCmdToScript(finalCmd, "./", scriptName)
    sys.exit("Exiting the program")

# [print(k, v.__name__) for k, v in options.items()]
# options[int(selection)]()

funcs = av.listAllUsefullFunctions()

scriptName = "./tmp" + (".sh" if "linux" in sys.platform else ".bat")
if directoryUtils.isFile(scriptName):
    directoryUtils.removeFile(scriptName)

funcs[100] = (finishNow.__name__, finishNow)
finalCmd = []
doit = True;
while doit:
    directoryUtils.printList(finalCmd)
    # [print(str(k) + ": ", v[0]) for k, v in funcs.items()]
    [print("{0:<3} : {1:>3}".format(str(k), v[0])) for k, v in funcs.items()]
    selection = input("What u want! : ")
    # options.get(selection) doesn't work
    cmd = funcs[int(selection)][1]()
    finalCmd.extend(cmd) if isinstance(cmd, list) else finalCmd.append(cmd)

    scriptName = "./tmp" + (".sh" if "linux" in sys.platform else ".bat")
    cmdsToWrite = cmd if isinstance(cmd, list) else [cmd]
    directoryUtils.writeToFile(cmdsToWrite, scriptName, "a+")
    print(f"Commands are also being dumped to: {scriptName}")
        
    # map(lambda cmd:directoryUtils.execCmd(cmd), cmdList)
    