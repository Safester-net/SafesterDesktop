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
package net.safester.application.util;

import java.awt.Desktop;
import java.awt.Dimension;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.safester.application.messages.MessagesManager;
import net.safester.clientserver.ServerParms;




/**
 *
 * @author  Nicolas de Pomereu
 */
public class JErrorDialog extends javax.swing.JDialog {

    /** Dialog Width */
    private int STANDARD_WIDTH = 600;
    
    /** Height when Error Stack is not displayed */
    private int MIN_HEIGHT = 160;
    
    /** Height when Error Stack is  displayed */
    private int MAX_HEIGHT = 450;
    
    /** The parent calling window */
    private java.awt.Frame parent = null;
            
    /** The error Inf block */
    private ErrorInfo errorInfo = null;
    
    /** Messages in national language */
    private MessagesManager messages = new  MessagesManager();
    
    /** The Details button text */
    private String buttonDetailText = null;
    
    /** 
     * Not to be used Constructor. 
     */
    private JErrorDialog(JFrame jframe) {
        super(jframe);
        initComponents();   
        initializeCompany();
    }
    
    /** 
     * Default Constructor
     * Creates new form NewJDialog 
     */    
    public JErrorDialog(JFrame jframe, ErrorInfo errorInfo)
    {
        super(jframe);
        initComponents();
        this.errorInfo = errorInfo;  
        
        if (this.errorInfo.getErrorException() != null) {
            Throwable t = this.errorInfo.getErrorException();
            t.printStackTrace();
        }
        
        initializeCompany();
    }

   
    private void initializeCompany()
    {
        jEditorPaneMessage.setEditable(false);
        jEditorPaneStackTrace.setEditable(false);

        jEditorPaneMessage.setContentType("text/html");
        
        if (errorInfo != null)
        {
            this.setTitle(errorInfo.getTitle());
            jEditorPaneMessage.setText(errorInfo.getBasicErrorMessage());
            jEditorPaneStackTrace.setText(errorInfo.getDetailedErrorMessage());
        }
        
        buttonDetailText = this.messages.getMessage("details_button");
        jButtonDetails.setText(buttonDetailText + " >>"  );
        
        jButtonCopytToClipBoard.setText(this.messages.getMessage("copy_to_clipboard"));
        jButtonSendEmail.setText(this.messages.getMessage("send_email"));

        jEditorPaneStackTrace.moveCaretPosition(0);
        jEditorPaneStackTrace.setSelectionEnd(0);

        jPanelErrorStack.setVisible(false);
        this.setPreferredSize(new Dimension( STANDARD_WIDTH, MIN_HEIGHT));
        this.setSize(new Dimension( STANDARD_WIDTH, MIN_HEIGHT));
        pack();
        
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }        
   
