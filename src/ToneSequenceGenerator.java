import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.*;

public class ToneSequenceGenerator {

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        System.out.println("Hello world");

        Scanner scanner = new Scanner(System.in);

        String response = scanner.next();

        if ("play".equals(response)){
            File file = new File("output.wav");
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);

            Clip clip = AudioSystem.getClip();
            clip.open(audio);

            clip.start();

            String terminate = scanner.next();
        }

        if ("record".equals(response)){
            try {
                // Set up audio format
                AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44000, 16, 2, 4, 44000, false);

                // Open line for playback
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                // Generate audio data
                byte[] audioData = generateAudio(format);

                // Write audio data to file
                AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioData), format,
                        audioData.length / format.getFrameSize());
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File("output.wav"));

                // Close the line and input stream
                line.drain();
                line.close();
                audioInputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static byte[] generateAudio(AudioFormat format) {
        int sampleRate = (int) format.getSampleRate();
        byte[] audioData = new byte[sampleRate * format.getFrameSize() * (2 + 1 + 5)]; // 2 seconds of A, 1 second of silence, 5 seconds of B
    
        // Generate 2 seconds of frequency A (440 Hz)
        for (int i = 0; i < sampleRate / 2; i++) {
            double angle = 2.0 * Math.PI * 440 * i / sampleRate;
            short aSample = (short) (Short.MAX_VALUE * Math.sin(angle));
            audioData[i * format.getFrameSize()] = (byte) (aSample & 0xFF);
            audioData[i * format.getFrameSize() + 1] = (byte) ((aSample >> 8) & 0xFF);
        }
    
        // Generate 1 second of silence
        for (int i = sampleRate / 2; i < sampleRate * 1.5; i++) {
            for (int j = 0; j < format.getChannels(); j++) {
                audioData[i * format.getFrameSize() + j] = 0;
            }
        }
    
        // Generate 5 seconds of frequency B (880 Hz)
        for (int i = (int) (sampleRate * 1.5); i < sampleRate * 4; i++) {
            double angle = 2.0 * Math.PI * 880 * (i - sampleRate * 3) / sampleRate;
            short bSample = (short) (Short.MAX_VALUE * Math.sin(angle));
            audioData[i * format.getFrameSize()] = (byte) (bSample & 0xFF);
            audioData[i * format.getFrameSize() + 1] = (byte) ((bSample >> 8) & 0xFF);
        }
    
        return audioData;
    }
}