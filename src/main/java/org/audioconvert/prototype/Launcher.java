package org.audioconvert.prototype;

import javafx.application.Application;
import org.audioconvert.prototype.controller.Convert;
import org.audioconvert.prototype.model.Audio;
import ws.schild.jave.encode.AudioAttributes;

import java.io.File;

public class Launcher {
    public static void main(String[] args) {
        Audio audio = new Audio("C:/audio/source.wav", "mp3", 2, 44100, 9,"C://audio/converted.mp3");
        Convert.audio(audio);

    }
}
