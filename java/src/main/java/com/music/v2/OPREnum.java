package com.music.v2;

import java.util.function.Function;

public enum OPREnum {
    ENCODE(x -> IAV.encode(x)),
    VOLUME(x -> IAV.volume(x)),

    DONE(x -> x);

    private Function<AVData, AVData> opr;

    OPREnum(Function<AVData, AVData> opr) {
        this.opr = opr;
    }

    public Function<AVData, AVData> getOpr() {
        return opr;
    }

    //    getOpr() {
//
//    }
}
