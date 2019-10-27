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
package net.safester.application.photo;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;

import com.safelogic.utilx.Base64;

public class ImageResizer {

    //The image
    BufferedImage image = null;
    
    // The maximum image size in pixels
    private static int MAXIMUM_SURFACE_SIZE = 120 * 120;
    
    // The maximum sticker size in pixels
    private static int MAXIMUM_STICKER_SURFACE_SIZE = 26 * 26;

    /**
     * Constructor
     *
     * @param imageFile reference to image file on disk
     */
    public ImageResizer(File imageFile)
            throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("imageFile can not be null!");
        }

        image = ImageIO.read(imageFile);
    }

    /**
     * Constructor
     *
     * @param urlImage reference to image file on disk
     */
    public ImageResizer(URL urlImage)
            throws IOException {
        if (urlImage == null) {
            throw new IllegalArgumentException("urlImage can not be null!");
        }

        image = ImageIO.read(urlImage);
    }

    /**
     * Constructor
     *
     * @param imageArray reference to image in byte array
     */
    public ImageResizer(byte[] imageArray)
            throws IllegalArgumentException {
        if (imageArray == null) {
            throw new IllegalArgumentException("imageFile can not be null!");
        }

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageArray);
            image = ImageIO.read(bis);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ImageIcon createIconFromBase64(String photoBase64) {

        ImageResizer imageResizer = new ImageResizer(Base64.base64ToByteArray(photoBase64));
        ImageIcon imageIcon = new ImageIcon(imageResizer.getBufferedImage());
        return imageIcon;
    }

    /**
     *
     * @return the sticker height in pixels
     */
    public static int getStickerHeight() {
        return (int) Math.sqrt((double) MAXIMUM_STICKER_SURFACE_SIZE);
    }

    /**
     * Set image size to maximum size (keep aspect ratio)
     */
    public void resizeToMaximumAllowedSize() {
        int height = image.getHeight();
        int width = image.getWidth();

        int surface = height * width;

        if (surface > MAXIMUM_SURFACE_SIZE) {
            // Compute the percentage size difference

            double reducerFactor = Math.sqrt((double) MAXIMUM_SURFACE_SIZE) / Math.sqrt((double) surface);

            int newHeight = (int) (height * reducerFactor);
            int newWidth = (int) (width * reducerFactor);

            this.resize(newWidth, newHeight);
        }

    }

    /**
     * Adjust to sticker size
     */
    public void resizeToStickerSize() {
        int height = image.getHeight();
        int width = image.getWidth();

        int surface = height * width;

        if (surface > MAXIMUM_STICKER_SURFACE_SIZE) {
            // Compute the percentage size difference
            
            double reducerFactor = Math.sqrt((double) MAXIMUM_STICKER_SURFACE_SIZE) / Math.sqrt((double) surface);

            int newHeight = (int) (height * reducerFactor);
            int newWidth = (int) (width * reducerFactor);

            this.resize(newWidth, newHeight);
        }
    }

    /**
     * Resize image.
     *
     * @param newW new width
     * @param newH new height
     */
    public void resize(int newW, int newH) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, image.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        image = dimg;
    }

    /**
     * Save image to disk
     *
     * @param ref
     */
    public void saveImage(File ref)
            throws IOException {
        String format = (ref.toString().toLowerCase().endsWith(".png")) ? "png" : "jpg";
        ImageIO.write(image, format, ref);
    }

    /**
     *
     * @return the JPEG Image as a byte array
     * @throws IOException
     */
    public byte[] getImage() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        String format = "jpg";

        try {
            ImageIO.write(image, format, os);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return os.toByteArray();
    }

    /**
     * Get the buffered image
     *
     * @return the buffered image
     */
    public BufferedImage getBufferedImage() {
        return image;
    }

    /**
     * Load an image into a byte array from a file
     *
     * @param imageFile the image file
     * @return a byte array containing the image
     *
     * @throws IOException
     */
    public static byte[] loadImage(File imageFile)
            throws IOException {
        
        DataInputStream in =null;
        
        try {
            if (imageFile == null) {
                throw new IllegalArgumentException("imageFile can not be null!");
            }

            long length = imageFile.length();
            byte[] image = new byte[(int) length];

            in = new DataInputStream(new FileInputStream(imageFile));
            in.read(image);
            return image;            
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }
}
