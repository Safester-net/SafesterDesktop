/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.safester.application.wakeup;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author ndepo
 */
public class WakeupCallSender {
    
   public static void sendWakeUpCall() {
        try {
            int port = AppPortFile.readPortFromFile();
            try (Socket clientSocket = new Socket("localhost", port)) {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("WAKEUP");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
}
