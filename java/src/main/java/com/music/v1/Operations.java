package com.music.v1;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Operations {
    ENCODE(AVTerminalViewModel.INSTANCE.encode()),
    VOLUME_INC(AVTerminalViewModel.INSTANCE.volumes()),
    IMPORT(AVTerminalViewModel.INSTANCE.importSubtitless()),
    DONE(cmdsR -> cmdsR);

    private final Function<AVTerminalViewModel.CmdsR, AVTerminalViewModel.CmdsR> func;

    Operations(Function<AVTerminalViewModel.CmdsR, AVTerminalViewModel.CmdsR> func) {
        this.func = func;
    }

    public Function<AVTerminalViewModel.CmdsR, AVTerminalViewModel.CmdsR> getFunc() {
        return func;
    }

    public List<String> getOperations() {
        return Stream.of(Operations.values()).map(e -> e.toString()).collect(Collectors.toList());
    }

}
