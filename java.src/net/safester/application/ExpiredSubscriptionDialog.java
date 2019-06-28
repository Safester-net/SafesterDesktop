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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.Connection;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.DesktopWrapper;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.UserPrefManager;
import net.safester.clientserver.ServerParms;
import net.safester.clientserver.util.TestAwakeConnection;

import org.awakefw.sql.api.client.AwakeConnection;


/**
 * This dialog is displayed on connection when active subscription is expired
 * @author Alexandre Becquereau
 */
public class ExpiredSubscriptionDialog extends javax.swing.JDialog {

    private MessagesManager messages = new MessagesManager();
    short expiredSubscription;

    private int userNumber;
    private short newSubscription = StoreParms.PRODUCT_FREE;

    private Connection connection;

    Frame caller;
    
    public ExpiredSubscriptionDialog(java.awt.Frame parent, Connection theConnection, int usernumber, short expiredSubscription, boolean modal) {
        super(parent, modal);
        initComponents();
        this.expiredSubscription = expiredSubscription;
        this.userNumber = usernumber;

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();

        caller = parent;
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

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        
        this.setTitle(messages.getMessage("subscription_expired"));
        this.jLabelTitle.setText(messages.getMessage("subscription_expired"));

        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        jEditorPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                try {
                    final URL url = e.getURL();
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        DesktopWrapper.browse(url);
                    }
                } catch (Exception ex) {
                    JOptionPaneNewCustom.showException(rootPane, ex);
                }

            }
        });

        jEditorPane.setText(HtmlTextUtil.getHtmlHelpContent("subscription_expired"));
        this.jButtonEnterCode.setText(messages.getMessage("enter_activation_code"));
        this.jButtonRenew.setText(messages.getMessage("renew"));
        this.jButtonCancel.setText(messages.getMessage("later"));
        
        jCheckBoxDiscard.setText(messages.getMessage("DO_NOT_SHOW_ME_AGAIN_MSG"));
        
        ButtonResizer br = new ButtonResizer(jPanelButton);
        br.setWidthToMax();
        this.setSize(430, 430);

        WindowSettingManager.load(this);

    }

    /** Close window ans says we are logged out */
    public void close()
    {
        WindowSettingManager.save(this);
    }
    
    private void doIt() {
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            String url = ServerParms.getHOST() + "/pricing";
            
            DesktopWrapper.browse(url);
            this.setCursor(Cursor.getDefaultCursor());
            //this.dispose();
            return;
        } catch (Exception ex) {
            JOptionPaneNewCustom.showException(this, ex);
        }
    }

    public short getNewSubscription() {
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

        buttonGroupProduct = new javax.swing.ButtonGroup();
        jPanelNorth = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelTitle = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane = new javax.swing.JEditorPane();
        jPanelSepBottom = new javax.swing.JPanel();
        jPanelDiscard = new javax.swing.JPanel();
        jCheckBoxDiscard = new javax.swing.JCheckBox();
        jPanelSepBottom1 = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jButtonEnterCode = new javax.swing.JButton();
        jPanelButton = new javax.swing.JPanel();
        jButtonRenew = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelWest.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelEast.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setBackground(new java.awt.Color(255, 255, 255));
        jPanelCenter.setRequestFocusEnabled(false);
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

        jEditorPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jEditorPane.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jEditorPane);

        jPanelCenter.add(jScrollPane1);

        jPanelSepBottom.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelSepBottom.setMinimumSize(new java.awt.Dimension(10, 4));
        jPanelSepBottom.setPreferredSize(new java.awt.Dimension(10, 4));
        jPanelCenter.add(jPanelSepBottom);

        jPanelDiscard.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelDiscard.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jCheckBoxDiscard.setText("jCheckBoxDiscard");
        jPanelDiscard.add(jCheckBoxDiscard);

        jPanelCenter.add(jPanelDiscard);

        jPanelSepBottom1.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelSepBottom1.setMinimumSize(new java.awt.Dimension(10, 4));
        jPanelSepBottom1.setPreferredSize(new java.awt.Dimension(10, 4));
        jPanelCenter.add(jPanelSepBottom1);

        jPanelSep.setMinimumSize(new java.awt.Dimension(10, 6));
        jPanelSep.setPreferredSize(new java.awt.Dimension(10, 6));
        jPanelSep.setLayout(new javax.swing.BoxLayout(jPanelSep, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSep.add(jSeparator2);

        jPanelCenter.add(jPanelSep);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new javax.swing.BoxLayout(jPanelSouth, javax.swing.BoxLayout.LINE_AXIS));

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        jPanel8.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel8.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel8.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanel7.add(jPanel8);

        jButtonEnterCode.setText("jButtonEnterCode");
        jButtonEnterCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnterCodeActionPerformed(evt);
            }
        });
        jPanel7.add(jButtonEnterCode);

        jPanelSouth.add(jPanel7);

        jPanelButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonRenew.setText("jButtonRenew");
        jButtonRenew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRenewActionPerformed(evt);
            }
        });
        jPanelButton.add(jButtonRenew);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelButton.add(jButtonCancel);

        jPanel5.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel5.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel5.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelButton.add(jPanel5);

        jPanelSouth.add(jPanelButton);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed

        if (jCheckBoxDiscard.isSelected())
        {
            UserPrefManager.setPreference(UserPrefManager.EXPIRED_SUBSCRIPTION_DIALOG_DISCARD, true);
        }
        
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonRenewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRenewActionPerformed
        doIt();
    }//GEN-LAST:event_jButtonRenewActionPerformed

    private void jButtonEnterCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnterCodeActionPerformed

        ActivateSubscriptionDialog activateSubscriptionDialog = new ActivateSubscriptionDialog(caller, userNumber, true, connection);
        activateSubscriptionDialog.setVisible(true);
        if(activateSubscriptionDialog.getNewSubscription() != (short)-1){
            this.newSubscription = activateSubscriptionDialog.getNewSubscription();
            this.setVisible(false);
        }
        activateSubscriptionDialog.dispose();

    }//GEN-LAST:event_jButtonEnterCodeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                Connection connection = TestAwakeConnection.getConnection();

                ExpiredSubscriptionDialog dialog = new ExpiredSubscriptionDialog(new javax.swing.JFrame(), connection, 1, (short)1, true);
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
    private javax.swing.ButtonGroup buttonGroupProduct;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonEnterCode;
    private javax.swing.JButton jButtonRenew;
    private javax.swing.JCheckBox jCheckBoxDiscard;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelDiscard;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSepBottom;
    private javax.swing.JPanel jPanelSepBottom1;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
}
