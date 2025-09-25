package org.audioconvert.prototype.model;

import java.io.File;

public class Audio {
    private String format = "mp3";
    private int channels = 1;
    private int samplingRate = 44100; // 44.1 kHz in Hz
    private int Quality = 5;
    private File target;
    private File path;
    private int Bitrate = 192000; // 192 kbps in bps
    public boolean isCBR = true;
    private boolean isVBR = false;
    private String VBR = "1";
    
    public Audio() {
        // Default constructor with default values
    }

    public String getVBR() {
        return VBR;
    }

    public void setVBR(String VBR) {
        this.VBR = VBR;
    }
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