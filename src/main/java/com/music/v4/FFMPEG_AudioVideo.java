package com.music.v4;

import java.io.File;

public class FFMPEG_AudioVideo implements IAV {

    @Override
    public String volume(File inFile, int volume, File outFile) {
        return String.format(
                "%s -i %s -map 0 -c copy -c:a aac -af \"volume=%sdB\" %s",
                "ffmpeg",
                addDoubleQuotes(inFile.getName()),
                volume,
                addDoubleQuotes(outFile.getName()));
    }

    @Override
    public String encode(File inFile, String resolution, IVideoEncoder encoder, int crf, File outFile) {
        String forAV1 = encoder == FFMPEG_videoEncoder.aom_av1 ? "-preset medium -cpu-used 4" : "";
        String command = String.format(
                "%s -i %s -vf scale=%s -map 0 -c copy -c:v %s %s -crf %s %s",
                "ffmpeg",
                addDoubleQuotes(inFile.getName()),
                resolution,
                encoder.getName(),
                forAV1,
                crf,
                addDoubleQuotes(outFile.getName()));
        return command;
    }
}
