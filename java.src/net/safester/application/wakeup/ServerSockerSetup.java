/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.safester.application.wakeup;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import net.safester.application.Safester;
import static net.safester.application.Safester.PORT_FILE;
import static net.safester.application.Safester.serverSocket;

/**
 *
 * @author ndepo
 */
public class ServerSockerSetup {
 
       public static void setupServerSocket() {
        try {
            Safester.serverSocket = new ServerSocket(0); // Bind to any available port
            int port = serverSocket.getLocalPort();
            try (FileWriter writer = new FileWriter(PORT_FILE)) {
                writer.write(Integer.toString(port));
            }
        } catch (IOException e) {
            System.err.println("Error setting up server socket.");
            e.printStackTrace();
            System.exit(1);
        }
    }
       
}
