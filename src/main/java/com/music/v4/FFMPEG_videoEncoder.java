package com.music.v4;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FFMPEG_videoEncoder implements IVideoEncoder {

    x264("libx264"),
    x265("libx265"),
    aom_av1("libaom-av1"),
    vp9("libvpx-vp9");
    private String name;

    FFMPEG_videoEncoder(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    static String[] allNames() {
        FFMPEG_videoEncoder[] values = FFMPEG_videoEncoder.values();
        return Stream.of(values).map(e -> e.getName()).collect(Collectors.toList()).toArray(new String[values.length]);
    }

    static IVideoEncoder getEncoder(String name) {
        return Stream.of(FFMPEG_videoEncoder.values()).filter(e -> e.ordinal() == Integer.parseInt(name)).findFirst().orElse(x265);
    }
}
