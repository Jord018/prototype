package org.audioconvert.prototype.controller;

import org.audioconvert.prototype.model.Audio;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;

public class Convert {
    public static void Convert(Audio audio) {
        AudioAttributes audioAtt = new AudioAttributes();
        audioAtt.setQuality(audio.getQuality());
        audioAtt.setChannels(audio.getChannels());
        audioAtt.setSamplingRate(audio.getSamplingRate());
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("mp3");
        attrs.setAudioAttributes(audioAtt);
        Encoder encoder = new Encoder();
        try {
            encoder.encode(new MultimediaObject(audio.getPath()), audio.getTarget(), attrs);
        } catch (
                EncoderException e) {
            throw new RuntimeException(e);
        }
    }
}
