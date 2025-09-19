package org.audioconvert.prototype.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import org.audioconvert.prototype.model.Audio;

import java.io.File;
import java.util.List;

public class Input_Handle {


    int channels = 2;
    int samplingRate = 44100;
    int quality = 32000;
    String format;
    String target;
File file;

    /*
    private void convertFiles() {
        if (droppedFiles.isEmpty()) {
            System.out.println("No files to convert!");
            return;
        }
        
        System.out.println("Starting conversion...");
        for (File file : droppedFiles) {
            System.out.println("Processing: " + file.getAbsolutePath());
            
            // Create Audio object with default parameters
            String fileName = file.getName();
            String format = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            int channels = 2; // Default to stereo
            int samplingRate = 44100; // Default to 44.1kHz
            int quality = 192; // Default bitrate in kbps
            String target = "output"; // Default target directory
            
            Audio audio = new Audio(file, format, channels, samplingRate, quality, target);
            Convert.Convert(audio);
            
            // TODO: Add conversion logic using the Audio object
        }
    }

     */
public void setFormat(String format) {
    this.format = format;

}
public void setChannels(int channels) {
    this.channels = channels;
}
public void setSamplingRate(int samplingRate) {
    this.samplingRate = samplingRate;
}
public void setQuality(int quality) {
    this.quality = quality;
}
public void setTarget(String target) {
    this.target = target;
}
public  void setFile(File file) {
    this.file = file;
}
public String getFormat() {
    return format;
}
public void combine() {
    Audio audio = new Audio(file, format, channels, samplingRate, quality, target);
    Convert.Convert(audio);
}
}
