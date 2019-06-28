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

import java.awt.Cursor;
import java.awt.Frame;
import java.sql.Connection;

import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ConnectionParms;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.DesktopWrapper;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.swing.util.SwingUtil;
import java.awt.Color;

/**
 * This dialog is displayed on connection when active subscription is expired
 * @author Alexandre Becquereau
 */
public class ActivateSubscriptionDialog extends javax.swing.JDialog {


    private Connection connection;
    private int userNumber;
    private MessagesManager messages = new MessagesManager();
    short newSubscription = StoreParms.PRODUCT_FREE;

    Frame caller;

    /** Creates new form ExpiredSubscriptionDialog */
    public ActivateSubscriptionDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initCompany();

        this.setLocationRelativeTo(parent);

    }

    public ActivateSubscriptionDialog(java.awt.Frame parent, int user_number, boolean modal, Connection theConnection) {
        this(parent, modal);
        caller = parent;
        userNumber = user_number;

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        initCompany();
        this.setLocationRelativeTo(parent);
    }

    private void initCompany() {

        try
        {
            this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        
        this.setTitle(messages.getMessage("subscription_activation"));
        this.jLabelTitle.setText(messages.getMessage("subscription_activation"));

        jLabelVoucherCode.setText(messages.getMessage("activation_code"));
        jTextFieldVoucherCode.setText("");
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);
        
        jEditorPane.setBackground(Color.WHITE);

        // Hyperlink listener that will open a new Browser with the given URL
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent r) {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    DesktopWrapper.browse(r.getURL());
                }
            }
        });
        
        jEditorPane.setText(HtmlTextUtil.getHtmlHelpContent("activate_subscription"));


        this.jButtonActivate.setText(messages.getMessage("activate"));
        this.jButtonCancel.setText(messages.getMessage("cancel"));

        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);

        this.setSize(400, 400);

        this.jTextFieldVoucherCode.requestFocus();
    }

    private void doIt() {
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if(jTextFieldVoucherCode.getText() == null){
                JOptionPane.showMessageDialog(rootPane, messages.getMessage("please_enter_code"));
                return;
            }
            //Get voucher code from user input
            String voucherCode = jTextFieldVoucherCode.getText().trim();           
            voucherCode = voucherCode.toLowerCase();
            AwakeConnection awakeConnection = (AwakeConnection)connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            //Activate subscription
            String subscriptionCode = awakeFileSession.call("net.safester.server.SubscriptionValidator.validateSubscription",connection, userNumber, voucherCode);

            this.setCursor(Cursor.getDefaultCursor());
            try{
                //Subscription type was returned by awake call...
                short subscription = Short.parseShort(subscriptionCode);
                //Store new subscription in memory
                newSubscription = subscription;
                ConnectionParms.setSubscription(subscription);
            }
            catch(NumberFormatException nfe){
                displayError(subscriptionCode);
                return;
            }

            JOptionPane.showMessageDialog(rootPane, messages.getMessage("subscription_activated"));
            this.dispose();
        } catch (Exception ex) {
            JOptionPaneNewCustom.showException(this, ex);
        }
    }

    private void displayError(String errorCode){
        String errorMessage = "";
    
        if(errorCode.equals(StoreParms.EXPIRED_CODE)){
            errorMessage = messages.getMessage("code_already_used");
        } else if(errorCode.equals(StoreParms.INVALID_CODE)) {
            errorMessage = messages.getMessage("invalid_code");
        } else if (errorCode.equals(StoreParms.SYSTEM_ERROR)){
            errorMessage = messages.getMessage("error_code_contact_support");
        } else {
            errorMessage = messages.getMessage("unknown_error_contact_support");
        }

            JOptionPane.showMessageDialog(rootPane, errorMessage,
                                          errorMessage, JOptionPane.ERROR_MESSAGE);
    }

    public short getNewSubscription(){
        return newSubscription;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelTitle = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane = new javax.swing.JEditorPane();
        jPanel2 = new javax.swing.JPanel();
        jPanelVoucher = new javax.swing.JPanel();
        jLabelVoucherCode = new javax.swing.JLabel();
        jTextFieldVoucherCode = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jButtonActivate = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelWest.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelEast.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelTitle.setMaximumSize(new java.awt.Dimension(32767, 52));
        jPanelTitle.setMinimumSize(new java.awt.Dimension(80, 52));
        jPanelTitle.setPreferredSize(new java.awt.Dimension(80, 52));
        jPanelTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/speech_balloon_answer.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanelTitle.add(jLabelTitle);

        jPanelCenter.add(jPanelTitle);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jEditorPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jEditorPane.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jEditorPane);

        jPanelCenter.add(jScrollPane1);

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanel2.setMinimumSize(new java.awt.Dimension(10, 4));
        jPanel2.setPreferredSize(new java.awt.Dimension(10, 4));
        jPanelCenter.add(jPanel2);

        jPanelVoucher.setMaximumSize(new java.awt.Dimension(98301, 34));
        jPanelVoucher.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jLabelVoucherCode.setText("jLabelVoucherCode");
        jPanelVoucher.add(jLabelVoucherCode);

        jTextFieldVoucherCode.setText("jTextFieldVoucherCode");
        jTextFieldVoucherCode.setMinimumSize(new java.awt.Dimension(220, 22));
        jTextFieldVoucherCode.setPreferredSize(new java.awt.Dimension(220, 22));
        jPanelVoucher.add(jTextFieldVoucherCode);

        jPanelCenter.add(jPanelVoucher);

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanel1.setMinimumSize(new java.awt.Dimension(10, 4));
        jPanel1.setPreferredSize(new java.awt.Dimension(10, 4));
        jPanelCenter.add(jPanel1);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonActivate.setText("jButtonRenew");
        jButtonActivate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActivateActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonActivate);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonCancel);

        jPanel5.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel5.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel5.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelSouth.add(jPanel5);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonActivateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActivateActionPerformed
        doIt();
    }//GEN-LAST:event_jButtonActivateActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ActivateSubscriptionDialog dialog = new ActivateSubscriptionDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonActivate;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelVoucherCode;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelVoucher;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldVoucherCode;
    // End of variables declaration//GEN-END:variables
}
