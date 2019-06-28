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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.HtmlTextUtil;

import com.swing.util.SwingUtil;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;

/**
 *
 * @author  Nicolas de Pomereu
 */
public class NewsFrame extends javax.swing.JFrame {

    /** The parent JFrame */
    private Window parentJframe = null;
    private Window thisOne;
    private MessagesManager messages = new MessagesManager();

    /** Creates new form NewsFrame */
    public NewsFrame(Window parentJframe){
        this.parentJframe = parentJframe;
        thisOne = this;
        initComponents();
        initializeCompany(HtmlTextUtil.getHtmlHelpContent("news"), "Safester - What\'s New");
        
        /*
        UrlContent urlContent = new UrlContent(new URL(AskForDownloadJframe.getWhatsNewUrl()), NewVersionInstaller.getProxy(), NewVersionInstaller.getPasswordAuthentication());
        String whatsNew = urlContent.download();
        System.out.println("whatsNew: " + whatsNew);
        initializeCompany(whatsNew, "Safester - What\'s New");
        */
    }

    /** Creates new form NewsFrame */
    public NewsFrame(Window parentJframe, String content, String title) {
        this.parentJframe = parentJframe;
        thisOne = this;
        initComponents();
        initializeCompany(content, title);
    }

    /**
     * This is the method to include in the constructor
     */
    public void initializeCompany(String content, String title) {

        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.setSize(400, 400);

//        if (parentJframe != null) {
//            this.setLocationRelativeTo(parentJframe);
//        }

        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        jEditorPane.setText(content);

        // Hyperlink listener that will open a new Broser with the given URL
        jEditorPane.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent r)
            {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    try
                    {
                        java.awt.Desktop dekstop = java.awt.Desktop.getDesktop();
                        dekstop.browse(r.getURL().toURI());
                    }
                    catch (Exception e )
                    {
                        // We don't care
                        e.printStackTrace();
                    }
                }
            }
        });
        
        this.jButtonClose.setText(messages.getMessage("ok"));

        // These 2 stupid lines : only to force to display top of file first
        jEditorPane.moveCaretPosition(0);
        jEditorPane.setSelectionEnd(0);

        this.keyListenerAdder();
        //this.setLocationByPlatform(true);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
            }
        });
        
        //this.setLocationRelativeTo(parentJframe);
        
        int theWidth = this.getPreferredSize().width;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        Point upRight = new Point(dim.width - theWidth  - 1, 0);
        
        WindowSettingManager.load(this, upRight);
        
        //pack();
                
        this.setTitle(title);
        this.setVisible(true);
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
        jPanelCenter = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane = new javax.swing.JEditorPane();
        jPanelSep = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jPanelEast = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jEditorPane.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jEditorPane);

        jPanelCenter.add(jScrollPane1);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelSep.setMinimumSize(new java.awt.Dimension(0, 4));
        jPanelSep.setPreferredSize(new java.awt.Dimension(376, 4));

        javax.swing.GroupLayout jPanelSepLayout = new javax.swing.GroupLayout(jPanelSep);
        jPanelSep.setLayout(jPanelSepLayout);
        jPanelSepLayout.setHorizontalGroup(
            jPanelSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
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
            .addGap(0, 269, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        jButtonClose.setText("Fermer");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonClose);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanelEastLayout = new javax.swing.GroupLayout(jPanelEast);
        jPanelEast.setLayout(jPanelEastLayout);
        jPanelEastLayout.setHorizontalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelEastLayout.setVerticalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 269, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelEast, java.awt.BorderLayout.LINE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    this.dispose();
}//GEN-LAST:event_jButtonCloseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new NewsFrame(null).setVisible(true);

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
