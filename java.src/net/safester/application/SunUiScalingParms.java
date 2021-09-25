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
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.swing.util.SwingUtil;
import java.awt.Toolkit;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.SunUiScalingUtil;
import net.safester.application.util.UserPrefManager;


/**
 *
 * @author  Nicolas de Pomereu
 */
public class SunUiScalingParms extends javax.swing.JFrame {
    
    /** The parent Window */
    private JFrame parent = null;

    private JFrame thisOne;
    private MessagesManager messages = new MessagesManager();
    
    private ClipboardManager clipboardManager;

    /** Creates new form FrameProxyParms*/
    public SunUiScalingParms(JFrame parent) {
        
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
        
        Dimension dim = new Dimension(457, 378);
        this.setSize(dim);
        this.setPreferredSize(dim);

        TitledBorder titledBorder =
            new TitledBorder( " " + this.messages.getMessage("scaling_explain") + " ");
        this.jPanelProxyContainer.setBorder(titledBorder);
        
        buttonGroup1.add(jRadioButtonScale100);
        buttonGroup1.add(jRadioButtonScale125);
        buttonGroup1.add(jRadioButtonScale150);
        buttonGroup1.add(jRadioButtonScale200);
        buttonGroup1.add(jRadioButtonScale250);
        
        try
        {
            this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        } 
            
        this.setTitle(messages.getMessage("scaling"));
        jLabelLogoTitle.setText(messages.getMessage("scaling"));
        
        //jButtonHelpNtlm.putClientProperty( "JButton.buttonType", "square" );

        jButtonApply.setText(messages.getMessage("ok"));
        jButtonClose.setText(messages.getMessage("cancel"));

        hideUpperSizeIfLowResolution();
        
        // Set the Send preferences for user Preferences
        setStoredPreferences();
        
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
        
        SwingUtil.applySwingUpdates(rootPane);
        
        this.setLocationByPlatform(true);
        this.setLocationRelativeTo(parent);
        WindowSettingManager.load(this);

    }    
    
