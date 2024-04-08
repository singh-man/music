package com.music.v3;

import java.util.Objects;

/**
 * Not specially made but this must be treated as Immutable!!
 */
public class AVCommand {

    private String inFile;

    private String outFile;
    private StringBuilder cmd;

    private AVCommand(String outFile, String cmd) {
        this(outFile);
        this.cmd.append(cmd);
    }

    public AVCommand(String inFile) {
        this.inFile = inFile;
        this.outFile = inFile;
        this.cmd = new StringBuilder();
    }

    /**
     * Returns new AVCommand where the previous command is separated with new command by a new line!!
     */
    public AVCommand addCmd(String outFile, String cmd) {
        Objects.requireNonNull(outFile, "Any reason why output file is not provided!");
        Objects.requireNonNull(cmd, "Any reason foe new command to be Null!");
        this.cmd.append("\n").append(cmd);
        return new AVCommand(outFile, this.cmd.toString());
    }

    public String getCmd() {
        return cmd.toString();
    }

    public String getOutFile() {
        return outFile;
    }

    @Override
    public String toString() {
        return getCmd();
    }
}
