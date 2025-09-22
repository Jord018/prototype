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
    public static void Convert(Audio audio) throws EncoderException {
        AudioAttributes audioAtt = new AudioAttributes();
        audioAtt.setBitRate(audio.getBitrate());
        audioAtt.setChannels(audio.getChannels());
        audioAtt.setSamplingRate(audio.getSamplingRate());
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setAudioAttributes(audioAtt);
        Encoder encoder = new Encoder();
        System.out.println("audio bitrate"+ audio.getBitrate());
        if(audio.isVBR()){
            Encoder.addOptionAtIndex(
                    new SimpleArgument(ArgType.OUTFILE, a -> Stream.of("-qscale:a", audio.getVBR())),
                    0
            );}
            encoder.encode(new MultimediaObject(audio.getPath()), audio.getTarget(), attrs);
        }

}

