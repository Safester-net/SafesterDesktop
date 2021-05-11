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
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.UserPrefManager;


/**
 *
 * @author  Nicolas de Pomereu
 */
public class FrameProxyParms extends javax.swing.JFrame {
    
    public static final String PROXY_ADDRESS    = "PROXY_ADDRESS";
    public static final String PROXY_AUTH       = "PROXY_AUTH";
    public static final String PROXY_PASSWORD   = "PROXY_PASSWORD";
    public static final String PROXY_PORT       = "PROXY_PORT";
    public static final String PROXY_USE        = "PROXY_USE";
    public static final String PROXY_USERNAME   = "PROXY_USERNAME";

    /** The parent Window */
    private JFrame parent = null;

    private JFrame thisOne;
    private MessagesManager messages = new MessagesManager();
    
    private ClipboardManager clipboardManager;

    /** Creates new form FrameProxyParms*/
    public FrameProxyParms(JFrame parent) {
        
        this.parent = parent; 
        thisOne = this;
        initComponents();
        initializeCompany();
        
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
                
    }

    /**
     * This is the method to include in *our* constructor
     */
    public void initializeCompany()
    {
        clipboardManager = new ClipboardManager(rootPane);
        
        Dimension dim = new Dimension(428, 344);
        this.setSize(dim);
        this.setPreferredSize(dim);

        TitledBorder titledBorder =
            new TitledBorder(this.messages.getMessage("proxy_activation"));
        this.jPanelProxyContainer.setBorder(titledBorder);
        
        buttonGroup1.add(jRadioButtonBrowserParameters);
        buttonGroup1.add(jRadioButtonUseProxy);
        buttonGroup1.add(jRadioButtonDirectConnection);
        
        try
        {
            this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        } 
            
        this.setTitle(messages.getMessage("proxy_settings"));

        this.jTextFieldProxyAddress.setText(null);
        this.jTextFieldProxyPort.setText(null);

        //this.jCheckBoxNtlmProxy.setText(messages.getMessage("ntlm_authentication"));
        //this.jTextFieldWorkstation.setText(null);
        //this.jTextFieldDomain.setText(null);
        ///this.jLabelWorkstation.setText(messages.getMessage("workstation"));
        //this.jLabelDomain.setText(messages.getMessage("domain"));

        jLabelLogoTitle.setText(messages.getMessage("proxy_settings"));

        jRadioButtonBrowserParameters.setText(messages.getMessage("use_browser_parameters"));
        jRadioButtonUseProxy.setText(messages.getMessage("use_proxy_server"));
        jRadioButtonDirectConnection.setText(messages.getMessage("direct_connection"));
        
        jLabelProxyAddress.setText(messages.getMessage("address"));
        jLabelProxyPort.setText(messages.getMessage("port"));

        //jButtonHelpNtlm.putClientProperty( "JButton.buttonType", "square" );

        jButtonApply.setText(messages.getMessage("ok"));
        jButtonClose.setText(messages.getMessage("cancel"));

        // Set the Send preferences for user Preferences
        setStoredPreferences();
        jRadioButtonUseProxyItemStateChanged(null);
        //jCheckBoxNtlmProxyItemStateChanged(null);

        jTextFieldProxyPort.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e)
            {
                if((int)e.getKeyChar()>=48 && (int)e.getKeyChar()<=57)
                {
                    // Ok. Numeric values
                    return;
                }
                else
                {
                    if ((int)e.getKeyChar() != KeyEvent.VK_BACK_SPACE)
                    {
                        Toolkit.getDefaultToolkit().beep();
                        e.consume();
                    }
                }

            }
        });
        
        ButtonResizer buttonResizer = new ButtonResizer(jPanelButtons);
        buttonResizer.setWidthToMax();
                
        this.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e)
            {
               saveSettings();
            }

            public void componentResized(ComponentEvent e)
            {
                saveSettings();
            }

        });
        
        // Our window listener for all events
        // If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
                close();
            }
        });
 
        this.keyListenerAdder();       
        
        SwingUtil.resizeJComponentsForAll(rootPane);
        
        this.setLocationByPlatform(true);
        this.setLocationRelativeTo(parent);
        WindowSettingManager.load(this);

    }    
    

    /**
     * Set the Send Preferences found in User Preferences (Registry)
     */
    private void setStoredPreferences()
    {
        jRadioButtonBrowserParameters.setSelected(false);
        jRadioButtonUseProxy.setSelected(false);
        jRadioButtonDirectConnection.setSelected(false);
        
        int proxyType= UserPrefManager.getIntegerPreference(UserPrefManager.PROXY_TYPE);

        if (proxyType == UserPrefManager.PROXY_TYPE_BROWSER_DEF)
        {
            jRadioButtonBrowserParameters.setSelected(true);
        }
        else if (proxyType == UserPrefManager.PROXY_TYPE_USER_DEF)
        {
            jRadioButtonUseProxy.setSelected(true);
        }
        else if (proxyType == UserPrefManager.PROXY_TYPE_DIRECT)
        {
            jRadioButtonDirectConnection.setSelected(true);
        }
        else
        {
            throw new IllegalArgumentException("Proxy Type is invalid: " + proxyType);
        }        

        jTextFieldProxyAddress.setText(UserPrefManager.getPreference(UserPrefManager.PROXY_ADDRESS));
        jTextFieldProxyPort.setText(UserPrefManager.getPreference(UserPrefManager.PROXY_PORT));

        //jCheckBoxNtlmProxy.setSelected(UserPrefManager.getBooleanPreference(UserPrefManager.PROXY_AUTH_NTLM));
        //jTextFieldWorkstation.setText(UserPrefManager.getPreference(UserPrefManager.NTLM_WORKSTATION));
        //jTextFieldDomain.setText(UserPrefManager.getPreference(UserPrefManager.NTLM_DOMAIN));

    }

    private void close()
    {
        WindowSettingManager.save(this);
        this.dispose();
    }
    
    /** 
     * Universal key listener
     */
    private void keyListenerAdder()
    {
        List<Component> components = SwingUtil.getAllComponants(this);
        
        for (int i = 0; i < components.size(); i++)
        {
            Component comp = components.get(i);
            
            comp.addKeyListener(new KeyAdapter() { 
                public void keyReleased(KeyEvent e) 
                { 
                    this_keyReleased(e); 
                } 
            }); 
        }
    }   
    
    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    ///////////////////////////////////////////////////////////////////////////    
    
    private void this_keyReleased(KeyEvent e) 
    {        
        //System.out.println("this_keyReleased(KeyEvent e) " + e.getComponent().getName()); 
        int id = e.getID();
        if (id == KeyEvent.KEY_RELEASED) 
        { 
            int keyCode = e.getKeyCode();
          
            if (keyCode == KeyEvent.VK_ENTER)
            {
                actionOk();
            }
            
            if (keyCode == KeyEvent.VK_ESCAPE)
            {
               actionCancel();
            }  
            
            if (keyCode == KeyEvent.VK_F1)
            {
               //jButtonHelpActionPerformed(null);
            }              
        }       
    } 
        
    /**
     * Done if OK Button hit
     */
    private void actionOk()
    {
        int proxyType = 0;
        
        if (jRadioButtonBrowserParameters.isSelected())
        {
            proxyType = UserPrefManager.PROXY_TYPE_BROWSER_DEF;
        }
        else if (jRadioButtonUseProxy.isSelected())
        {
            proxyType = UserPrefManager.PROXY_TYPE_USER_DEF;
        }
        else if (jRadioButtonDirectConnection.isSelected())
        {
            proxyType = UserPrefManager.PROXY_TYPE_DIRECT;
        }        


        if(proxyType == UserPrefManager.PROXY_TYPE_USER_DEF)
        {
            String proxyAddress = jTextFieldProxyAddress.getText();
            String proxyPort = jTextFieldProxyPort.getText();
         
            if (proxyAddress == null || proxyAddress.length() <=1 ) {
                JOptionPane.showMessageDialog(null, MessagesManager.get("proxy_address_cannot_be_empty"), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            UserPrefManager.setPreference(UserPrefManager.PROXY_ADDRESS, proxyAddress);
            UserPrefManager.setPreference(UserPrefManager.PROXY_PORT, proxyPort);
        }

        UserPrefManager.setPreference(UserPrefManager.PROXY_TYPE, proxyType);
        
        //UserPrefManager.setPreference(UserPrefManager.PROXY_AUTH_NTLM, jCheckBoxNtlmProxy.isSelected());
        //UserPrefManager.setPreference(UserPrefManager.NTLM_WORKSTATION, jTextFieldWorkstation.getText());
        //UserPrefManager.setPreference(UserPrefManager.NTLM_DOMAIN, jTextFieldDomain.getText());
        
        close();
    }

    
    /**
     * Done if Cancel Button hit
     */
    private void actionCancel()
    {
        close();
    }    
    
    
    public void saveSettings()
    {
        WindowSettingManager.save(this);
    }
    
   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel5 = new javax.swing.JPanel();
        jPanelBorderTop = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabelLogoTitle = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelSepLine1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel8 = new javax.swing.JPanel();
        jPanelSep4 = new javax.swing.JPanel();
        jPanelProxy = new javax.swing.JPanel();
        jPanelLetf2 = new javax.swing.JPanel();
        jPanelProxyContainer = new javax.swing.JPanel();
        jPaneProxyOptions = new javax.swing.JPanel();
        jPanelBrowserDef = new javax.swing.JPanel();
        jRadioButtonBrowserParameters = new javax.swing.JRadioButton();
        jPanelUserDef = new javax.swing.JPanel();
        jRadioButtonUseProxy = new javax.swing.JRadioButton();
        jPanelProxyAddressAndPort = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabelProxyAddress = new javax.swing.JLabel();
        jTextFieldProxyAddress = new javax.swing.JTextField();
        jLabelProxyPort = new javax.swing.JLabel();
        jTextFieldProxyPort = new javax.swing.JTextField();
        jPanelUserDef1 = new javax.swing.JPanel();
        jRadioButtonDirectConnection = new javax.swing.JRadioButton();
        jPanelRight2 = new javax.swing.JPanel();
        jPanelSep3 = new javax.swing.JPanel();
        jPanelSepLine = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel7 = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonApply = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(525, 425));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanel5);

        jPanelBorderTop.setLayout(new javax.swing.BoxLayout(jPanelBorderTop, javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setMaximumSize(new java.awt.Dimension(32767, 38));
        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 3));

        jLabelLogoTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelLogoTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/server_network.png"))); // NOI18N
        jLabelLogoTitle.setText("Settings");
        jPanel12.add(jLabelLogoTitle);

        jPanel6.add(jPanel12);

        jPanel13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 15, 3));
        jPanel6.add(jPanel13);

        jPanelBorderTop.add(jPanel6);

        getContentPane().add(jPanelBorderTop);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(1000, 10));
        getContentPane().add(jPanelSep2);

        jPanelSepLine1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepLine1.setPreferredSize(new java.awt.Dimension(20, 8));
        jPanelSepLine1.setLayout(new javax.swing.BoxLayout(jPanelSepLine1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel2.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelSepLine1.add(jPanel2);
        jPanelSepLine1.add(jSeparator3);

        jPanel8.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelSepLine1.add(jPanel8);

        getContentPane().add(jPanelSepLine1);

        jPanelSep4.setMaximumSize(new java.awt.Dimension(32767, 5000));
        jPanelSep4.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSep4.setPreferredSize(new java.awt.Dimension(1000, 5));
        getContentPane().add(jPanelSep4);

        jPanelProxy.setLayout(new javax.swing.BoxLayout(jPanelProxy, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLetf2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelProxy.add(jPanelLetf2);

        jPanelProxyContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelProxyContainer.setLayout(new javax.swing.BoxLayout(jPanelProxyContainer, javax.swing.BoxLayout.LINE_AXIS));

        jPaneProxyOptions.setLayout(new javax.swing.BoxLayout(jPaneProxyOptions, javax.swing.BoxLayout.Y_AXIS));

        jPanelBrowserDef.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelBrowserDef.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jRadioButtonBrowserParameters.setText("Use Browser Parameters");
        jPanelBrowserDef.add(jRadioButtonBrowserParameters);

        jPaneProxyOptions.add(jPanelBrowserDef);

        jPanelUserDef.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelUserDef.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jRadioButtonUseProxy.setText("Use  Proxy Server");
        jRadioButtonUseProxy.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonUseProxyItemStateChanged(evt);
            }
        });
        jPanelUserDef.add(jRadioButtonUseProxy);

        jPaneProxyOptions.add(jPanelUserDef);

        jPanelProxyAddressAndPort.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelProxyAddressAndPort.setMinimumSize(new java.awt.Dimension(289, 33));
        jPanelProxyAddressAndPort.setPreferredSize(new java.awt.Dimension(294, 33));
        jPanelProxyAddressAndPort.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanel3.setPreferredSize(new java.awt.Dimension(15, 10));
        jPanel3.setRequestFocusEnabled(false);
        jPanelProxyAddressAndPort.add(jPanel3);

        jLabelProxyAddress.setText("Address");
        jPanelProxyAddressAndPort.add(jLabelProxyAddress);

        jTextFieldProxyAddress.setText("jTextFieldProxyAddress");
        jTextFieldProxyAddress.setMaximumSize(new java.awt.Dimension(130, 22));
        jTextFieldProxyAddress.setMinimumSize(new java.awt.Dimension(130, 22));
        jTextFieldProxyAddress.setPreferredSize(new java.awt.Dimension(130, 22));
        jPanelProxyAddressAndPort.add(jTextFieldProxyAddress);

        jLabelProxyPort.setText("Port");
        jPanelProxyAddressAndPort.add(jLabelProxyPort);

        jTextFieldProxyPort.setText("jTextFieldProxyPort");
        jTextFieldProxyPort.setMaximumSize(new java.awt.Dimension(60, 22));
        jTextFieldProxyPort.setMinimumSize(new java.awt.Dimension(60, 22));
        jTextFieldProxyPort.setPreferredSize(new java.awt.Dimension(60, 22));
        jPanelProxyAddressAndPort.add(jTextFieldProxyPort);

        jPaneProxyOptions.add(jPanelProxyAddressAndPort);

        jPanelUserDef1.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelUserDef1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jRadioButtonDirectConnection.setText("Direct Connection");
        jPanelUserDef1.add(jRadioButtonDirectConnection);

        jPaneProxyOptions.add(jPanelUserDef1);

        jPanelProxyContainer.add(jPaneProxyOptions);

        jPanelProxy.add(jPanelProxyContainer);

        jPanelRight2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelProxy.add(jPanelRight2);

        getContentPane().add(jPanelProxy);

        jPanelSep3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSep3.setMinimumSize(new java.awt.Dimension(10, 100));
        jPanelSep3.setPreferredSize(new java.awt.Dimension(1000, 10));
        getContentPane().add(jPanelSep3);

        jPanelSepLine.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepLine.setPreferredSize(new java.awt.Dimension(20, 8));
        jPanelSepLine.setLayout(new javax.swing.BoxLayout(jPanelSepLine, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelSepLine.add(jPanel1);
        jPanelSepLine.add(jSeparator2);

        jPanel7.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelSepLine.add(jPanel7);

        getContentPane().add(jPanelSepLine);

        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonApply.setText("OK");
        jButtonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApplyActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonApply);

        jButtonClose.setText("Cancel");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonClose);

        jPanel4.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelButtons.add(jPanel4);

        getContentPane().add(jPanelButtons);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    actionCancel();
}//GEN-LAST:event_jButtonCloseActionPerformed

