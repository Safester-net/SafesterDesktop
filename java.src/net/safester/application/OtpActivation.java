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
package net.safester.application;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.lang3.SystemUtils;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;

/**
 *
 * @author  Nicolas de Pomereu
 */
public class OtpActivation extends javax.swing.JFrame {

    public static final int OTP_STEP_0 = 0;
    public static final int OTP_STEP_1 = 1;
    public static final int OTP_STEP_2 = 2;
    public static final int OTP_STEP_3 = 3;
    
    /** The parent JFrame */
    private JFrame parentJframe = null;
    private JFrame thisOne;
    private MessagesManager messages = new MessagesManager();
    private int optActivationStep = 0;

    private Connection connection;

    private int userNumber;
    private String keyId;

    String activationCode = null;
    /** Creates new form NewsFrame */
    public OtpActivation(JFrame parentJframe) {
        this.parentJframe = parentJframe;
        thisOne = this;
        initComponents();
        this.optActivationStep = 0;
        String title = messages.getMessage("otp_activation");
        initializeCompany(HtmlTextUtil.getHtmlHelpContent("otp_activation"), title);
    }

    /** Creates new form NewsFrame */
    public OtpActivation(JFrame parentJframe, Connection theConnection, int user_number, String key_id, String content, String title, int step) {
        this.parentJframe = parentJframe;
        thisOne = this;
        this.connection = theConnection;
        this.optActivationStep = step;
        this.userNumber = user_number;
        this.keyId = key_id;
        initComponents();
        initializeCompany(content, title);
    }

