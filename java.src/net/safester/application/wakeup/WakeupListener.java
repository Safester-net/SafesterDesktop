/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.safester.application.wakeup;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import net.safester.application.Login;
import net.safester.application.Main;
import net.safester.application.Safester;

public class WakeupListener implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (Socket clientSocket = Safester.serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String message = in.readLine();
                if ("WAKEUP".equals(message)) {
                    // Implement your wake-up logic here
                    
                    //System.out.println("Waking up the application.");
                    //JOptionPane.showConfirmDialog(null, "OK!");
                    Main main = Main.MAIN;
                    if (main != null) {
                        main.deiconify();
                    }
                    else {
                        Login login = Login.LOGIN;
                        if (login != null) {
                            login.deiconify();
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                // Break the loop if the server socket is closed
                break;
            }
        }
    }
}
