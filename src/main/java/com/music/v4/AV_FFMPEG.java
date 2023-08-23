package com.music.v4;

import java.io.File;
import java.util.function.Function;

public class AV_FFMPEG implements IAV {

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

    static Function<AVCommand, AVCommand> encode() {
        String resolution = IAV.input("enter resolution", IAV.videoResolution.toArray(new String[IAV.videoResolution.size()]));
        IVideoEncoder encoder = FFMPEG_videoEncoder.getEncoder(IAV.input("enter encoder", FFMPEG_videoEncoder.allNames()));
        int crf = Integer.parseInt(IAV.input("enter crf"));
        return avCommand -> {
            String out = IAV.replaceExtension(new File(avCommand.getOprFile()), "_" + encoder.getName() + ".mkv");
            return avCommand.newAVCommand(out, new AV_FFMPEG().encode(new File(avCommand.getOprFile()), resolution, encoder, crf, new File(out)));
        };
    }

    static Function<AVCommand, AVCommand> volume() {
        int db = Integer.parseInt(IAV.input("enter db to raise"));
        return avCommand -> {
            String out = IAV.replaceExtension(new File(avCommand.getOprFile()), "_v" + db + ".mkv");
            return avCommand.newAVCommand(out, new AV_FFMPEG().volume(new File(avCommand.getOprFile()), db, new File(out)));
        };
    }
}
