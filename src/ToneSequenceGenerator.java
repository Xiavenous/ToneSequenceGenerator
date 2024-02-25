import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        int sampleRateMilli = sampleRate / 1000;
        int frameSize = format.getFrameSize();
        byte[] audioData = new byte[frameSize * sampleRateMilli * 4000];
        // The "4" Comes from:
        // 0.5 seconds of A, 1 second of silence, 2.5 seconds of B
    
        // Generate 0.5 seconds of frequency A (440 Hz)
        for (int i = 0; i < sampleRateMilli * 500; i++) {
            double angle = 2.0 * Math.PI * 440 * i / sampleRate;
            short aSample = (short) (Short.MAX_VALUE * Math.sin(angle));
            audioData[i * frameSize] = (byte) (aSample & 0xFF);
            audioData[i * frameSize + 1] = (byte) ((aSample >> 8) & 0xFF);
        }
    
        // byte[] auxArray = new byte[audioData.length];
        // for (int i = 0; i < audioData.length; i++) {
        //     auxArray[i] = audioData[i];
        // }
        // audioData = new byte[auxArray.length + sampleRate * frameSize + 1];

        // Generate 1 second of silence
        for (int i = sampleRateMilli * 500; i < sampleRateMilli * 1500; i++) {
            for (int j = 0; j < format.getChannels(); j++) {
                audioData[i * frameSize + j] = 0;
            }
        }
    
        // auxArray = new byte[audioData.length];
        // for (int i = 0; i < audioData.length; i++) {
        //     auxArray[i] = audioData[i];
        // }
        // audioData = new byte[auxArray.length + sampleRate * frameSize + ];
    
        // Generate 2.5 seconds of frequency B (880 Hz)
        for (int i = (int) (sampleRateMilli * 1500); i < sampleRateMilli * 4000; i++) {
            double angle = 2.0 * Math.PI * 880 * i / sampleRate;
            short bSample = (short) (Short.MAX_VALUE * Math.sin(angle));
            audioData[i * frameSize] = (byte) (bSample & 0xFF);
            audioData[i * frameSize + 1] = (byte) ((bSample >> 8) & 0xFF);
        }

        return audioData;
    }
}