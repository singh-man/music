package com.music.v1;

import java.util.function.BiFunction;

public enum OPREnum {
    ENCODE(IAV.encode),
    VOLUME(IAV.volume);

    private BiFunction<String, String, String> opr;

    OPREnum(BiFunction<String, String, String> biFunction) {
        this.opr = biFunction;
    }

    public BiFunction<String, String, String> getOpr() {
        return opr;
    }

    //    getOpr() {
//
//    }
}