private void jButtonApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApplyActionPerformed
    actionOk();
}//GEN-LAST:event_jButtonApplyActionPerformed

private void jRadioButtonUseProxyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonUseProxyItemStateChanged
    if (jRadioButtonUseProxy.isSelected()) {
        this.jTextFieldProxyAddress.setEnabled(true);
        this.jTextFieldProxyPort.setEnabled(true);
        this.jLabelProxyAddress.setEnabled(true);
        this.jLabelProxyPort.setEnabled(true);
        
        this.jTextFieldProxyAddress.requestFocus();

    } else {
        this.jTextFieldProxyAddress.setEnabled(false);
        this.jTextFieldProxyPort.setEnabled(false);
        this.jLabelProxyAddress.setEnabled(false);
        this.jLabelProxyPort.setEnabled(false);

    }
}//GEN-LAST:event_jRadioButtonUseProxyItemStateChanged

/**
 * @param args the command line arguments
 */
public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {

            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            try
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
            catch (Exception ex)
            {
                System.out.println("Failed loading L&F: ");
                System.out.println(ex);
            }                 

            new FrameProxyParms(null);
        }
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonApply;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JLabel jLabelLogoTitle;
    private javax.swing.JLabel jLabelProxyAddress;
    private javax.swing.JLabel jLabelProxyPort;
    private javax.swing.JPanel jPaneProxyOptions;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelBorderTop;
    private javax.swing.JPanel jPanelBrowserDef;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelLetf2;
    private javax.swing.JPanel jPanelProxy;
    private javax.swing.JPanel jPanelProxyAddressAndPort;
    private javax.swing.JPanel jPanelProxyContainer;
    private javax.swing.JPanel jPanelRight2;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSep4;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSepLine1;
    private javax.swing.JPanel jPanelUserDef;
    private javax.swing.JPanel jPanelUserDef1;
    private javax.swing.JRadioButton jRadioButtonBrowserParameters;
    private javax.swing.JRadioButton jRadioButtonDirectConnection;
    private javax.swing.JRadioButton jRadioButtonUseProxy;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextFieldProxyAddress;
    private javax.swing.JTextField jTextFieldProxyPort;
    // End of variables declaration//GEN-END:variables

}
