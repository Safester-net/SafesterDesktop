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
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.AppDateFormat;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.util.TestAwakeConnection;
import net.safester.noobs.clientserver.LoginLogLocal2;

public class LastLogin extends javax.swing.JFrame {

    private ClipboardManager clipboardManager;
    private MessagesManager messages = new MessagesManager();

    private JFrame thisOne;

    private JFrame parent;
    private Connection connection;
    private int userNumber;
    private String keyId;

    /** Creates new form SafeShareItSettings */
    public LastLogin(JFrame jFrame, Connection theConnection, int theUserNumber, String keyId) {
        initComponents();
        parent = jFrame;

        thisOne = this;
        
        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        userNumber = theUserNumber;
        this.keyId = keyId;
        initCompany();
    }

    private void initCompany() {
        
        clipboardManager = new ClipboardManager(rootPane);

        this.setTitle(messages.getMessage("last_login") + " - " + this.keyId);
        this.jLabelTitle.setText(this.getTitle());
        
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.jLabelDateTime.setText(messages.getMessage("date_time"));
        this.jLabelIp.setText(messages.getMessage("ip_address"));
        this.jLabelHostname.setText(messages.getMessage("isp_hostname"));

        jButtonOk.setText(messages.getMessage("ok"));
        
        try {
            loadData();
        } catch (Exception ex) {
            JOptionPaneNewCustom.showException(parent, ex);
        }
        
        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        this.setLocationByPlatform(true);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
            }
        });
        
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e)
            {
               WindowSettingManager.save(thisOne);
            }
            
            @Override
            public void componentResized(ComponentEvent e)
            {
                WindowSettingManager.save(thisOne);
            }

        });

        this.keyListenerAdder();
        this.setLocationRelativeTo(parent);

        this.setSize(new Dimension(532, 258));

        WindowSettingManager.load(this);
        
        SwingUtil.applySwingUpdates(rootPane);
    }


    /**
     * Load last login info from remote SQL and display it in window
     */
    private void loadData() throws SQLException {
        
        AppDateFormat df = new AppDateFormat();
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        String jsonString = null;
        try {
            jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.LoginLogNewApi2.getPreviousLogin",
                    userNumber,
                    connection);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        
        Gson gsonOut = new Gson();
	java.lang.reflect.Type type = new TypeToken<LoginLogLocal2>() {
	}.getType();
	LoginLogLocal2 loginLogLocal2 = gsonOut.fromJson(jsonString, type);
        
        if (loginLogLocal2 != null && loginLogLocal2.getUser_number() > 0) {
            Timestamp dateTime = new Timestamp(loginLogLocal2.getDate_time());
            String ipAddress = loginLogLocal2.getIp_address();
            String hostname = loginLogLocal2.getHostname();

            String formatedDateTime = df.format(dateTime);

            this.jTextFieldDateTime.setText(formatedDateTime);
            this.jTextFieldIp.setText(ipAddress);

            if (hostname.equals(ipAddress))
            {
                this.jTextFieldHostname.setText(messages.getMessage("unknown"));
            }
            else
            {
                this.jTextFieldHostname.setText(hostname);
            }

            this.jTextFieldHostname.setCaretPosition(0);
        }
        
        
    }
        

    /**
     * Universal key listener
     *
     */
    private void keyListenerAdder() {
        java.util.List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    this_keyPressed(e);
                }
            });
        }
    }

    
    private void this_keyPressed(KeyEvent e) {
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_ENTER  ) {
                this.dispose();
            }

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

        jPanelNorth = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelTop = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelSepLine1 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelSep13 = new javax.swing.JPanel();
        jPanelNameAndEmail = new javax.swing.JPanel();
        jPanelDateTime = new javax.swing.JPanel();
        jLabelDateTime = new javax.swing.JLabel();
        jTextFieldDateTime = new javax.swing.JTextField();
        jPanelIP = new javax.swing.JPanel();
        jLabelIp = new javax.swing.JLabel();
        jTextFieldIp = new javax.swing.JTextField();
        jPanelHostname = new javax.swing.JPanel();
        jLabelHostname = new javax.swing.JLabel();
        jTextFieldHostname = new javax.swing.JTextField();
        jPanelSepBlank1 = new javax.swing.JPanel();
        jPanelSepLine2 = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelTop.setMaximumSize(new java.awt.Dimension(32767, 45));
        jPanelTop.setMinimumSize(new java.awt.Dimension(94, 45));
        jPanelTop.setPreferredSize(new java.awt.Dimension(45, 45));
        jPanelTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/calendar_clock.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanelTop.add(jLabelTitle);

        jPanelCenter.add(jPanelTop);

        jPanelSepLine1.setMaximumSize(new java.awt.Dimension(32767, 6));
        jPanelSepLine1.setMinimumSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.setLayout(new javax.swing.BoxLayout(jPanelSepLine1, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.add(jSeparator3);

        jPanelCenter.add(jPanelSepLine1);

        jPanelSep13.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep13.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSep13.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelCenter.add(jPanelSep13);

        jPanelNameAndEmail.setPreferredSize(new java.awt.Dimension(422, 208));
        jPanelNameAndEmail.setLayout(new javax.swing.BoxLayout(jPanelNameAndEmail, javax.swing.BoxLayout.Y_AXIS));

        jPanelDateTime.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelDateTime.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelDateTime.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelDateTime.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelDateTime.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelDateTime.setText("jLabelDateTime");
        jLabelDateTime.setPreferredSize(new java.awt.Dimension(150, 14));
        jPanelDateTime.add(jLabelDateTime);

        jTextFieldDateTime.setEditable(false);
        jTextFieldDateTime.setText("jTextFieldDateTime");
        jTextFieldDateTime.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldDateTime.setMinimumSize(new java.awt.Dimension(20, 22));
        jTextFieldDateTime.setPreferredSize(new java.awt.Dimension(190, 22));
        jPanelDateTime.add(jTextFieldDateTime);

        jPanelNameAndEmail.add(jPanelDateTime);

        jPanelIP.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelIP.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelIP.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelIP.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelIp.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelIp.setText("jLabelIp");
        jLabelIp.setPreferredSize(new java.awt.Dimension(150, 14));
        jPanelIP.add(jLabelIp);

        jTextFieldIp.setEditable(false);
        jTextFieldIp.setText("jTextFieldIp");
        jTextFieldIp.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldIp.setPreferredSize(new java.awt.Dimension(190, 22));
        jPanelIP.add(jTextFieldIp);

        jPanelNameAndEmail.add(jPanelIP);

        jPanelHostname.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelHostname.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelHostname.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelHostname.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelHostname.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelHostname.setText("jLabelHostname");
        jLabelHostname.setPreferredSize(new java.awt.Dimension(150, 14));
        jPanelHostname.add(jLabelHostname);

        jTextFieldHostname.setEditable(false);
        jTextFieldHostname.setText("jTextFieldHostname");
        jTextFieldHostname.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldHostname.setPreferredSize(new java.awt.Dimension(320, 22));
        jPanelHostname.add(jTextFieldHostname);

        jPanelNameAndEmail.add(jPanelHostname);

        jPanelSepBlank1.setMaximumSize(new java.awt.Dimension(32767, 7));
        jPanelSepBlank1.setMinimumSize(new java.awt.Dimension(0, 7));
        jPanelSepBlank1.setPreferredSize(new java.awt.Dimension(0, 7));
        jPanelSepBlank1.setLayout(new javax.swing.BoxLayout(jPanelSepBlank1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelNameAndEmail.add(jPanelSepBlank1);

        jPanelCenter.add(jPanelNameAndEmail);

        jPanelSepLine2.setLayout(new javax.swing.BoxLayout(jPanelSepLine2, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator4.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator4.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator4.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine2.add(jSeparator4);

        jPanelCenter.add(jPanelSepLine2);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonOk.setText("jButtonOk");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonOk);

        jPanel9.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel9.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel9.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelSouth.add(jPanel9);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
       this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                Connection connection = TestAwakeConnection.getConnection();

                new LastLogin(null, connection, 2, "contact@safester.net").setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelDateTime;
    private javax.swing.JLabel jLabelHostname;
    private javax.swing.JLabel jLabelIp;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelDateTime;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelHostname;
    private javax.swing.JPanel jPanelIP;
    private javax.swing.JPanel jPanelNameAndEmail;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep13;
    private javax.swing.JPanel jPanelSepBlank1;
    private javax.swing.JPanel jPanelSepLine1;
    private javax.swing.JPanel jPanelSepLine2;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextField jTextFieldDateTime;
    private javax.swing.JTextField jTextFieldHostname;
    private javax.swing.JTextField jTextFieldIp;
    // End of variables declaration//GEN-END:variables

}
