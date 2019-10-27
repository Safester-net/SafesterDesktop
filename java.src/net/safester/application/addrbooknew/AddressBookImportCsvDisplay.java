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
package net.safester.application.addrbooknew;
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
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;

 
/**
 * Main class & frame to import external address book.
 * 
 * @author  Nicolas de Pomereu
 */
public class AddressBookImportCsvDisplay extends javax.swing.JDialog {

    private static final String CR_LF = System.getProperty("line.separator");
    
    /** The parent JFrame */
    private Window parent = null;

    /** The file to display */
    private final File file;
    private final String separator;
        
    private ClipboardManager clipboardManager;

    
    /**
     * Constructor
     * @param parent
     * @param file
     * @param separator 
     */
    public AddressBookImportCsvDisplay(Window parent, File file, String separator) {
        this.parent = parent;
        this.file = file;
        this.separator = separator;
        initComponents();
        initializeCompany();
    }

    /**
     * This is the method to include in the constructor
     */
    public void initializeCompany() {

        
        Dimension dim = new Dimension(895, 501);
        
        this.setSize(dim);
        this.setPreferredSize(dim);
        
        this.setModal(true);
                
        clipboardManager = new ClipboardManager(rootPane);

        try
        {
            this.setIconImage(ImageParmsUtil.getAppIcon());
        }
        catch (RuntimeException e1)
        {
            e1.printStackTrace();
        } 

        if (parent != null) {
            this.setLocationRelativeTo(parent);
        }

        jLabelTitle.setText(MessagesManager.get("file_format_of")  +  " " + file.getName());
        jButtonOk.setText(MessagesManager.get("ok"));
        
        jTextArea1.setEditable(false);
        jTextArea1.setBackground(Color.WHITE);

        // Set the editor pane content
        displayFileContent();
        
        // These 2 stupid lines : only to force to display top of file first
        jTextArea1.moveCaretPosition(0);
        jTextArea1.setSelectionEnd(0);

        this.keyListenerAdder();
        this.setLocationByPlatform(true);

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
                closeOnExit();
            }
        });
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                saveSettings();
            }
        });

        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);
        
        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        
        this.setLocationRelativeTo(parent);
        WindowSettingManager.load(this);

        this.jLabelTitle.setText(this.jLabelTitle.getText());
        this.setTitle(this.jLabelTitle.getText());
        
        pack();
        
        this.setVisible(true);
    }

    public void saveSettings() {
        WindowSettingManager.save(this);
    }
    
    private void closeOnExit() {
        saveSettings();
        this.dispose();
    }
        
    /**
     * Extract the content of the file and put it into the editor pane
     */
    private void displayFileContent()
    {
        try {
            CsvAddressBook csvAddressBook = new CsvAddressBook(file, separator);
            List<String> content = csvAddressBook.getFirstLines();

            StringBuffer text = new StringBuffer();
            for (String theLine : content) {
                text.append(theLine + CR_LF);
            }

            //jTextArea1.setFont(Font.getFont(Font.MONOSPACED));
            jTextArea1.setLineWrap(false);
            this.jTextArea1.setText(text.toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, "Exception raised: " + ex.toString(), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }

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
        jLabelIcon = new javax.swing.JLabel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSepLine2New2 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel27 = new javax.swing.JPanel();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelScroll = new javax.swing.JPanel();
        jPanelSpaces11 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanelSpaces12 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanelSepLine2New = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        jPanel29 = new javax.swing.JPanel();
        jPanelSpaces10 = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jPanel0Pixels = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelNorth.setMaximumSize(new java.awt.Dimension(32767, 72));
        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 12));

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/text.png"))); // NOI18N
        jPanelNorth.add(jLabelIcon);

        jLabelTitle.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelTitle.setText("Format du fichier");
        jPanelNorth.add(jLabelTitle);

        getContentPane().add(jPanelNorth);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSepLine2New2.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New2.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New2.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel26.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New2.add(jPanel26);
        jPanelSepLine2New2.add(jSeparator3);

        jPanel27.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New2.add(jPanel27);

        jPanelCenter.add(jPanelSepLine2New2);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanelSep2Layout = new javax.swing.GroupLayout(jPanelSep2);
        jPanelSep2.setLayout(jPanelSep2Layout);
        jPanelSep2Layout.setHorizontalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSep2Layout.setVerticalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep2);

        jPanelScroll.setLayout(new javax.swing.BoxLayout(jPanelScroll, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSpaces11.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSpaces11.setName(""); // NOI18N
        jPanelSpaces11.setPreferredSize(new java.awt.Dimension(10, 11));

        javax.swing.GroupLayout jPanelSpaces11Layout = new javax.swing.GroupLayout(jPanelSpaces11);
        jPanelSpaces11.setLayout(jPanelSpaces11Layout);
        jPanelSpaces11Layout.setHorizontalGroup(
            jPanelSpaces11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSpaces11Layout.setVerticalGroup(
            jPanelSpaces11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelScroll.add(jPanelSpaces11);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jPanelScroll.add(jScrollPane1);

        jPanelSpaces12.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSpaces12.setPreferredSize(new java.awt.Dimension(10, 11));

        javax.swing.GroupLayout jPanelSpaces12Layout = new javax.swing.GroupLayout(jPanelSpaces12);
        jPanelSpaces12.setLayout(jPanelSpaces12Layout);
        jPanelSpaces12Layout.setHorizontalGroup(
            jPanelSpaces12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSpaces12Layout.setVerticalGroup(
            jPanelSpaces12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelScroll.add(jPanelSpaces12);

        jPanelCenter.add(jPanelScroll);

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanel1.setPreferredSize(new java.awt.Dimension(376, 10));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 396, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanel1);

        jPanelSepLine2New.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New, javax.swing.BoxLayout.LINE_AXIS));

        jPanel28.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New.add(jPanel28);
        jPanelSepLine2New.add(jSeparator5);

        jPanel29.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New.add(jPanel29);

        jPanelCenter.add(jPanelSepLine2New);

        jPanelSpaces10.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanelSpaces10Layout = new javax.swing.GroupLayout(jPanelSpaces10);
        jPanelSpaces10.setLayout(jPanelSpaces10Layout);
        jPanelSpaces10Layout.setHorizontalGroup(
            jPanelSpaces10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSpaces10Layout.setVerticalGroup(
            jPanelSpaces10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSpaces10);

        getContentPane().add(jPanelCenter);

        jPanelSouth.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanelSouth.setPreferredSize(new java.awt.Dimension(62, 35));
        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        jButtonOk.setText("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonOk);

        jPanel0Pixels.setMaximumSize(new java.awt.Dimension(0, 0));
        jPanel0Pixels.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel0PixelsLayout = new javax.swing.GroupLayout(jPanel0Pixels);
        jPanel0Pixels.setLayout(jPanel0PixelsLayout);
        jPanel0PixelsLayout.setHorizontalGroup(
            jPanel0PixelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel0PixelsLayout.setVerticalGroup(
            jPanel0PixelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelSouth.add(jPanel0Pixels);

        getContentPane().add(jPanelSouth);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
    this.closeOnExit();
}//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                try {
                    //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    System.out.println("Failed loading L&F: ");
                    System.out.println(ex);
                }
                
                File file = new File("c:\\temp\\yahoo_ab.csv");
                
                new AddressBookImportCsvDisplay(null, file, ",").setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel0Pixels;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelScroll;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSepLine2New;
    private javax.swing.JPanel jPanelSepLine2New2;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelSpaces10;
    private javax.swing.JPanel jPanelSpaces11;
    private javax.swing.JPanel jPanelSpaces12;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
