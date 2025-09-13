import org.junit.jupiter.api.Test;
import ws.schild.jave.encode.AudioAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Quality {
    @Test
    public void should_Select_Quality() {
    AudioAttributes audio = new AudioAttributes();
    audio.setCodec("libmp3lame");
    audio.setBitRate(64000);
    audio.setChannels(2);
    audio.setSamplingRate(44100);
    assertEquals(audio.getCodec(), "libmp3lame");
    }
}
