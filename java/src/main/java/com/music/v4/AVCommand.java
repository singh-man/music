package com.music.v4;

import java.util.List;
import java.util.Objects;

public class AVCommand {
    private String oprFile;
    private List<String> cmd;

    public AVCommand(String oprFile, List<String> cmd) {
        this.oprFile = Objects.requireNonNull(oprFile);
        this.cmd = Objects.requireNonNull(cmd);
    }

    public void addCmd(String cmd) {
        this.cmd.add(cmd);
    }

    public void setNewInFile(String inFile) {
        this.oprFile = inFile;
    }

    public String oprFile() {
        return oprFile;
    }
}
