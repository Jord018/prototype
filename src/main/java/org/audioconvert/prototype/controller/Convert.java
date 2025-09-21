package org.audioconvert.prototype.controller;

import org.audioconvert.prototype.model.Audio;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.ArgType;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.SimpleArgument;

import java.io.File;
import java.util.stream.Stream;

public class Convert {
    public static void Convert(Audio audio) {
        try {
            // 1. Set up audio attributes
            AudioAttributes audioAtt = new AudioAttributes();
            audioAtt.setQuality(audio.getQuality());
            audioAtt.setChannels(audio.getChannels());
            audioAtt.setSamplingRate(audio.getSamplingRate());

            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setAudioAttributes(audioAtt);

            // Debug output
            System.out.println("\n--- Audio Conversion Settings ---");
            System.out.println("Input: " + audio.getPath().getName());
            System.out.println("Output: " + audio.getTarget().getName());
            System.out.println("Channels: " + audio.getChannels());
            System.out.println("Sample Rate: " + audio.getSamplingRate() + " Hz");
            System.out.println("Bitrate: " + (audioAtt.getBitRate()) + " kbps");
            System.out.println("VBR Mode: " + audio.isVBR());
            System.out.println("Codec: " + audioAtt.getCodec());
            
            // 6. Create encoder and perform conversion
            Encoder encoder = new Encoder();
            if(audio.isVBR()){
                encoder.addOptionAtIndex(
                        new SimpleArgument(ArgType.OUTFILE, a -> Stream.of("-qscale:a", audio.getVBR())),
                        0
                );}
            System.out.println("\nStarting conversion...");
            encoder.encode(new MultimediaObject(audio.getPath()), audio.getTarget(), attrs);
        } catch (
                EncoderException e) {
            throw new RuntimeException(e);
        }
    }
}