    private void hideUpperSizeIfLowResolution() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenSize.width < 2200) {
            jRadioButtonScale250.setEnabled(false);
        }
    }
    
    /**
     * Set the Send Preferences found in User Preferences (Registry)
     */
    private void setStoredPreferences()
    {
        jRadioButtonScale100.setSelected(false);
        jRadioButtonScale110.setSelected(false);
        jRadioButtonScale125.setSelected(false);
        jRadioButtonScale150.setSelected(false);
        jRadioButtonScale200.setSelected(false);
        jRadioButtonScale250.setSelected(false);

        String scaling = UserPrefManager.getPreference(UserPrefManager.SUN_SCALING, SunUiScalingUtil.SCALING_100);

        if (scaling.equals(SunUiScalingUtil.SCALING_100)) {
            jRadioButtonScale100.setSelected(true);
        } else if (scaling.equals(SunUiScalingUtil.SCALING_110)) {
            jRadioButtonScale110.setSelected(true);
        } else if (scaling.equals(SunUiScalingUtil.SCALING_125)) {
            jRadioButtonScale125.setSelected(true);
        } else if (scaling.equals(SunUiScalingUtil.SCALING_150)) {
            jRadioButtonScale150.setSelected(true);
        } else if (scaling.equals(SunUiScalingUtil.SCALING_200)) {
            jRadioButtonScale200.setSelected(true);
        } else if (scaling.equals(SunUiScalingUtil.SCALING_250)) {
            jRadioButtonScale250.setSelected(true);
        } else {
            throw new IllegalArgumentException("Scaling is invalid: " + scaling);
        }     


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
        String scaling = null;

        if (jRadioButtonScale100.isSelected()) {
            scaling = SunUiScalingUtil.SCALING_100;
        } else if (jRadioButtonScale110.isSelected()) {
            scaling = SunUiScalingUtil.SCALING_110;
        } else if (jRadioButtonScale125.isSelected()) {
            scaling = SunUiScalingUtil.SCALING_125;
        } else if (jRadioButtonScale150.isSelected()) {
            scaling = SunUiScalingUtil.SCALING_150;
        } else if (jRadioButtonScale200.isSelected()) {
            scaling = SunUiScalingUtil.SCALING_200;
        } else if (jRadioButtonScale250.isSelected()) {
            scaling = SunUiScalingUtil.SCALING_250;
        }

        SunUiScalingUtil.setPreferenceScaling(scaling);
        
        MessagesManager messages = new MessagesManager();
        JOptionPane.showMessageDialog(this, messages.getMessage("safester_will_be_closed"));
        close();
        System.exit(0);
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
        jRadioButtonScale100 = new javax.swing.JRadioButton();
        jPanelBrowserDef1 = new javax.swing.JPanel();
        jRadioButtonScale110 = new javax.swing.JRadioButton();
        jPanelUserDef3 = new javax.swing.JPanel();
        jRadioButtonScale125 = new javax.swing.JRadioButton();
        jPanelUserDef = new javax.swing.JPanel();
        jRadioButtonScale150 = new javax.swing.JRadioButton();
        jPanelUserDef1 = new javax.swing.JPanel();
        jRadioButtonScale200 = new javax.swing.JRadioButton();
        jPanelUserDef2 = new javax.swing.JPanel();
        jRadioButtonScale250 = new javax.swing.JRadioButton();
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
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanel5);

        jPanelBorderTop.setLayout(new javax.swing.BoxLayout(jPanelBorderTop, javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setMaximumSize(new java.awt.Dimension(32767, 38));
        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 3));

        jLabelLogoTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelLogoTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/window_size.png"))); // NOI18N
        jLabelLogoTitle.setText("UI Scaling");
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

        buttonGroup1.add(jRadioButtonScale100);
        jRadioButtonScale100.setText("100%");
        jPanelBrowserDef.add(jRadioButtonScale100);

        jPaneProxyOptions.add(jPanelBrowserDef);

        jPanelBrowserDef1.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelBrowserDef1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup1.add(jRadioButtonScale110);
        jRadioButtonScale110.setText("110%");
        jPanelBrowserDef1.add(jRadioButtonScale110);

        jPaneProxyOptions.add(jPanelBrowserDef1);

        jPanelUserDef3.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelUserDef3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup1.add(jRadioButtonScale125);
        jRadioButtonScale125.setText("125");
        jPanelUserDef3.add(jRadioButtonScale125);

        jPaneProxyOptions.add(jPanelUserDef3);

        jPanelUserDef.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelUserDef.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup1.add(jRadioButtonScale150);
        jRadioButtonScale150.setText("150%");
        jPanelUserDef.add(jRadioButtonScale150);

        jPaneProxyOptions.add(jPanelUserDef);

        jPanelUserDef1.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelUserDef1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup1.add(jRadioButtonScale200);
        jRadioButtonScale200.setText("200%");
        jPanelUserDef1.add(jRadioButtonScale200);

        jPaneProxyOptions.add(jPanelUserDef1);

        jPanelUserDef2.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelUserDef2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup1.add(jRadioButtonScale250);
        jRadioButtonScale250.setText("250%");
        jPanelUserDef2.add(jRadioButtonScale250);

        jPaneProxyOptions.add(jPanelUserDef2);

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

            new SunUiScalingParms(null);
        }
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonApply;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JLabel jLabelLogoTitle;
    private javax.swing.JPanel jPaneProxyOptions;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelBorderTop;
    private javax.swing.JPanel jPanelBrowserDef;
    private javax.swing.JPanel jPanelBrowserDef1;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelLetf2;
    private javax.swing.JPanel jPanelProxy;
    private javax.swing.JPanel jPanelProxyContainer;
    private javax.swing.JPanel jPanelRight2;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSep4;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSepLine1;
    private javax.swing.JPanel jPanelUserDef;
    private javax.swing.JPanel jPanelUserDef1;
    private javax.swing.JPanel jPanelUserDef2;
    private javax.swing.JPanel jPanelUserDef3;
    private javax.swing.JRadioButton jRadioButtonScale100;
    private javax.swing.JRadioButton jRadioButtonScale110;
    private javax.swing.JRadioButton jRadioButtonScale125;
    private javax.swing.JRadioButton jRadioButtonScale150;
    private javax.swing.JRadioButton jRadioButtonScale200;
    private javax.swing.JRadioButton jRadioButtonScale250;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables



}
