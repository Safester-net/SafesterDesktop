/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.safester.application.util.test;

import java.util.Date;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFTextExtractor {

    public static void main(String[] args) throws Exception {
        System.out.println(new Date() + " Begin");
        String filePath = "c:\\tmp\\anon_bug\\social-kitcen.pdf";
        filePath = "c:\\tmp\\anon_bug\\Jugement_1213.pdf";
        File file = new File(filePath);
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper stripper = new PDFTextStripper();
        
        // Configuration to maintain format
        stripper.setSortByPosition(true);
        stripper.setParagraphStart("\n");
        stripper.setParagraphEnd("\n");
        stripper.setPageStart("\n\n");
        stripper.setPageEnd("\n\n");
            
        String text = stripper.getText(document);
        
        System.out.println(new Date() + " End");
                
        File fileOut = new File(filePath + ".txt");
        FileUtils.write(fileOut, text, "UTF-8");

    }
}