    /**
     * Done when Details Button is hit
     */
    private void doDetail()
    {
        if (jPanelErrorStack.isVisible())
        {
            jPanelErrorStack.setVisible(false);
            this.setPreferredSize(new Dimension(STANDARD_WIDTH, MIN_HEIGHT));
            jButtonDetails.setText(buttonDetailText + " >>");
            pack();
        }
        else
        {

            Desktop dekstop = Desktop.getDesktop();
            if (! dekstop.isSupported(Desktop.Action.MAIL))
            {
                this.jButtonSendEmail.setVisible(false);
            }
        
            jPanelErrorStack.setVisible(true);
            this.setPreferredSize(new Dimension(STANDARD_WIDTH, MAX_HEIGHT));
            jButtonDetails.setText(buttonDetailText + " <<");
            pack();
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

        jPanelHeader = new javax.swing.JPanel();
        jPanelTopMessages = new javax.swing.JPanel();
        jPanelLogo = new javax.swing.JPanel();
        jLabelLogoError = new javax.swing.JLabel();
        jPanelHeaderMessage = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPaneMessage = new javax.swing.JEditorPane();
        jPanel2 = new javax.swing.JPanel();
        jPanelRight = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonDetails = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanelErrorStack = new javax.swing.JPanel();
        jPanelErrorStackContent = new javax.swing.JPanel();
        jPanelErrorStackContentLeft = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPaneStackTrace = new javax.swing.JEditorPane();
        jPanelErrorStackContentRight = new javax.swing.JPanel();
        jPanelErrorStackButtons = new javax.swing.JPanel();
        jButtonCopytToClipBoard = new javax.swing.JButton();
        jButtonSendEmail = new javax.swing.JButton();

        setTitle("Error");
        setModal(true);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelHeader.setLayout(new javax.swing.BoxLayout(jPanelHeader, javax.swing.BoxLayout.Y_AXIS));

        jPanelTopMessages.setMaximumSize(new java.awt.Dimension(65544, 80));
        jPanelTopMessages.setMinimumSize(new java.awt.Dimension(10, 80));
        jPanelTopMessages.setPreferredSize(new java.awt.Dimension(10, 80));
        jPanelTopMessages.setLayout(new javax.swing.BoxLayout(jPanelTopMessages, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLogo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        jLabelLogoError.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/JXErrorPane32.png"))); // NOI18N
        jPanelLogo.add(jLabelLogoError);

        jPanelTopMessages.add(jPanelLogo);

        jPanelHeaderMessage.setLayout(new javax.swing.BoxLayout(jPanelHeaderMessage, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel3.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanel3.setPreferredSize(new java.awt.Dimension(507, 10));

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 507, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 10, Short.MAX_VALUE)
        );

        jPanelHeaderMessage.add(jPanel3);

        jScrollPane2.setBorder(null);

        jEditorPaneMessage.setBorder(null);
        jEditorPaneMessage.setOpaque(false);
        jScrollPane2.setViewportView(jEditorPaneMessage);

        jPanelHeaderMessage.add(jScrollPane2);

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel2.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanel2.setPreferredSize(new java.awt.Dimension(507, 10));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 507, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 10, Short.MAX_VALUE)
        );

        jPanelHeaderMessage.add(jPanel2);

        jPanelTopMessages.add(jPanelHeaderMessage);

        jPanelRight.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelRight.setPreferredSize(new java.awt.Dimension(10, 100));

        org.jdesktop.layout.GroupLayout jPanelRightLayout = new org.jdesktop.layout.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 10, Short.MAX_VALUE)
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 10, Short.MAX_VALUE)
        );

        jPanelTopMessages.add(jPanelRight);

        jPanelHeader.add(jPanelTopMessages);

        jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonDetails.setText("<< Details");
        jButtonDetails.setActionCommand("Details >>");
        jButtonDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailsActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonDetails);

        jPanel1.setPreferredSize(new java.awt.Dimension(2, 5));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 2, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 5, Short.MAX_VALUE)
        );

        jPanelButtons.add(jPanel1);

        jPanelHeader.add(jPanelButtons);

        getContentPane().add(jPanelHeader);

        jPanelErrorStack.setLayout(new javax.swing.BoxLayout(jPanelErrorStack, javax.swing.BoxLayout.Y_AXIS));

        jPanelErrorStackContent.setPreferredSize(new java.awt.Dimension(10, 300));
        jPanelErrorStackContent.setLayout(new javax.swing.BoxLayout(jPanelErrorStackContent, javax.swing.BoxLayout.LINE_AXIS));

        jPanelErrorStackContentLeft.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelErrorStackContentLeft.setMinimumSize(new java.awt.Dimension(10, 0));

        org.jdesktop.layout.GroupLayout jPanelErrorStackContentLeftLayout = new org.jdesktop.layout.GroupLayout(jPanelErrorStackContentLeft);
        jPanelErrorStackContentLeft.setLayout(jPanelErrorStackContentLeftLayout);
        jPanelErrorStackContentLeftLayout.setHorizontalGroup(
            jPanelErrorStackContentLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 7, Short.MAX_VALUE)
        );
        jPanelErrorStackContentLeftLayout.setVerticalGroup(
            jPanelErrorStackContentLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 150, Short.MAX_VALUE)
        );

        jPanelErrorStackContent.add(jPanelErrorStackContentLeft);

        jScrollPane1.setViewportView(jEditorPaneStackTrace);

        jPanelErrorStackContent.add(jScrollPane1);

        jPanelErrorStackContentRight.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelErrorStackContentRight.setMinimumSize(new java.awt.Dimension(10, 0));

        org.jdesktop.layout.GroupLayout jPanelErrorStackContentRightLayout = new org.jdesktop.layout.GroupLayout(jPanelErrorStackContentRight);
        jPanelErrorStackContentRight.setLayout(jPanelErrorStackContentRightLayout);
        jPanelErrorStackContentRightLayout.setHorizontalGroup(
            jPanelErrorStackContentRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 7, Short.MAX_VALUE)
        );
        jPanelErrorStackContentRightLayout.setVerticalGroup(
            jPanelErrorStackContentRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 150, Short.MAX_VALUE)
        );

        jPanelErrorStackContent.add(jPanelErrorStackContentRight);

        jPanelErrorStack.add(jPanelErrorStackContent);

        jPanelErrorStackButtons.setMaximumSize(new java.awt.Dimension(32767, 47));
        jPanelErrorStackButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        jButtonCopytToClipBoard.setText("Copy To Clipboard");
        jButtonCopytToClipBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCopytToClipBoardActionPerformed(evt);
            }
        });
        jPanelErrorStackButtons.add(jButtonCopytToClipBoard);

        jButtonSendEmail.setText("Send Email");
        jButtonSendEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendEmailActionPerformed(evt);
            }
        });
        jPanelErrorStackButtons.add(jButtonSendEmail);

        jPanelErrorStack.add(jPanelErrorStackButtons);

        getContentPane().add(jPanelErrorStack);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailsActionPerformed
    doDetail();
}//GEN-LAST:event_jButtonDetailsActionPerformed

