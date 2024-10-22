package com.music.v4;

import java.util.function.Function;

public enum OPREnum {
    ENCODE(e -> new AV_FFMPEG().encode(e)),
    VOLUME(e -> new AV_FFMPEG().volume(e)),

    DONE(x -> x);

    private Function<AVCommand, AVCommand> opr;

    OPREnum(Function<AVCommand, AVCommand> opr) {
        this.opr = opr;
    }

    public Function<AVCommand, AVCommand> getOpr() {
        return opr;
    }
}
