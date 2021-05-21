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

import com.swing.util.ButtonUrlOver;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.swing.util.SwingUtil;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.net.URI;
import javax.swing.JOptionPane;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.parms.SubscriptionLocalStore;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.version.Version;
import net.safester.clientserver.ServerParms;

/**
 *
 * @author Nicolas de Pomereu
 */
public class About extends javax.swing.JFrame {

    public static final String CR_LF = System.getProperty("line.separator");

    /**
     * The parent Window
     */
    private Window parent = null;

    //private static String urlDevelopedBy   = "www.kawansoftwares.com";
    private MessagesManager messages = new MessagesManager();

    private boolean displayEdition = false;

    /**
     * Creates new form About
     */
    public About(Window parent, boolean displayEdition) {
        this.parent = parent;
        this.displayEdition = displayEdition;

        initComponents();
        initializeCompany();

        this.setVisible(true);
    }

    /**
     * Creates new form About
     */
    public About(Window parent) {
        this(parent, true);
    }
    
    /*
    private static class Painter extends javax.swing.plaf.nimbus.AbstractRegionPainter {
        private final Color color;

        private Painter(Color color) {
            this.color = color;
        }
        @Override
        protected AbstractRegionPainter.PaintContext getPaintContext() {
            return new AbstractRegionPainter.PaintContext(null, null, false);
        }

        @Override
        protected void doPaint(Graphics2D g, JComponent c, 
                int width, int height, Object[] extendedCacheKeys) {
            g.setColor(c.isEnabled() ? c.getBackground() : color);
            g.fillRect(0, 0, width, height);
        }
    }
    */

