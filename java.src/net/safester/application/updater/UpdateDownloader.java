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
package net.safester.application.updater;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.awakefw.file.api.client.AwakeFileSession;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.version.Version;

/**
 * JFrame of SafeShareIt updater
 * If download / install succeed launch SafeShareIt
 * @author Alexandre Becquereau
 */
public class UpdateDownloader extends javax.swing.JFrame {

    MessagesManager messageManager;
    
    /** The Download Engine Monitor that wathes the thread progress */
    private Timer downloadEngineMonitor;

    /** The http parameters for the http session */
    private AwakeFileSession awakeFileSession = null;
    private String installationDir = null;
    private String email = null;
    private char[] passphrase = null;

    
    /** Creates new form InstallUpdater */
    public UpdateDownloader(AwakeFileSession awakeFileSession, String installationDir, String theEmail, char[] thePassphrase) throws IOException {
        
        initComponents();

        this.awakeFileSession = awakeFileSession;
        this.installationDir = installationDir;

        this.email = theEmail;
        this.passphrase = thePassphrase;
        
        initCompany();
    }

    private void initCompany() throws IOException{
        
        messageManager = new MessagesManager();
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        jLabelTitle.setIcon(Parms.createImageIcon("images/files/safester_logo_small.png"));
        jLabelTitle.setText(null);
        jLabelWarning.setText(messageManager.getMessage("wait_during_download"));
        jButtonCancel.setText(messageManager.getMessage("cancel"));
        this.setTitle(Version.NAME);

        keyListenerAdder();
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        Point middlePoint = new Point((dim.width / 2) - (this.getWidth() / 2),
                (dim.height / 2) - (this.getHeight() / 2));
        
        this.setLocation(middlePoint);
        setVisible(true);
        
        //Launch download of files
        try{
            doDownload();
        }
        catch(Exception e){
            e.printStackTrace();
            String message = messageManager.getMessage("download_failed") + "\n" +e.getMessage();
            message += "\n" + messageManager.getMessage("application_will_exit");
            JOptionPane.showMessageDialog(rootPane, message);
            System.exit(0);
        }
    }

    private void keyListenerAdder(){
        KeyListener keyListener = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                dispose();
            }

        };
        List<Component> components = SwingUtil.getAllComponants(this);
        for (Component component : components) {
            component.addKeyListener(keyListener);
        }
    }

    /**
     * Download SafeShareIt files to installation directory
     */
    private void doDownload() throws IOException{
        
        downloadEngineMonitor = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                jProgressBar.setValue(UrlFileDownloader.getProgress());
            }
        });

        jProgressBar.setString("Starting...");

        downloadEngineMonitor.start();
        
        Thread t = new Thread(){
            public void run(){
                try{
                    ApplicationJarDowloader ssiJarInstaller
                                = new ApplicationJarDowloader(awakeFileSession, installationDir, email, passphrase);
                    ssiJarInstaller.install();
                }
                catch(Exception e ){
                    JOptionPaneNewCustom.showException(rootPane, e);
                    System.exit(0);
                }
            }
        };

        t.start();
        jProgressBar.setString("Launching...");
        return;
            
    }

    /**
     * Ask for user confirmation before closing app
     */
    @Override
    public void dispose() {
        int result = JOptionPane.showConfirmDialog(rootPane, messageManager.getMessage("cancel_and_exit"), messageManager.getMessage("warning"), JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMain = new javax.swing.JPanel();
        jPanelUp = new javax.swing.JPanel();
        jPanelLogoContainer = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelLogoMain = new javax.swing.JPanel();
        jPanelLogo = new javax.swing.JPanel();
        jLabelWarning = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanelMiddle = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();
        jProgressBar = new javax.swing.JProgressBar();
        jPanelRight = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanelUpDown = new javax.swing.JPanel();
        jPanelLeft1 = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jPanelRight1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelUp.setLayout(new javax.swing.BoxLayout(jPanelUp, javax.swing.BoxLayout.Y_AXIS));

        jPanelLogoContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabelTitle.setText("jLabelTitle");
        jPanel2.add(jLabelTitle);

        jPanelLogoContainer.add(jPanel2);

        jPanelUp.add(jPanelLogoContainer);

        jPanelLogoMain.setLayout(new java.awt.BorderLayout());

        jLabelWarning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWarning.setText("jLabelWarning");
        jPanelLogo.add(jLabelWarning);

        jPanelLogoMain.add(jPanelLogo, java.awt.BorderLayout.CENTER);

        jPanelUp.add(jPanelLogoMain);

        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel3.setPreferredSize(new java.awt.Dimension(400, 10));
        jPanelUp.add(jPanel3);

        jPanelMain.add(jPanelUp);

        jPanelMiddle.setLayout(new javax.swing.BoxLayout(jPanelMiddle, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft.setOpaque(false);
        jPanelMiddle.add(jPanelLeft);

        jProgressBar.setPreferredSize(new java.awt.Dimension(162, 17));
        jProgressBar.setStringPainted(true);
        jPanelMiddle.add(jProgressBar);

        jPanelRight.setOpaque(false);
        jPanelMiddle.add(jPanelRight);

        jPanelMain.add(jPanelMiddle);

        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel4.setPreferredSize(new java.awt.Dimension(400, 10));
        jPanelMain.add(jPanel4);

        jPanelUpDown.setLayout(new javax.swing.BoxLayout(jPanelUpDown, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft1.setOpaque(false);
        jPanelUpDown.add(jPanelLeft1);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelUpDown.add(jButtonCancel);

        jPanelRight1.setOpaque(false);
        jPanelUpDown.add(jPanelRight1);

        jPanelMain.add(jPanelUpDown);

        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel5.setPreferredSize(new java.awt.Dimension(400, 10));
        jPanelMain.add(jPanel5);

        getContentPane().add(jPanelMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException{
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpdateDownloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(UpdateDownloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(UpdateDownloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(UpdateDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    String installationDir = InstallParameters.getInstallationDir();
                    new UpdateDownloader(null, installationDir, null, null).setVisible(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                    Logger.getLogger(UpdateDownloader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelWarning;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelLeft1;
    private javax.swing.JPanel jPanelLogo;
    private javax.swing.JPanel jPanelLogoContainer;
    private javax.swing.JPanel jPanelLogoMain;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelMiddle;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelRight1;
    private javax.swing.JPanel jPanelUp;
    private javax.swing.JPanel jPanelUpDown;
    private javax.swing.JProgressBar jProgressBar;
    // End of variables declaration//GEN-END:variables
}
