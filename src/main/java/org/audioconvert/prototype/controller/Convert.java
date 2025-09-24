package org.audioconvert.prototype.controller;

import org.audioconvert.prototype.model.Audio;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.ArgType;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.SimpleArgument;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.progress.EncoderProgressListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Convert {
    public static void convertWithProgress(Audio audio, Consumer<Double> progressCallback) throws EncoderException {
        AudioAttributes audioAtt = new AudioAttributes();
        audioAtt.setBitRate(audio.getBitrate());
        audioAtt.setChannels(audio.getChannels());
        audioAtt.setSamplingRate(audio.getSamplingRate());
        
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setAudioAttributes(audioAtt);
        
        Encoder encoder = new Encoder();
        
        if (audio.isVBR()) {
            Encoder.addOptionAtIndex(
                    new SimpleArgument(ArgType.OUTFILE, a -> Stream.of("-qscale:a", audio.getVBR())),
                    0
            );
        }
        
        // Get source duration for progress calculation
        MultimediaObject source = new MultimediaObject(audio.getPath());
        long duration = 0;
        try {
            MultimediaInfo info = source.getInfo();
            duration = info.getDuration() / 1000; // Convert to seconds
        } catch (Exception e) {
            throw new RuntimeException("Could not get media info: " + e.getMessage(), e);
        }
        
        final long finalDuration = duration;
        AtomicBoolean isProcessing = new AtomicBoolean(true);
        
        EncoderProgressListener listener = new EncoderProgressListener() {
            @Override
            public void sourceInfo(MultimediaInfo info) {}
            
            @Override
            public void progress(int permil) {
                // permil is 0-1000 representing 0-100%
                double progress = permil / 1000.0;
                progressCallback.accept(progress);
            }
            
            @Override
            public void message(String message) {
                System.out.println("Encoder message: " + message);
            }
        };
        
        try {
            encoder.encode(source, audio.getTarget(), attrs, listener);
        } finally {
            isProcessing.set(false);
        }
    }
    
    // Keep the old method for backward compatibility
    public static void Convert(Audio audio) throws EncoderException {
        convertWithProgress(audio, progress -> {
            // Default empty progress handler for backward compatibility
        });
    }

}

