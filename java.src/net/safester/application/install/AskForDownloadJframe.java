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
package net.safester.application.install;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.swing.util.SwingUtil;

import net.safester.application.Help;
import net.safester.application.messages.LanguageManager;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.version.Version;
import net.safester.clientserver.ServerParms;


/**
 *
 * @author Nicolas de Pomereu
 */
public class AskForDownloadJframe extends javax.swing.JDialog {

    public static final String CR_LF = System.getProperty("line.separator");
    /**
     * Add a clipboard manager for content management
     */
    private ClipboardManager clipboard = null;
    private java.awt.Window parent = null;
    
    private String serverVersion = null;
    private int result = -1;

    private MessagesManager messagesManager = new MessagesManager();
            
    /**
     * Creates new form AskForDownloadJframe
     *
     * @param parent the parent Window
     * @param serverVersion
     *
     * TODO
     */
    public AskForDownloadJframe(Window parent, String serverVersion) {

        initComponents();

        this.parent = parent;
        this.serverVersion = serverVersion;
        initializeIt();

        /*
        final javax.swing.JDialog thisOne = this;
        // Close the Dialog after 15 seconds
        Thread t = new Thread() {
            public void run() {
                int cpt = 0;
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    cpt++;

                    //debug("cpt: " + cpt);
                    if (cpt > 15) {
                        thisOne.dispose();
                        break;
                    }
                }
            }
        };
        t.start();
        */
                
        com.swing.util.SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);

        this.setVisible(true);

    }

    /**
     * @return the result
     */
    public int getResult() {
        return result;
    }

    /**
     * This is the method to include in *our* constructor
     */
    public void initializeIt() {
        
        Dimension dimension = new Dimension(665, 241);
        
        this.setSize(dimension);
        this.setPreferredSize(dimension);

        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        // Add a Clipboard Manager
        clipboard = new ClipboardManager((JPanel) this.getContentPane());

        // Content is not editable
        jEditorPane1.setContentType("text/html");
        jEditorPane1.setEditable(false);
        
        String whatsNewHtml = getWhatsNewUrl();
        
        String message = Help.getHtmlHelpContent("snip/install_ask_new_version");
        message = message.replace("{0}", Parms.PRODUCT_NAME);
        String theServerVersion = serverVersion.replace("v", "");
        message = message.replace("{1}", theServerVersion);
        message = message.replace("{2}",  whatsNewHtml);
        
        jEditorPane1.setText(message);

        // Hyperlink listener that will open a new Broser with the given URL 
        jEditorPane1.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent r) {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.browse(r.getURL().toURI());
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(parent, e.toString());
                    }
                }

            }
        });

        this.setTitle(messagesManager.getMessage("new_version"));

        jButtonNo.setText(messagesManager.getMessage("no"));
        jButtonYes.setText(messagesManager.getMessage("yes"));
        
        ButtonResizer buttonResizer = new ButtonResizer(jPanelButtons);
        buttonResizer.setWidthToMax();

        this.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                saveSettings();
            }

            public void componentResized(ComponentEvent e) {
                saveSettings();
            }
        });

        // Our window listener for all events
        // If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        this.keyListenerAdder();

        WindowSettingManager.load(this);
        //WindowSettingMgr.load(this);

        this.setAlwaysOnTop(false);

        // These 2 stupid lines : only to Force to diplay top of file first
        jEditorPane1.moveCaretPosition(0);
        jEditorPane1.setSelectionEnd(0);

        pack();

    }

    public static String getWhatsNewUrl() {
        String whatsNewHtml = ServerParms.getHOST() + "/whats_new_{0}.html";
        whatsNewHtml = whatsNewHtml.replace("{0}", LanguageManager.getLanguage());
        return whatsNewHtml;
    }

    public void close() {
        this.dispose();
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
                actionCancel();
            }
        }
    }

    private void actionCancel() {
        this.dispose();
    }

    public void saveSettings() {
        WindowSettingManager.save(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelBorderTop = new javax.swing.JPanel();
        jPanelHelp = new javax.swing.JPanel();
        jPanelHelpLeft = new javax.swing.JPanel();
        jPanelImageHelp = new javax.swing.JPanel();
        jLabelHelp = new javax.swing.JLabel();
        jPanelSep = new javax.swing.JPanel();
        jPanelHelpMain = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanelHelpRight = new javax.swing.JPanel();
        jPanelBottom = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonYes = new javax.swing.JButton();
        jButtonNo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Aide");
        setAlwaysOnTop(true);
        setModal(true);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelBorderTop.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelBorderTop.setMinimumSize(new java.awt.Dimension(20, 10));
        jPanelBorderTop.setPreferredSize(new java.awt.Dimension(20, 10));
        jPanelBorderTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        getContentPane().add(jPanelBorderTop);

        jPanelHelp.setLayout(new javax.swing.BoxLayout(jPanelHelp, javax.swing.BoxLayout.LINE_AXIS));

        jPanelHelpLeft.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelHelp.add(jPanelHelpLeft);

        jPanelImageHelp.setLayout(new javax.swing.BoxLayout(jPanelImageHelp, javax.swing.BoxLayout.LINE_AXIS));

        jLabelHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/48x48/recycle.png"))); // NOI18N
        jPanelImageHelp.add(jLabelHelp);

        jPanelHelp.add(jPanelImageHelp);

        jPanelSep.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelHelp.add(jPanelSep);

        jPanelHelpMain.setPreferredSize(new java.awt.Dimension(319, 180));
        jPanelHelpMain.setLayout(new javax.swing.BoxLayout(jPanelHelpMain, javax.swing.BoxLayout.LINE_AXIS));

        jEditorPane1.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jEditorPane1);

        jPanelHelpMain.add(jScrollPane1);

        jPanelHelp.add(jPanelHelpMain);

        jPanelHelpRight.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelHelp.add(jPanelHelpRight);

        getContentPane().add(jPanelHelp);

        jPanelBottom.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelBottom.setPreferredSize(new java.awt.Dimension(100, 5));

        javax.swing.GroupLayout jPanelBottomLayout = new javax.swing.GroupLayout(jPanelBottom);
        jPanelBottom.setLayout(jPanelBottomLayout);
        jPanelBottomLayout.setHorizontalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 542, Short.MAX_VALUE)
        );
        jPanelBottomLayout.setVerticalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 3, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelBottom);

        jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jButtonYes.setText("Oui");
        jButtonYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonYesActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonYes);

        jButtonNo.setText("Non");
        jButtonNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNoActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonNo);

        getContentPane().add(jPanelButtons);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonYesActionPerformed
        result = JOptionPane.YES_OPTION;
        this.dispose();
    }//GEN-LAST:event_jButtonYesActionPerformed

    private void jButtonNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNoActionPerformed
        result = JOptionPane.NO_OPTION;
        this.dispose();
    }//GEN-LAST:event_jButtonNoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AskForDownloadJframe(null, Version.VERSION);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonNo;
    private javax.swing.JButton jButtonYes;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabelHelp;
    private javax.swing.JPanel jPanelBorderTop;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelHelp;
    private javax.swing.JPanel jPanelHelpLeft;
    private javax.swing.JPanel jPanelHelpMain;
    private javax.swing.JPanel jPanelHelpRight;
    private javax.swing.JPanel jPanelImageHelp;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
