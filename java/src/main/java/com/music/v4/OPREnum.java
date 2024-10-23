package com.music.v4;

import java.util.function.Function;

public enum OPREnum {
    ENCODE(e -> getLib().encode(e)),
    VOLUME(e -> getLib().volume(e)),
    CHANGE_CONTAINER(e -> getLib().changeContainer(e)),

    DONE(x -> x);

    private Function<AVCommand, AVCommand> opr;

    OPREnum(Function<AVCommand, AVCommand> opr) {
        this.opr = opr;
    }

    public Function<AVCommand, AVCommand> getOpr() {
        return opr;
    }

    private static IAV getLib() {
        return new AV_FFMPEG();
    }
}
