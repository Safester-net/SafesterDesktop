/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.safester.application.util.crypto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

public class PdfEncryptor {

    /**
     * Takes a PDF stream (typically from memory) and encrypts it with a password. The encrypted PDF is then
     * saved to the specified output path. This method uses Apache PDFBox and Apache Commons IO libraries.
     *
     * @param inputPdfStream The InputStream of the PDF to be encrypted.
     * @param outputPath     The file path where the encrypted PDF should be saved.
     * @param userPassword   The password required to open/view the PDF. Must not be null.
     * @param ownerPassword  The password that gives full control over the PDF. Must not be null.
     * @throws IOException If an error occurs during processing the PDF or saving the file.
     */
    
    public static void encryptPdf(InputStream inputPdfStream, String outputPath, String userPassword, String ownerPassword) throws IOException {
        Objects.requireNonNull(userPassword, "User password must not be null");
        Objects.requireNonNull(ownerPassword, "Owner password must not be null");

        byte[] streamAsByteArray = IOUtils.toByteArray(inputPdfStream);
        PDDocument document = Loader.loadPDF(streamAsByteArray);

        AccessPermission accessPermission = new AccessPermission();

        // Set to true if you want to allow printing
        accessPermission.setCanPrint(false);
        accessPermission.setCanExtractContent(false);

        StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
        spp.setEncryptionKeyLength(128);
        document.protect(spp);

        //ByteArrayInputStream bis = new ByteArrayInputStream(streamAsByteArray);
        File output = new File(outputPath);
        document.save(output);

    }
}
