package com.music.v3;

import java.util.function.Function;

public enum OPREnum {
    ENCODE(x -> IAV.encode(x)),
    VOLUME(x -> IAV.volume(x)),

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