    /**
     * This is the method to include in the constructor
     */
    private void initializeCompany(String content, String title) {

        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.setSize(430, 585);

        if (parentJframe != null) {
            this.setLocationRelativeTo(parentJframe);
        }

        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        jEditorPane.setText(content);

        // Hyperlink listener that will open a new Broser with the given URL
        jEditorPane.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent r)
            {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    try
                    {
                        java.awt.Desktop dekstop = java.awt.Desktop.getDesktop();
                        dekstop.browse(r.getURL().toURI());
                    }
                    catch (Exception e )
                    {
                        // We don't care
                        e.printStackTrace();
                    }
                }
            }
        });
        
        this.jButtonClose.setText(messages.getMessage("cancel"));
        this.jButtonNext.setText(messages.getMessage("next") + ">");
        this.jButtonPrevious.setText("<" + messages.getMessage("previous"));

        // These 2 stupid lines : only to force to display top of file first
        jEditorPane.moveCaretPosition(0);
        jEditorPane.setSelectionEnd(0);

        this.keyListenerAdder();
        this.setLocationByPlatform(true);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
            }
        });

        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();

        if(this.optActivationStep  == OTP_STEP_0){
            jPanelSouth.remove(jButtonPrevious);
        }
        
        this.setLocationRelativeTo(parentJframe);
      //  WindowSettingManager.load(this);
        this.setTitle(title);
        this.setVisible(true);
    }

    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    this_keyReleased(e);
                }
            });
        }
    }

    private void showStep() {
        String title;
        String content;
        switch (this.optActivationStep) {
            case OTP_STEP_1:
                content = HtmlTextUtil.getHtmlHelpContent("otp_activation_1");
                title = messages.getMessage("otp_activation_1");
                break;
            case OTP_STEP_2:
                content = HtmlTextUtil.getHtmlHelpContent("otp_activation_2");
                title = messages.getMessage("otp_activation_2");
                content = MessageFormat.format(content, activationCode);
                break;
            case OTP_STEP_3:
                content = HtmlTextUtil.getHtmlHelpContent("otp_activation_3");
                content = MessageFormat.format(content, this.activationCode);
                title = messages.getMessage("otp_activation_3");
                break;
            default:
                content = HtmlTextUtil.getHtmlHelpContent("otp_activation");
                title = messages.getMessage("otp_activation");
        }
        this.dispose();
        new OtpActivation(parentJframe, connection, userNumber, keyId, content, title, optActivationStep).setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    ///////////////////////////////////////////////////////////////////////////    
    private void this_keyReleased(KeyEvent e) {
        //System.out.println("this_keyReleased(KeyEvent e) " + e.getComponent().getName()); 
        int id = e.getID();
        if (id == KeyEvent.KEY_RELEASED) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ENTER) {
                this.dispose();
            }
            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.dispose();
            }
        }
    }

    private void doIt(){
        switch(this.optActivationStep){
            case OTP_STEP_1: doItStep1();
            break;
            case OTP_STEP_2: doItStep2();
            break;
            case OTP_STEP_3: doItStep3();
            break;
            default: doItStep0();
        }
    }

    private void doItStep0(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        showNextStep();
        this.dispose();
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void doItStep1(){
        try
        {
            this.setCursor(Cursor.getDefaultCursor());
            AwakeConnection awakeConnection = (AwakeConnection)connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            try
            {
                activationCode = awakeFileSession.call("net.safester.server.OtpAuthentification.createActivationCode", keyId);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentJframe, messages.getMessage("otp_activation_not_available"));
                return;
            }
            
            showNextStep();

        }catch(Exception e ){
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(this, e);
        }
    }

    private void doItStep2(){
        try{
            this.setCursor(Cursor.getDefaultCursor());
            AwakeConnection awakeConnection = (AwakeConnection)connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
            String isActivated = awakeFileSession.call("net.safester.server.OtpAuthentification.userInWeboActivated", keyId);
            if(isActivated.equalsIgnoreCase("true")){
                showNextStep();
                this.dispose();
                this.setCursor(Cursor.getDefaultCursor());
            }else{
                this.setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, messages.getMessage("otp_activation_failed"), messages.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                activationCode = awakeFileSession.call("net.safester.server.OtpAuthentification.createActivationCode", keyId);
                this.optActivationStep--;
                showNextStep();
                dispose();
                this.setCursor(Cursor.getDefaultCursor());
            }
        }catch(Exception e){
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(this, e);
        }
    }

    private void doItStep3(){
        this.dispose();
    }

    private void showPreviousStep(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        optActivationStep--;
        showStep();
    }
    private void showNextStep(){

        optActivationStep++;
       showStep();
        
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jPanelCenter = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane = new javax.swing.JEditorPane();
        jPanelSep = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jButtonPrevious = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jPanelEast = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/inwebo_medium.png"))); // NOI18N
        jPanelNorth.add(jLabelLogo);

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jEditorPane.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jEditorPane);

        jPanelCenter.add(jScrollPane1);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelSep.setMinimumSize(new java.awt.Dimension(0, 4));
        jPanelSep.setPreferredSize(new java.awt.Dimension(376, 4));

        javax.swing.GroupLayout jPanelSepLayout = new javax.swing.GroupLayout(jPanelSep);
        jPanelSep.setLayout(jPanelSepLayout);
        jPanelSepLayout.setHorizontalGroup(
            jPanelSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 379, Short.MAX_VALUE)
        );
        jPanelSepLayout.setVerticalGroup(
            jPanelSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 4, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelWest.setPreferredSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelWestLayout = new javax.swing.GroupLayout(jPanelWest);
        jPanelWest.setLayout(jPanelWestLayout);
        jPanelWestLayout.setHorizontalGroup(
            jPanelWestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelWestLayout.setVerticalGroup(
            jPanelWestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 199, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        jButtonPrevious.setText("Previous");
        jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonPrevious);

        jButtonNext.setText("Next");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonNext);

        jButtonClose.setText("jButtonClose");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonClose);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanelEastLayout = new javax.swing.GroupLayout(jPanelEast);
        jPanelEast.setLayout(jPanelEastLayout);
        jPanelEastLayout.setHorizontalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelEastLayout.setVerticalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 199, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelEast, java.awt.BorderLayout.LINE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
    doIt();
}//GEN-LAST:event_jButtonNextActionPerformed

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    this.dispose();
}//GEN-LAST:event_jButtonCloseActionPerformed

private void jButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousActionPerformed
    showPreviousStep();
}//GEN-LAST:event_jButtonPreviousActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
         try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // For all unix machines ==> Nimbus
                if (! SystemUtils.IS_OS_WINDOWS && ! SystemUtils.IS_OS_MAC_OSX)
                {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MessagesManager messages = new MessagesManager();
                 String content = HtmlTextUtil.getHtmlHelpContent("otp_activation_1");
                String title = messages.getMessage(messages.getMessage("otp_activation_1"));
        new OtpActivation(null, null, -1, "keyId", content, title, 1).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrevious;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
