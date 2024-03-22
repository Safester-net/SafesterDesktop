/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.safester.application.util.test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import net.safester.application.util.crypto.PdfEncryptor;

/**
 *
 * @author ndepo
 */
public class PdfEncryptorTest {

    public static void main(String args[]) throws Exception {
        System.out.println(new Date() + " " + "Begin..." );
        String inputPath = "c:\\tmp\\Safester_Cybtech_dev_1.0.pdf";
        String outputPath = "c:\\tmp\\Safester_Cybtech_dev_1.0._ENCRYPTED.pdf";
        
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputPath));) {
             PdfEncryptor.encryptPdf(in, outputPath, "test", "test");
        }
        
        System.out.println(new Date() + " " + "End." );

    }
}
