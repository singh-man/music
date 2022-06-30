package com.music;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AVController {

    private IAudioVideo iav = new FFmpeg("ffmpeg", OS.getEnv());

    private List<String> doLoop(List<File> files, Function<File, String> f) {
        List<String> cmds = files.stream()
                .map(f)
                .collect(Collectors.toList());
        return cmds;
    }

    public String encode(String in, String out, int crf, String lib, String resolution) {
        return iav.encode(in, out, crf, lib, resolution);
    }

    public List<String> encode(String dirOrFile, int crf, String lib, String resolution) {
        List<File> files = getFilteredFiles(dirOrFile, IAudioVideo::isValidVideoFileExtension);
        List<String> cmds = files.stream()
                .map(e -> iav.encode(e.getAbsolutePath(), iav.replaceExtension(e,"_" + lib + ".mkv").getAbsolutePath(),
                        crf, lib, resolution))
                .collect(Collectors.toList());
        return cmds;
    }

    public List<String> volume(String dirOrFile, int decible) {
        List<File> files = getFilteredFiles(dirOrFile, IAudioVideo::isValidVideoFileExtension);
        List<String> cmds = files.stream()
                .map(e -> iav.volume(e.getAbsolutePath(), iav.replaceExtension(e,"_v" + decible + ".mkv").getAbsolutePath(),
                        decible))
                .collect(Collectors.toList());
        return cmds;
    }

    public String volume(String in, String out, int decible) {
        return iav.volume(in, out, decible);
    }

    public String importSubtitles(String in, String srt, String language) {
        return iav.importSubtitles(in, iav.replaceExtension(new File(in), "_en.mkv").getAbsolutePath(),
                new IAudioVideo.Subtitles(new File(srt), language));
    }

    public String importSubtitles(String in, String out, String srt, String language) {
        return iav.importSubtitles(in, out, new IAudioVideo.Subtitles(new File(srt), language));
    }

    public String importSubtitles(String in, Map<String, String> i18_fileMap) {
        IAudioVideo.Subtitles[] subtitles = i18_fileMap.entrySet().stream()
                .map(e -> new IAudioVideo.Subtitles(new File(e.getValue()), e.getKey()))
                .collect(Collectors.toList()).toArray(new IAudioVideo.Subtitles[i18_fileMap.size()]);
        return iav.importSubtitles(in, iav.replaceExtension(new File(in), "_en.mkv").getAbsolutePath(), subtitles);
    }

    public List<File> getFilteredFiles(String dirOrFile, Predicate<File> fileFilter) {
        File base = new File(dirOrFile);
        List<File> files = new ArrayList<>();
        files.add(base);
        if(base.isDirectory()) {
            try (Stream<Path> walk = Files.walk(Paths.get(dirOrFile))) {
                files = walk.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(fileFilter)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return files;
    }

}