private void jButtonCopytToClipBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCopytToClipBoardActionPerformed
     jEditorPaneStackTrace.selectAll();
     jEditorPaneStackTrace.copy();
     jEditorPaneStackTrace.setCaretPosition(0);
}//GEN-LAST:event_jButtonCopytToClipBoardActionPerformed

private void jButtonSendEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendEmailActionPerformed

    String unableToSendEmail = this.messages.getMessage("unable_to_send_email");

    try {
        Desktop dekstop = java.awt.Desktop.getDesktop();

        String text = jEditorPaneStackTrace.getText();
        String mailTo = ServerParms.CONTACT_EMAIL + "?subject=Error Message&body=" + text;

        URI uriMailTo;
        uriMailTo = new URI("mailto", mailTo, text);
        dekstop.mail(uriMailTo);
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(parent, unableToSendEmail);
    }


}//GEN-LAST:event_jButtonSendEmailActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JErrorDialog dialog = new JErrorDialog(new javax.swing.JFrame());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCopytToClipBoard;
    private javax.swing.JButton jButtonDetails;
    private javax.swing.JButton jButtonSendEmail;
    private javax.swing.JEditorPane jEditorPaneMessage;
    private javax.swing.JEditorPane jEditorPaneStackTrace;
    private javax.swing.JLabel jLabelLogoError;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelErrorStack;
    private javax.swing.JPanel jPanelErrorStackButtons;
    private javax.swing.JPanel jPanelErrorStackContent;
    private javax.swing.JPanel jPanelErrorStackContentLeft;
    private javax.swing.JPanel jPanelErrorStackContentRight;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelHeaderMessage;
    private javax.swing.JPanel jPanelLogo;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelTopMessages;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
