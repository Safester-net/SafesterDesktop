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
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.parms.SubscriptionLocalStore;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.DesktopWrapper;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JEditorPaneLinkDetector;
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

        jPanelAbout.setBorder(javax.swing.BorderFactory.createTitledBorder(this.messages.getMessage("about")));
        jPanelCredits.setBorder(javax.swing.BorderFactory.createTitledBorder(this.messages.getMessage("credits")));

        jPanelAbout.remove(jEditorPaneSafeLogic);
        jEditorPaneSafeLogic = new JEditorPaneLinkDetector();

        jEditorPaneSafeLogic.setContentType("text/html");
        jEditorPaneSafeLogic.setEditable(false);

        /*
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        Painter painter = new Painter(Color.red);
        String key = "EditorPane.backgroundPainter";
        defaults.put(key, painter);
        jEditorPaneSafeLogic.putClientProperty("Nimbus.Overrides", defaults);
        jEditorPaneSafeLogic.putClientProperty("Nimbus.Overrides.InheritDefaults", false);
        */
        
        String aboutText
                = "<P ALIGN=RIGHT><font face=\"Arial\" size=4>"
                + Version.getVersionWithCopyright();

        if (displayEdition) {
            short currentSubscription = SubscriptionLocalStore.getSubscription();
            System.out.println("currentSubscription: " + currentSubscription);
            String subscription = StoreParms.getProductNameForSubscription(currentSubscription);
            aboutText += "<br><i>" + subscription + " " + this.messages.getMessage("account") + "</i>";
        }

        String urlEmail = "<a href=\"mailto:{0}\" style=\"text-decoration: none;\">{1}</a>";
        urlEmail = MessageFormat.format(urlEmail, ServerParms.CONTACT_EMAIL, ServerParms.CONTACT_EMAIL);

        String urlWeb = "<a href=\"{0}\" style=\"text-decoration: none;\">{1}</a>";
        urlWeb = MessageFormat.format(urlWeb, "https://" + ServerParms.CONTACT_WEB, ServerParms.CONTACT_WEB);

        aboutText
                += "<br>" + CR_LF + urlWeb
                + "<br>" + CR_LF + this.messages.getMessage("support_report_a_bug")
                + " " + urlEmail.trim() 
                + "<br>";

        //+ "<br><br><font face=\"Arial\" size=3>"
        //+ CR_LF + "<i>" + this.messages.getMessage("safester_property") + "</i>";
        //System.out.println(aboutText);
        
        jEditorPaneSafeLogic.setText(aboutText);
        jPanelAbout.add(jEditorPaneSafeLogic);
        
        // Hyperlink listener that will open a new Browser with the given URL
        jEditorPaneSafeLogic.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent r) {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    DesktopWrapper.browse(r.getURL());
                }
            }
        });

                        
        jPanelCredits.remove(jEditorPaneCredits);
        jEditorPaneCredits = new JEditorPaneLinkDetector();

        jEditorPaneCredits.setContentType("text/html");
        jEditorPaneCredits.setEditable(false);

        String creditText
                = "<P ALIGN=RIGHT><font face=\"Arial\" size=4>"
                + this.messages.getMessage("this_product_includes_software_developped_by")
                + "<br>"
                + "<br><a href=\"http://www.apache.org\" style=\"text-decoration: none;\">The Apache Software Foundation</a>"
                + "<br><a href=\"http://www.bouncycastle.org\" style=\"text-decoration: none;\">The Legion Of The Bouncy Castle</a>"
                + "<br><a href=\"http://iharder.sourceforge.net/current/java/filedrop\" style=\"text-decoration: none;\">iHarder.net</a>"
                + "<br>";

        jEditorPaneCredits.setText(creditText);
        jPanelCredits.add(jEditorPaneCredits);

        jEditorPaneCredits.setOpaque(false);

        // Hyperlink listener that will open a new Browser with the given URL
        jEditorPaneCredits.addHyperlinkListener(new HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent r) {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    DesktopWrapper.browse(r.getURL());
                }
            }
        });
       
        Color theBackground = jPanelAbout.getBackground();
        jEditorPaneSafeLogic.setBackground(theBackground);
        jEditorPaneCredits.setBackground(theBackground);

        jEditorPaneSafeLogic.setOpaque(false);
        
        
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

        jPanelLogos = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jPanelMain = new javax.swing.JPanel();
        jPanelBorderLeft = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelAbout = new javax.swing.JPanel();
        jEditorPaneSafeLogic = new javax.swing.JEditorPane();
        jPanelSep = new javax.swing.JPanel();
        jPanelCredits = new javax.swing.JPanel();
        jEditorPaneCredits = new javax.swing.JEditorPane();
        jPanelBorderRight = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelLogos.setBackground(new java.awt.Color(255, 255, 255));
        jPanelLogos.setMaximumSize(new java.awt.Dimension(32767, 93));
        jPanelLogos.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 12, 12));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabelLogo.setBackground(new java.awt.Color(255, 255, 255));
        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/logo-blue-on-white-300x99.png"))); // NOI18N
        jLabelLogo.setOpaque(true);
        jPanel1.add(jLabelLogo);

        jPanelLogos.add(jPanel1);

        getContentPane().add(jPanelLogos);

        jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.X_AXIS));

        jPanelBorderLeft.setBackground(new java.awt.Color(255, 255, 255));
        jPanelBorderLeft.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelMain.add(jPanelBorderLeft);

        jPanelCenter.setMaximumSize(new java.awt.Dimension(32783, 600));
        jPanelCenter.setMinimumSize(new java.awt.Dimension(290, 600));
        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelAbout.setBackground(new java.awt.Color(255, 255, 255));
        jPanelAbout.setBorder(javax.swing.BorderFactory.createTitledBorder("About"));
        jPanelAbout.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jEditorPaneSafeLogic.setMinimumSize(new java.awt.Dimension(106, 80));
        jPanelAbout.add(jEditorPaneSafeLogic);

        jPanelCenter.add(jPanelAbout);

        jPanelSep.setBackground(new java.awt.Color(255, 255, 255));
        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelCenter.add(jPanelSep);

        jPanelCredits.setBackground(new java.awt.Color(255, 255, 255));
        jPanelCredits.setBorder(javax.swing.BorderFactory.createTitledBorder("Credits"));
        jPanelCredits.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jEditorPaneCredits.setEditable(false);
        jEditorPaneCredits.setMinimumSize(new java.awt.Dimension(106, 80));
        jPanelCredits.add(jEditorPaneCredits);

        jPanelCenter.add(jPanelCredits);

        jPanelMain.add(jPanelCenter);

        jPanelBorderRight.setBackground(new java.awt.Color(255, 255, 255));
        jPanelBorderRight.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelMain.add(jPanelBorderRight);

        getContentPane().add(jPanelMain);

        jPanelButtons.setBackground(new java.awt.Color(255, 255, 255));
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
    private javax.swing.JEditorPane jEditorPaneCredits;
    private javax.swing.JEditorPane jEditorPaneSafeLogic;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelAbout;
    private javax.swing.JPanel jPanelBorderLeft;
    private javax.swing.JPanel jPanelBorderRight;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCredits;
    private javax.swing.JPanel jPanelLogos;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelSep;
    // End of variables declaration//GEN-END:variables

}
