/*
 * This file is part of Safester.                                    
 * Copyright (C) 2019, KawanSoft SAS
 * (https://www.Safester.net). All rights reserved.                                
 *                                                                               
 * Safester is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * Safester is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package net.safester.application.util.sound;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

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
        player.playSound(new File(
                "C:\\Users\\Nicolas de Pomereu\\Desktop\\snapshot.mp3"));
        player.playSound(
                new File("C:\\Users\\Nicolas de Pomereu\\Desktop\\notify.wav"));

        InputStream in = new FileInputStream(
                "C:\\Users\\Nicolas de Pomereu\\Desktop\\01 Where the Sour Turns to Sweet.mp3");
        player.playSound(in);

        System.out.println(new Date() + " Audio Player Started!");
    }

    /**
     * Gets an input stream on a resource resourceFilepath. resourceFilepath is
     * a resource with filename that starts with "images/files/sounds/".
     *
     * @param resourceFilepath
     * @return the input stream
     * @throws IOException
     */
    public static InputStream getInputStreamOnResource(String resourceFilepath) throws IOException {
        java.net.URL url = Main.class.getResource(resourceFilepath);
        if (url != null) {
            return url.openStream();
        } else {
            throw new FileNotFoundException("Filepath not found for audio file: " + resourceFilepath);
        }
    }

    /**
     * Plays the input file in a thread;
     *
     * @param file
     */
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

    /**
     * Plays a stream. Because stream must be-reread, it is first dumped in temp
     * file and read as temp file.
     *
     * @param in
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    public void playSound(InputStream in) throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {

        Runnable r = new Runnable() {
            private InputStream in;

            public void run() {
                try {

                    File temp = File.createTempFile("sound", null);
                    try {
                        Files.copy(in, temp.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } finally {
                        if (in != null) {
                            in.close();
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

    /**
     * Real sound player.
     *
     * @param file
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    private void playSoundInternal(File file) throws IOException,
            UnsupportedAudioFileException, LineUnavailableException {

        try (final AudioInputStream in = getAudioInputStream(file)) {

//            if (true) {
//                throw new IllegalArgumentException("Simulation of Exception in playSoundInternal().");
//            }
                                
            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final Info info = new Info(SourceDataLine.class, outFormat);

            final SourceDataLine line = (SourceDataLine) AudioSystem
                    .getLine(info);

            if (line != null) {
                line.open(outFormat);
                line.start();
                stream(getAudioInputStream(outFormat, in), line);
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
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
}
