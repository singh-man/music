package com.music.v4;

import java.util.function.Function;

public enum OPERATIONS {
    ENCODE(e -> useLib().encode(e)),
    VOLUME(e -> useLib().volume(e)),
    CHANGE_CONTAINER(e -> useLib().changeContainer(e)),

    DONE(x -> x);

    private Function<AVCommand, AVCommand> opr;

    OPERATIONS(Function<AVCommand, AVCommand> opr) {
        this.opr = opr;
    }

    public Function<AVCommand, AVCommand> getOpr() {
        return opr;
    }

    private static IAV useLib() {
        return new AV_FFMPEG();
    }
}
