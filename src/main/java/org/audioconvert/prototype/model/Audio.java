package org.audioconvert.prototype.model;

import java.io.File;

public class Audio {
    private String format;
    private int channels;
    private int samplingRate;
    private int Quality;
    private File target;
    private File path;
    private int Bitrate;
    public boolean isCBR;
    private boolean isVBR;

    public String getVBR() {
        return VBR;
    }

    public void setVBR(String VBR) {
        this.VBR = VBR;
    }

    private String VBR;
    public void setCBR(boolean CBR) {
        isCBR = CBR;
    }

    public void setVBR(boolean VBR) {
        isVBR = VBR;
    }

    public int getBitrate() {
        return Bitrate;
    }

    public void setBitrate(int bitrate) {
        Bitrate = bitrate;
    }


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

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public void setQuality(int quality) {
        Quality = quality;
    }

    public void setTarget(File target) {
        this.target = target;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public boolean isCBR() {
        return isCBR;
    }

    public boolean isVBR() {
        return isVBR;
    }
}