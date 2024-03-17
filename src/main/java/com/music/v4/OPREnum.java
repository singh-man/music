package com.music.v4;

import java.util.function.Function;

public enum OPREnum {
    ENCODE(new AV_FFMPEG().encode()),
    VOLUME(new AV_FFMPEG().volume()),

    DONE(x -> x);

    private Function<AVCommand, AVCommand> opr;

    OPREnum(Function<AVCommand, AVCommand> opr) {
        this.opr = opr;
    }

    public Function<AVCommand, AVCommand> getOpr() {
        return opr;
    }

    //    getOpr() {
//
//    }
}
