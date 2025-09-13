package org.audioconvert.prototype.model;

import java.io.File;

public class Audio {

    public String getFormat() {
        return format;
    }

    public int getChannels() {
        return channels;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public int getQuality() {
        return Quality;
    }


    public File getTarget() {
        return target;
    }

    private String format;
    private int channels;
    private int samplingRate;
    private int Quality;
    private File target;

    public File getPath() {
        return path;
    }

    private File path;

    public Audio(String path, String format, int channels, int samplingRate, int Quality, String target) {
        this.path = new File(path);
        this.format = format;
        this.channels = channels;
        this.samplingRate = samplingRate;
        this.Quality = Quality;
        this.target = new File(target);
    }
}