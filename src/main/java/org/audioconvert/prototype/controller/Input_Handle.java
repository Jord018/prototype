package org.audioconvert.prototype.controller;

import org.audioconvert.prototype.model.File_Audio;

import java.io.FileInputStream;

public class Input_Handle {
    public static void handleInput() {
    File_Audio file = new File_Audio(1024, "C:/audio/source.wav");

        if (file.size > 1024) {
            System.out.println("File size is too large");
    }
}
}