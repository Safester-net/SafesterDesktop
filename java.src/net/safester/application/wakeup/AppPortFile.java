/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.safester.application.wakeup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import net.safester.application.parms.Parms;

/**
 *
 * @author ndepo
 */
public class AppPortFile {
    
   public static final String APP_PORT_FILE = Parms.getSafesterUserHomeDir() + File.separator +  "app_port.txt";
        
    public static int readPortFromFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(APP_PORT_FILE))) {
            return Integer.parseInt(reader.readLine());
        }
    }
    
    /**
     * Not necessary
     */
    static void deletePortFile() {
        try {
            File file = new File(APP_PORT_FILE);
            file.delete();
        } catch (Exception e) {
            System.err.println();
        }
    }
}