    /**
     * This is the method to include in *our* constructor
     */
    public void initializeCompany() {
        this.setSize(new Dimension(434, 522));
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        ButtonResizer buttonResizer = new ButtonResizer(jPanelButtons);
        buttonResizer.setWidthToMax();

        // Our window listener for all events
        // If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                save();
            }
        });

        jButtonClose.requestFocus();
        this.keyListenerAdder();

        jPanelAbout1.setBorder(javax.swing.BorderFactory.createTitledBorder(this.messages.getMessage("about")));
        jPanelCredits.setBorder(javax.swing.BorderFactory.createTitledBorder(this.messages.getMessage("credits")));

        /*
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        Painter painter = new Painter(Color.red);
        String key = "EditorPane.backgroundPainter";
        defaults.put(key, painter);
        jEditorPaneSafeLogic.putClientProperty("Nimbus.Overrides", defaults);
        jEditorPaneSafeLogic.putClientProperty("Nimbus.Overrides.InheritDefaults", false);
        */
        
        jLabelCopyRight.setText(Version.getVersionWithCopyright());
        jLabelSupport.setText(this.messages.getMessage("support_report_a_bug"));
        jLabelSoftware.setText(this.messages.getMessage("this_product_includes_software_developped_by"));
  
      
        /*
        if (displayEdition) {
            short currentSubscription = SubscriptionLocalStore.getSubscription();
            System.out.println("currentSubscription: " + currentSubscription);
            String subscription = StoreParms.getProductNameForSubscription(currentSubscription);
            aboutText += "<br><i>" + subscription + " " + this.messages.getMessage("account") + "</i>";
        }
        */
        
        SwingUtil.applySwingUpdates(rootPane);
        
        this.setTitle(this.messages.getMessage("about"));
        this.jButtonClose.setText(this.messages.getMessage("ok"));

        // Load and activate previous windows settings
        this.setLocationRelativeTo(parent);

        // Necessary to update the buttons width
        //Dimension dim = this.getPreferredSize();
        //this.setPreferredSize(new Dimension(dim.width + 1,  dim.height));
        //pack();
    }

    private void save() {
        WindowSettingManager.save(this);
    }

    private void close() {
        WindowSettingManager.save(this);
        dispose();
    }

    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {
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

            if (keyCode == KeyEvent.VK_ESCAPE) {
                close();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMain = new javax.swing.JPanel();
        jPanelBorderLeft = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelLogos = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jPanelAbout1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelCopyRight = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButtonUrl = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabelSupport = new javax.swing.JLabel();
        jButtonEmail = new javax.swing.JButton();
        jPanelSep1 = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jPanelCredits = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabelSoftware = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jButtonSoftware1 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jButtonSoftware2 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jButtonSoftware3 = new javax.swing.JButton();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelBorderRight = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.X_AXIS));

        jPanelBorderLeft.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelMain.add(jPanelBorderLeft);

        jPanelCenter.setMaximumSize(new java.awt.Dimension(32783, 600));
        jPanelCenter.setMinimumSize(new java.awt.Dimension(290, 600));
        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelLogos.setMaximumSize(new java.awt.Dimension(32767, 133));
        jPanelLogos.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 12, 12));

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 0, 5));

        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/logo-blue-on-white-300x99.png"))); // NOI18N
        jLabelLogo.setOpaque(true);
        jPanel1.add(jLabelLogo);

        jPanelLogos.add(jPanel1);

        jPanelCenter.add(jPanelLogos);

        jPanelAbout1.setBorder(javax.swing.BorderFactory.createTitledBorder("About"));
        jPanelAbout1.setLayout(new javax.swing.BoxLayout(jPanelAbout1, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 22));
        jPanel2.setMinimumSize(new java.awt.Dimension(51, 22));
        jPanel2.setPreferredSize(new java.awt.Dimension(51, 22));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabelCopyRight.setText("Safester v5.9 - 18-May-21 Copyright © 2021 Safester");
        jPanel2.add(jLabelCopyRight);

        jPanelAbout1.add(jPanel2);

        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 22));
        jPanel3.setMinimumSize(new java.awt.Dimension(114, 22));
        jPanel3.setPreferredSize(new java.awt.Dimension(114, 22));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButtonUrl.setForeground(new java.awt.Color(0, 0, 255));
        jButtonUrl.setText("www.safester.net");
        jButtonUrl.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonUrl.setBorderPainted(false);
        jButtonUrl.setContentAreaFilled(false);
        jButtonUrl.setFocusPainted(false);
        jButtonUrl.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButtonUrl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonUrlMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonUrlMouseExited(evt);
            }
        });
        jButtonUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUrlActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonUrl);

        jPanelAbout1.add(jPanel3);

        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 22));
        jPanel5.setMinimumSize(new java.awt.Dimension(114, 22));
        jPanel5.setPreferredSize(new java.awt.Dimension(114, 22));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabelSupport.setText("Support / Signaler un bug :");
        jPanel5.add(jLabelSupport);

        jButtonEmail.setForeground(new java.awt.Color(0, 0, 255));
        jButtonEmail.setText("contact@safester.net");
        jButtonEmail.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonEmail.setBorderPainted(false);
        jButtonEmail.setContentAreaFilled(false);
        jButtonEmail.setFocusPainted(false);
        jButtonEmail.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButtonEmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonEmailMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonEmailMouseExited(evt);
            }
        });
        jButtonEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEmailActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonEmail);

        jPanelAbout1.add(jPanel5);

        jPanelSep1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelAbout1.add(jPanelSep1);

        jPanelCenter.add(jPanelAbout1);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelCenter.add(jPanelSep);

        jPanelCredits.setBorder(javax.swing.BorderFactory.createTitledBorder("Credits"));
        jPanelCredits.setLayout(new javax.swing.BoxLayout(jPanelCredits, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setMaximumSize(new java.awt.Dimension(32767, 22));
        jPanel6.setMinimumSize(new java.awt.Dimension(51, 22));
        jPanel6.setPreferredSize(new java.awt.Dimension(51, 22));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabelSoftware.setText("Ce produit inclut des logiciels développés par : ");
        jPanel6.add(jLabelSoftware);

        jPanelCredits.add(jPanel6);

        jPanel7.setMaximumSize(new java.awt.Dimension(32767, 22));
        jPanel7.setMinimumSize(new java.awt.Dimension(114, 22));
        jPanel7.setPreferredSize(new java.awt.Dimension(114, 22));
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButtonSoftware1.setForeground(new java.awt.Color(0, 0, 255));
        jButtonSoftware1.setText("The Apache Software Foundation");
        jButtonSoftware1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonSoftware1.setBorderPainted(false);
        jButtonSoftware1.setContentAreaFilled(false);
        jButtonSoftware1.setFocusPainted(false);
        jButtonSoftware1.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButtonSoftware1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonSoftware1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonSoftware1MouseExited(evt);
            }
        });
        jButtonSoftware1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSoftware1ActionPerformed(evt);
            }
        });
        jPanel7.add(jButtonSoftware1);

        jPanelCredits.add(jPanel7);

        jPanel9.setMaximumSize(new java.awt.Dimension(32767, 22));
        jPanel9.setMinimumSize(new java.awt.Dimension(114, 22));
        jPanel9.setPreferredSize(new java.awt.Dimension(114, 22));
        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButtonSoftware2.setForeground(new java.awt.Color(0, 0, 255));
        jButtonSoftware2.setText("The Legion Of The Bouncy Castle");
        jButtonSoftware2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonSoftware2.setBorderPainted(false);
        jButtonSoftware2.setContentAreaFilled(false);
        jButtonSoftware2.setFocusPainted(false);
        jButtonSoftware2.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButtonSoftware2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonSoftware2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonSoftware2MouseExited(evt);
            }
        });
        jButtonSoftware2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSoftware2ActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonSoftware2);

        jPanelCredits.add(jPanel9);

        jPanel10.setMaximumSize(new java.awt.Dimension(32767, 22));
        jPanel10.setMinimumSize(new java.awt.Dimension(114, 22));
        jPanel10.setPreferredSize(new java.awt.Dimension(114, 22));
        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButtonSoftware3.setForeground(new java.awt.Color(0, 0, 255));
        jButtonSoftware3.setText("iHarder.net");
        jButtonSoftware3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonSoftware3.setBorderPainted(false);
        jButtonSoftware3.setContentAreaFilled(false);
        jButtonSoftware3.setFocusPainted(false);
        jButtonSoftware3.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButtonSoftware3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonSoftware3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonSoftware3MouseExited(evt);
            }
        });
        jButtonSoftware3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSoftware3ActionPerformed(evt);
            }
        });
        jPanel10.add(jButtonSoftware3);

        jPanelCredits.add(jPanel10);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelCredits.add(jPanelSep2);

        jPanelCenter.add(jPanelCredits);

        jPanelMain.add(jPanelCenter);

        jPanelBorderRight.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelMain.add(jPanelBorderRight);

        getContentPane().add(jPanelMain);

        jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 65));
        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonClose.setText("Fermer");
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
    close();
}//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonUrlMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonUrlMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ButtonUrlOver.enter(evt);
    }//GEN-LAST:event_jButtonUrlMouseEntered

    private void jButtonUrlMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonUrlMouseExited
        this.setCursor(Cursor.getDefaultCursor());
        ButtonUrlOver.exit(evt);
    }//GEN-LAST:event_jButtonUrlMouseExited

    private void jButtonUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUrlActionPerformed
        // TODO add your handling code here:
        String web = "https://www.safester.net";
        callWeb(web);
    }//GEN-LAST:event_jButtonUrlActionPerformed

    private void callWeb(String web) throws HeadlessException {
        Desktop desktop = Desktop.getDesktop();
        try {
            
            desktop.browse(new URI(web));
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString());
        }
    }

    private void jButtonEmailMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonEmailMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ButtonUrlOver.enter(evt);
    }//GEN-LAST:event_jButtonEmailMouseEntered

    private void jButtonEmailMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonEmailMouseExited
        this.setCursor(Cursor.getDefaultCursor());
        ButtonUrlOver.exit(evt);
    }//GEN-LAST:event_jButtonEmailMouseExited

    private void jButtonEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEmailActionPerformed
        Desktop desktop = Desktop.getDesktop();
        try {
            String email = "mailto:contact@safester.net";
            desktop.mail(new URI(email));
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString());
        }
    }//GEN-LAST:event_jButtonEmailActionPerformed

    private void jButtonSoftware1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSoftware1MouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ButtonUrlOver.enter(evt);
    }//GEN-LAST:event_jButtonSoftware1MouseEntered

    private void jButtonSoftware1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSoftware1MouseExited
        this.setCursor(Cursor.getDefaultCursor());
        ButtonUrlOver.exit(evt);
    }//GEN-LAST:event_jButtonSoftware1MouseExited

    private void jButtonSoftware1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSoftware1ActionPerformed

        String web = "https://www.apache.org/";
        callWeb(web);
    }//GEN-LAST:event_jButtonSoftware1ActionPerformed

    private void jButtonSoftware2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSoftware2MouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ButtonUrlOver.enter(evt);
    }//GEN-LAST:event_jButtonSoftware2MouseEntered

    private void jButtonSoftware2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSoftware2MouseExited
        this.setCursor(Cursor.getDefaultCursor());
        ButtonUrlOver.exit(evt);
    }//GEN-LAST:event_jButtonSoftware2MouseExited

    private void jButtonSoftware2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSoftware2ActionPerformed
        String web = "https://www.bouncycastle.org"; 
        callWeb(web);
    }//GEN-LAST:event_jButtonSoftware2ActionPerformed

    private void jButtonSoftware3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSoftware3MouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ButtonUrlOver.enter(evt);
    }//GEN-LAST:event_jButtonSoftware3MouseEntered

    private void jButtonSoftware3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSoftware3MouseExited
        this.setCursor(Cursor.getDefaultCursor());
        ButtonUrlOver.exit(evt);
    }//GEN-LAST:event_jButtonSoftware3MouseExited

    private void jButtonSoftware3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSoftware3ActionPerformed
        // TODO add your handling code here:
        String web = "http://iharder.sourceforge.net/current/java/filedrop/";
        callWeb(web);
    }//GEN-LAST:event_jButtonSoftware3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {


                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);

                try {
                   UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                   // UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception ex) {
                    System.out.println("Failed loading L&F: ");
                    System.out.println(ex);
                }                
                               
                new About(null);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonEmail;
    private javax.swing.JButton jButtonSoftware1;
    private javax.swing.JButton jButtonSoftware2;
    private javax.swing.JButton jButtonSoftware3;
    private javax.swing.JButton jButtonUrl;
    private javax.swing.JLabel jLabelCopyRight;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelSoftware;
    private javax.swing.JLabel jLabelSupport;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelAbout1;
    private javax.swing.JPanel jPanelBorderLeft;
    private javax.swing.JPanel jPanelBorderRight;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCredits;
    private javax.swing.JPanel jPanelLogos;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    // End of variables declaration//GEN-END:variables

}
