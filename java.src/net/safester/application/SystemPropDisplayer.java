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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.SystemPropsTableCreator;
import net.safester.application.util.TableClipboardManager;

/**
 *
 * @author  Nicolas de Pomereu
 */
public class SystemPropDisplayer extends javax.swing.JFrame {

    public static final String CR_LF = System.getProperty("line.separator");

    /** The parent JFrame */
    private JFrame parentJframe = null;
    private JFrame thisOne;
    private MessagesManager messages = new MessagesManager();

    /** Pop Up menu */
    JPopupMenu popupMenu;
    private Font m_font = new Font("Tahoma", Font.PLAIN, 13);
    
    /** Add a clipboard manager for right button mouse control over input text fields */
    public TableClipboardManager clipboard = null;
    
    /** Creates new form NewsFrame */
    public SystemPropDisplayer(JFrame parentJframe) {
        this.parentJframe = parentJframe;
        thisOne = this;
        initComponents();
        initializeCompany();
    }

    /**
     * This is the method to include in the constructor
     */
    public void initializeCompany() {

        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.setSize(500, 500);
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        
        if (parentJframe != null) {
            this.setLocationRelativeTo(parentJframe);
        }

        this.jLabelMiniIcon.setText(MessagesManager.get("system_info"));
        this.jButtonClose.setText(messages.getMessage("ok"));

        this.keyListenerAdder();
        this.setLocationByPlatform(true);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
            }
        });

        this.setLocationRelativeTo(parentJframe);
        WindowSettingManager.load(this);

        jScrollPane1.setAutoscrolls(true);

        //Ok; clean (re)recration of the JTable

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                createTable();
            }
        });

        jScrollPane1.setViewportView(jTable1);
        
        this.setTitle(messages.getMessage("system_info"));
        this.setVisible(true);
    }

    /**
     * Will (re)create the JTable with all the public keys
     *
     */
    private void createTable() {
        
        SystemPropsTableCreator systemPropsTableCreator = new SystemPropsTableCreator(m_font);
        jTable1 = systemPropsTableCreator.create();

        jTable1.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                this_keyReleased(e);
            }
        });

        jTable1.requestFocusInWindow();

        // Sey colors to be clean with all environments
        // jTable1.setSelectionBackground(PgeepColor.LIGHT_BLUE);
        // jTable1.setSelectionForeground(Color.BLACK);

        jScrollPane1.setViewportView(jTable1);

        Color tableBackground = null;
        tableBackground = jTable1.getBackground();
        jTable1.getParent().setBackground(tableBackground);

        jTable1.setIntercellSpacing(new Dimension(5, 1));

        // Add a Clipboard Manager
        clipboard = new TableClipboardManager(jTable1);
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


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jPanelTop = new javax.swing.JPanel();
        jPanelIcon = new javax.swing.JPanel();
        jLabelMiniIcon = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanelSep = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jPanelEast = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanelTop.setMaximumSize(new java.awt.Dimension(32767, 80));
        jPanelTop.setLayout(new javax.swing.BoxLayout(jPanelTop, javax.swing.BoxLayout.LINE_AXIS));

        jPanelIcon.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        jLabelMiniIcon.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelMiniIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/speech_balloon_answer.png"))); // NOI18N
        jLabelMiniIcon.setText("System Info");
        jLabelMiniIcon.setToolTipText("");
        jPanelIcon.add(jLabelMiniIcon);

        jPanelTop.add(jPanelIcon);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jPanelTop.add(jPanel3);

        jPanelNorth.add(jPanelTop);

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanelCenter.add(jScrollPane1);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelSep.setMinimumSize(new java.awt.Dimension(0, 4));
        jPanelSep.setPreferredSize(new java.awt.Dimension(608, 4));

        javax.swing.GroupLayout jPanelSepLayout = new javax.swing.GroupLayout(jPanelSep);
        jPanelSep.setLayout(jPanelSepLayout);
        jPanelSepLayout.setHorizontalGroup(
            jPanelSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 608, Short.MAX_VALUE)
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
            .addGap(0, 388, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelSouth.setLayout(new java.awt.GridLayout(1, 2));

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        jPanelSouth.add(jPanel1);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 10));

        jButtonClose.setText("Fermer");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonClose);

        jPanelSouth.add(jPanel2);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanelEastLayout = new javax.swing.GroupLayout(jPanelEast);
        jPanelEast.setLayout(jPanelEastLayout);
        jPanelEastLayout.setHorizontalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelEastLayout.setVerticalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelEast, java.awt.BorderLayout.LINE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    dispose();
}//GEN-LAST:event_jButtonCloseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SystemPropDisplayer(null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JLabel jLabelMiniIcon;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelIcon;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
