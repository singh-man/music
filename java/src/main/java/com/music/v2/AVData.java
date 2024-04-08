package com.music.v2;

import java.util.ArrayList;
import java.util.List;

public class AVData {

    private String file;
    private List<String> commands;

    public AVData(String inFile) {
        this.file = inFile;
        this.commands = new ArrayList<>();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<String> getCommands() {
        return commands;
    }

    public boolean addCommand(String cmd) {
        return commands.add(cmd);
    }

}
