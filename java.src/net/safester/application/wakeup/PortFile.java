/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.safester.application.wakeup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import net.safester.application.Safester;

/**
 *
 * @author ndepo
 */
public class PortFile {
    
        public static int readPortFromFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(Safester.PORT_FILE))) {
            return Integer.parseInt(reader.readLine());
        }
    }
}
