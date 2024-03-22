/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.safester.application.wakeup;

import java.net.Socket;

/**
 *
 * @author ndepo
 */
public class AnotherInstanceTester {

    private int port;

    public boolean isAnotherInstanceRunning() {
        try {
            port = AppPortFile.readPortFromFile();
            new Socket("localhost", port).close(); // Try connecting to the port
            return true; // Connection successful, another instance is running
        } catch (Exception e) {
            return false; // No other instance is running
        }
    }

    public int getPort() {
        return port;
    }
   
}
