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

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ConnectionParms;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.util.TestAwakeConnection;

import org.awakefw.sql.api.client.AwakeConnection;

/**
 * This dialog is displayed when user try to access
 * a feature that is not available with his current subscription
 * Message displayed depends on the feature called
 * 
 * @author Alexandre Becquereau
 */
public class UnavailableFeatureDialog extends javax.swing.JDialog {

    private int userNumber;
    private Connection connection = null;

    private MessagesManager messages = new MessagesManager();

    //Message to be displayed
    private String messageToDisplay;

    private Frame parent = null;

    public UnavailableFeatureDialog(java.awt.Frame parent, int userNumber, Connection theConnection, String messageToDisplay, boolean modal) {
        super(parent, modal);

        this.parent = parent;
        this.userNumber = userNumber;
        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();

        initComponents();
        this.messageToDisplay = messageToDisplay;
        initCompany();
        this.setLocationRelativeTo(parent);
    }

    private void initCompany(){

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
        
        this.setTitle(messages.getMessage("upgrade_safester_account"));
        this.jLabelTitle.setText(this.getTitle());

        jPanelCenter.remove(jPanelRadioButtons);

        this.jRadioButtonStarter.setText(messages.getMessage("starter"));
        this.jRadioButtonBasic.setText(messages.getMessage("basic"));
        this.jRadioButtonSilver.setText(messages.getMessage("silver"));
        this.jRadioButtonGold.setText(messages.getMessage("gold"));
        this.jRadioButtonPlatinum.setText(messages.getMessage("platinum"));

        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        jEditorPane.setText(messageToDisplay);

        this.jButtonRenew.setText(messages.getMessage("upgrade"));
        this.jButtonCancel.setText(messages.getMessage("later"));

        jRadioButtonSilver.setSelected(true);
        
        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        this.setSize(450, 450);

        WindowSettingManager.load(this);

        // These 2 stupid lines : only to Force to display top of file first
        jEditorPane.moveCaretPosition(0);
        jEditorPane.setSelectionEnd(0);
    }


    /** Close window ans says we are logged out */
    public void close()
    {
        WindowSettingManager.save(this);
    }

    private void doIt(){
        try {
            
            BuyDialog buyDialog = new BuyDialog(this.parent, connection, userNumber, true);
            buyDialog.setVisible(true);

            if (buyDialog.getNewSubscription() != StoreParms.PRODUCT_FREE) {
                short userSubscription = buyDialog.getNewSubscription();
                ConnectionParms.setSubscription(userSubscription);
            }
            
            this.dispose();
        } catch (Exception ex) {
            JOptionPaneNewCustom.showException(this, ex);
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

        buttonGroupProduct = new javax.swing.ButtonGroup();
        jPanelNorth = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelTitle = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane = new javax.swing.JEditorPane();
        jPanelRadioButtons = new javax.swing.JPanel();
        jPanelBasicStarter = new javax.swing.JPanel();
        jRadioButtonStarter = new javax.swing.JRadioButton();
        jPanelBasic = new javax.swing.JPanel();
        jRadioButtonBasic = new javax.swing.JRadioButton();
        jPanelSilver = new javax.swing.JPanel();
        jRadioButtonSilver = new javax.swing.JRadioButton();
        jPanelGold = new javax.swing.JPanel();
        jRadioButtonGold = new javax.swing.JRadioButton();
        jPanelPlatinium = new javax.swing.JPanel();
        jRadioButtonPlatinum = new javax.swing.JRadioButton();
        jPanelBottom = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
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

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelTitle.setMaximumSize(new java.awt.Dimension(32767, 52));
        jPanelTitle.setMinimumSize(new java.awt.Dimension(80, 52));
        jPanelTitle.setPreferredSize(new java.awt.Dimension(80, 52));
        jPanelTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/speech_balloon_answer.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanelTitle.add(jLabelTitle);

        jPanelCenter.add(jPanelTitle);

        jScrollPane1.setBorder(null);

        jEditorPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jEditorPane.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jEditorPane);

        jPanelCenter.add(jScrollPane1);

        jPanelRadioButtons.setMaximumSize(new java.awt.Dimension(98301, 34));
        jPanelRadioButtons.setLayout(new javax.swing.BoxLayout(jPanelRadioButtons, javax.swing.BoxLayout.LINE_AXIS));

        jPanelBasicStarter.setBackground(new java.awt.Color(255, 255, 255));

        jRadioButtonStarter.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupProduct.add(jRadioButtonStarter);
        jRadioButtonStarter.setText("jRadioButtonStarter");
        jPanelBasicStarter.add(jRadioButtonStarter);

        jPanelRadioButtons.add(jPanelBasicStarter);

        jPanelBasic.setBackground(new java.awt.Color(255, 255, 255));

        jRadioButtonBasic.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupProduct.add(jRadioButtonBasic);
        jRadioButtonBasic.setText("jRadioButtonPlatinum");
        jPanelBasic.add(jRadioButtonBasic);

        jPanelRadioButtons.add(jPanelBasic);

        jPanelSilver.setBackground(new java.awt.Color(255, 255, 255));

        jRadioButtonSilver.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupProduct.add(jRadioButtonSilver);
        jRadioButtonSilver.setText("jRadioButtonSilver");
        jPanelSilver.add(jRadioButtonSilver);

        jPanelRadioButtons.add(jPanelSilver);

        jPanelGold.setBackground(new java.awt.Color(255, 255, 255));

        jRadioButtonGold.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupProduct.add(jRadioButtonGold);
        jRadioButtonGold.setText("jRadioButtonGold");
        jPanelGold.add(jRadioButtonGold);

        jPanelRadioButtons.add(jPanelGold);

        jPanelPlatinium.setBackground(new java.awt.Color(255, 255, 255));

        jRadioButtonPlatinum.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupProduct.add(jRadioButtonPlatinum);
        jRadioButtonPlatinum.setText("jRadioButtonPlatinum");
        jPanelPlatinium.add(jRadioButtonPlatinum);

        jPanelRadioButtons.add(jPanelPlatinium);

        jPanelCenter.add(jPanelRadioButtons);

        jPanelBottom.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelBottom.setMinimumSize(new java.awt.Dimension(10, 4));
        jPanelBottom.setPreferredSize(new java.awt.Dimension(10, 4));
        jPanelCenter.add(jPanelBottom);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonRenew.setText("jButtonRenew");
        jButtonRenew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRenewActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonRenew);

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

    private void jButtonRenewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRenewActionPerformed
        doIt();
    }//GEN-LAST:event_jButtonRenewActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String htmlMessage = HtmlTextUtil.getHtmlHelpContent("upgrade_storage_capacity_exceeded");
                Connection connection = TestAwakeConnection.getConnection();
                UnavailableFeatureDialog dialog = new UnavailableFeatureDialog(null, -1, connection, htmlMessage, true);

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
    private javax.swing.JButton jButtonRenew;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelBasic;
    private javax.swing.JPanel jPanelBasicStarter;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelGold;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelPlatinium;
    private javax.swing.JPanel jPanelRadioButtons;
    private javax.swing.JPanel jPanelSilver;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JRadioButton jRadioButtonBasic;
    private javax.swing.JRadioButton jRadioButtonGold;
    private javax.swing.JRadioButton jRadioButtonPlatinum;
    private javax.swing.JRadioButton jRadioButtonSilver;
    private javax.swing.JRadioButton jRadioButtonStarter;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
