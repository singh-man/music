package com.music.v4;

import java.util.Objects;

public record AVCommand(String oprFile, String cmd) {

    public AVCommand(String infile) {
        this(Objects.requireNonNull(infile), "");
    }

    public AVCommand newAVCommand(String outFile, String addCmd) {
        String cmd = new StringBuilder()
                .append(this.cmd)
                .append("\n")
                .append(Objects.requireNonNull(addCmd))
                .toString();
        return new AVCommand(outFile, cmd);
    }
}
