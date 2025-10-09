package org.audioconvert.prototype.controller;

import org.audioconvert.prototype.model.Audio;
import org.audioconvert.prototype.exception.SameFileTypeException;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.ArgType;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.SimpleArgument;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.progress.EncoderProgressListener;
import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Convert {
    private static final int BITRATE_READ_ERROR = -1;

    /**
     * Gets the file extension in lowercase (without the dot)
     */
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot == -1 ? "" : name.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Gets the audio bitrate of the source file
     *
     * @param file The audio file to check (supports MP3)
     * @return The bitrate in kbps, or BITRATE_READ_ERROR if unable to determine
     */
    private static int getAudioBitrate(File file) {
        String fileName = file.getName().toLowerCase();

        // Only MP3 files are supported for bitrate reading in this implementation
        if (!fileName.endsWith(".mp3")) {
            return BITRATE_READ_ERROR;
        }

        try {
            Mp3File mp3file = new Mp3File(file);
            if (mp3file.hasId3v1Tag() || mp3file.hasId3v2Tag() || mp3file.hasCustomTag()) {
                return (int) Math.round(mp3file.getBitrate() / 1000.0);
            }
            return BITRATE_READ_ERROR;
        } catch (Exception e) {
            System.err.println("Could not read bitrate for MP3 file: " + file.getName() + ", error: " + e.getMessage());
            return BITRATE_READ_ERROR;
        }
    }

    /**
     * Gets the target bitrate from the Audio object
     *
     * @param audio The Audio object containing conversion settings
     * @return The target bitrate in kbps, or BITRATE_READ_ERROR if not set
     */
    private static int getTargetBitrate(Audio audio) {
        try {
            // Convert from bps to kbps and round to nearest integer
            return (int) Math.round(audio.getBitrate() / 1000.0);
        } catch (Exception e) {
            System.err.println("Could not get target bitrate: " + e.getMessage());
            return BITRATE_READ_ERROR;
        }
    }

    public static void convertWithProgress(Audio audio, Consumer<Double> progressCallback)
            throws EncoderException, SameFileTypeException, IOException {

        File sourceFile = audio.getPath();
        File targetFile = audio.getTarget();
        boolean sameFile = sourceFile.getCanonicalPath().equals(targetFile.getCanonicalPath());
        File tempOutput = null;

        try {
            // If source and target are the same, create a temporary file for conversion
            if (sameFile) {
                String tempSuffix = ".tmp." + getFileExtension(sourceFile);
                tempOutput = File.createTempFile("convert_", tempSuffix, sourceFile.getParentFile());
                tempOutput.deleteOnExit();
                audio.setTarget(tempOutput);

                System.out.println("Source and target are the same. Using temporary file: " + tempOutput.getAbsolutePath());
            }

            // Get file extensions
            String sourceExt = getFileExtension(sourceFile);
            String targetExt = getFileExtension(audio.getTarget());

            // Only check bitrate if extensions are the same and not the same file
            if (sourceExt.equalsIgnoreCase(targetExt) && !sameFile) {
                int sourceBitrate = getAudioBitrate(sourceFile);
                int targetBitrate = getTargetBitrate(audio);

                // If we successfully read both bitrates and they're the same, skip conversion
                if (sourceBitrate != BITRATE_READ_ERROR &&
                        targetBitrate != BITRATE_READ_ERROR &&
                        sourceBitrate == targetBitrate) {

                    throw new SameFileTypeException(String.format(
                            "Source and target have same format (.%s) and bitrate (%d kbps)",
                            sourceExt, sourceBitrate));
                }

                // If we get here, either bitrate couldn't be read or they're different - proceed with conversion
                System.out.printf("Converting .%s (%d kbps) to .%s (%d kbps)%n",
                        sourceExt, sourceBitrate, targetExt, targetBitrate);
            }
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
                public void sourceInfo(MultimediaInfo info) {
                }

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
    // Keep the old method for backward compatibility
    /*public static void Convert(Audio audio) throws EncoderException, SameFileTypeException, IOException {
        File sourceFile = audio.getPath();
        File targetFile = audio.getTarget();
        boolean sameFile = sourceFile.getCanonicalPath().equals(targetFile.getCanonicalPath());
        File tempOutput = null;
        
        try {
            // Create a temporary file if source and target are the same
            if (sameFile) {
                String tempSuffix = ".tmp." + getFileExtension(sourceFile);
                tempOutput = File.createTempFile("convert_", tempSuffix, sourceFile.getParentFile());
                tempOutput.deleteOnExit();
                audio.setTarget(tempOutput);
            }
            
            // Perform the conversion
            convertWithProgress(audio, progress -> {
                // Default empty progress handler for backward compatibility
            });
            
            // If we used a temporary file and conversion was successful, replace the original
            if (sameFile && tempOutput != null && tempOutput.exists()) {
                // Close any open file handles
                System.gc();
                
                // Delete the original file
                if (!sourceFile.delete()) {
                    throw new IOException("Failed to delete original file");
                }
                
                // Rename the temp file to the original filename
                if (!tempOutput.renameTo(sourceFile)) {
                    throw new IOException("Failed to rename temporary file");
                }
                
                // Update the target back to the original file
                audio.setTarget(sourceFile);
            }
        } finally {
            // Clean up the temporary file if it still exists
            if (tempOutput != null && tempOutput.exists()) {
                tempOutput.delete();
            }
        }
    }

}
    */