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
package com.safelogic.pgp.util;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.jdesktop.swingx.error.ErrorInfo;

import com.safelogic.pgp.api.util.msg.MessagesManager;

/**
 *
 * @author  Nicolas de Pomereu
 */
public class JErrorDialog extends javax.swing.JDialog {

    /** Dialog Width */
    private int STANDARD_WIDTH = 600;
    
    /** Height when Error Stack is not displayed */
    private int MIN_HEIGHT = 190;
    
    /** Height when Error Stack is  displayed */
    private int MAX_HEIGHT = 420;    
    
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
        initializeSafeLogic();
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
        initializeSafeLogic();
    }

   
    private void initializeSafeLogic()
    {
//      If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                jButtonCloseActionPerformed(null);
            }
        });
        
        jEditorPaneMessage.setContentType("text/html");
        jEditorPaneMessage.setEditable(false);  
        
        jEditorPaneStackTrace.setContentType("text/html");
        jEditorPaneStackTrace.setEditable(false);          
        
        if (errorInfo != null)
        {
            this.setTitle(errorInfo.getTitle());
            jEditorPaneMessage.setText(errorInfo.getBasicErrorMessage());
            jEditorPaneStackTrace.setText(errorInfo.getDetailedErrorMessage());
        }
        
        buttonDetailText = this.messages.getMessage("DETAILS_BUTTON"); 
        jButtonDetails.setText(buttonDetailText + " >>"  );
        jButtonClose.setText(this.messages.getMessage("CLOSE_BUTTON"));
        jButtonCopytToClipBoard.setText(this.messages.getMessage("COPY_TO_CLIPBOARD"));
        
        jPanelErrorStack.setVisible(false);
        this.setPreferredSize(new Dimension( STANDARD_WIDTH, MIN_HEIGHT));            
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
        jPanelLeft = new javax.swing.JPanel();
        jLabelLogoError = new javax.swing.JLabel();
        jPanelHeaderMessage = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPaneMessage = new javax.swing.JEditorPane();
        jPanelRight = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jButtonDetails = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanelTopEmpty = new javax.swing.JPanel();
        jPanelErrorStack = new javax.swing.JPanel();
        jPanelErrorStackContent = new javax.swing.JPanel();
        jPanelErrorStackContentLeft = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPaneStackTrace = new javax.swing.JEditorPane();
        jPanelErrorStackContentRight = new javax.swing.JPanel();
        jPanelErrorStackButtons = new javax.swing.JPanel();
        jButtonCopytToClipBoard = new javax.swing.JButton();

        setTitle("cGeep Error");
        setModal(true);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelHeader.setLayout(new javax.swing.BoxLayout(jPanelHeader, javax.swing.BoxLayout.Y_AXIS));

        jPanelTopMessages.setMinimumSize(new java.awt.Dimension(80, 100));
        jPanelTopMessages.setPreferredSize(new java.awt.Dimension(306, 100));
        jPanelTopMessages.setLayout(new javax.swing.BoxLayout(jPanelTopMessages, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft.setMaximumSize(new java.awt.Dimension(32, 32767));
        jPanelLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        jLabelLogoError.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pgeep/application/tools/common/images/JXErrorPane32.png"))); // NOI18N
        jPanelLeft.add(jLabelLogoError);

        jPanelTopMessages.add(jPanelLeft);

        jPanelHeaderMessage.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        jScrollPane2.setBorder(null);

        jEditorPaneMessage.setBorder(null);
        jEditorPaneMessage.setOpaque(false);
        jScrollPane2.setViewportView(jEditorPaneMessage);

        jPanelHeaderMessage.add(jScrollPane2);

        jPanelTopMessages.add(jPanelHeaderMessage);

        jPanelRight.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelRight.setPreferredSize(new java.awt.Dimension(10, 100));

        jPanelTopMessages.add(jPanelRight);

        jPanelHeader.add(jPanelTopMessages);

        jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 47));
        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonClose);

        jButtonDetails.setText("<< Details");
        jButtonDetails.setActionCommand("Details >>");
        jButtonDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailsActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonDetails);

        jPanel1.setPreferredSize(new java.awt.Dimension(2, 5));
        jPanelButtons.add(jPanel1);

        jPanelHeader.add(jPanelButtons);

        jPanelTopEmpty.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelTopEmpty.setPreferredSize(new java.awt.Dimension(100, 10));
        jPanelHeader.add(jPanelTopEmpty);

        getContentPane().add(jPanelHeader);

        jPanelErrorStack.setLayout(new javax.swing.BoxLayout(jPanelErrorStack, javax.swing.BoxLayout.Y_AXIS));

        jPanelErrorStackContent.setPreferredSize(new java.awt.Dimension(10, 300));
        jPanelErrorStackContent.setLayout(new javax.swing.BoxLayout(jPanelErrorStackContent, javax.swing.BoxLayout.LINE_AXIS));

        jPanelErrorStackContentLeft.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelErrorStackContentLeft.setMinimumSize(new java.awt.Dimension(10, 0));

        jPanelErrorStackContent.add(jPanelErrorStackContentLeft);

        jScrollPane1.setViewportView(jEditorPaneStackTrace);

        jPanelErrorStackContent.add(jScrollPane1);

        jPanelErrorStackContentRight.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelErrorStackContentRight.setMinimumSize(new java.awt.Dimension(10, 0));

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

        jPanelErrorStack.add(jPanelErrorStackButtons);

        getContentPane().add(jPanelErrorStack);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailsActionPerformed
    doDetail();
}//GEN-LAST:event_jButtonDetailsActionPerformed

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    this.dispose();
}//GEN-LAST:event_jButtonCloseActionPerformed

private void jButtonCopytToClipBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCopytToClipBoardActionPerformed
     jEditorPaneStackTrace.selectAll();
     jEditorPaneStackTrace.copy();
     jEditorPaneStackTrace.setCaretPosition(0);
}//GEN-LAST:event_jButtonCopytToClipBoardActionPerformed

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
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonCopytToClipBoard;
    private javax.swing.JButton jButtonDetails;
    private javax.swing.JEditorPane jEditorPaneMessage;
    private javax.swing.JEditorPane jEditorPaneStackTrace;
    private javax.swing.JLabel jLabelLogoError;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelErrorStack;
    private javax.swing.JPanel jPanelErrorStackButtons;
    private javax.swing.JPanel jPanelErrorStackContent;
    private javax.swing.JPanel jPanelErrorStackContentLeft;
    private javax.swing.JPanel jPanelErrorStackContentRight;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelHeaderMessage;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelTopEmpty;
    private javax.swing.JPanel jPanelTopMessages;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
