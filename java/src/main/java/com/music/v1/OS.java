package com.music.v1;

import java.util.stream.Stream;

public enum OS {
    LINUX,
    WIN,
    MAC;

    public static OS getEnv() {
        return Stream.of(OS.values())
                .filter(e -> System.getProperty("os.name").toLowerCase().contains(e.name().toLowerCase()))
                .findFirst().get();
    }


}
