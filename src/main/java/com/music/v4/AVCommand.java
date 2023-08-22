package com.music.v4;

import java.util.Objects;

/**
 * Not specially made but this must be treated as Immutable!!
 */
public class AVCommand {
    private String oprFile;
    private String cmd;

    public AVCommand(String infile) {
        this.oprFile = Objects.requireNonNull(infile);
        this.cmd = "";
    }

    public String getCmd() {
        return cmd;
    }

    public String getOprFile() {
        return oprFile;
    }

    public AVCommand newAVCommand(String outFile, String addCmd) {
        AVCommand avCommand = new AVCommand(outFile);
        avCommand.cmd = new StringBuilder()
                .append(this.cmd)
                .append("\n")
                .append(Objects.requireNonNull(addCmd))
                .toString();
        return avCommand;
    }
}
