package net.safester.application.util.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.safester.application.Main;

public class AudioFilePlayer {

    private Exception exception;

    public static void main(String[] args) throws Exception {

        System.out.println(new Date() + " Audio Player Starting...");

        final AudioFilePlayer player = new AudioFilePlayer();
        player.playSound(new File("C:\\Users\\Nicolas de Pomereu\\Desktop\\snapshot.mp3"));
        player.playSound(new File("C:\\Users\\Nicolas de Pomereu\\Desktop\\notify.wav"));

        InputStream in = new FileInputStream(
                "C:\\Users\\Nicolas de Pomereu\\Desktop\\01 Where the Sour Turns to Sweet.mp3");
        player.playSound(in);

        System.out.println(new Date() + " Audio Player Started!");
    }

    public static InputStream getInputStreamOnResource(String resourceFilepath) throws IOException {
        java.net.URL url = Main.class.getResource(resourceFilepath);
        if (url != null) {
            return url.openStream();
        } else {
            throw new FileNotFoundException("Filepath not found for audio file: " + resourceFilepath);
        }
    }

    public void playSound(File file) {
        Runnable r = new Runnable() {
            private File file;

            public void run() {
                try {
                    playSoundInternal(this.file);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    exception = e;
                }
            }

            public Runnable setFile(File f) {
                this.file = f;
                return this;
            }
        }.setFile(file);

        new Thread(r).start();
    }

    public void playSound(InputStream in) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        Runnable r = new Runnable() {
            private InputStream in;

            public void run() {
                try {

                    File temp = File.createTempFile("sound", null);
                    try {
                        Files.copy(this.in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } finally {
                        if (this.in != null) {
                            this.in.close();
                        }
                    }

                    playSoundInternal(temp);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    exception = e;
                }
            }

            public Runnable setInputSream(InputStream in) {
                this.in = in;
                return this;
            }

        }.setInputSream(in);

        new Thread(r).start();
    }

    private void playSoundInternal(File file) throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        try (final AudioInputStream in = AudioSystem.getAudioInputStream(file)) {

            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final Info info = new Info(SourceDataLine.class, outFormat);

            try (final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                line.open(outFormat);
                line.start();
                stream(AudioSystem.getAudioInputStream(outFormat, in), line);
                line.drain();
                line.stop();
            }
        }
    }

    public Exception getException() {
        return exception;
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
}
