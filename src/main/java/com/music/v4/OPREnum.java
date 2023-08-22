package com.music.v4;

import java.util.function.Function;

public enum OPREnum {
    ENCODE(AVMain.encode()),
    VOLUME(AVMain.volume()),

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
