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
package net.safester.application.tool;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import org.apache.commons.lang3.StringUtils;

import com.safelogic.utilx.Debug;
import com.swing.util.SwingUtil;
import java.util.Date;
import javax.swing.JEditorPane;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;

/**
 *
 * @author Nicolas de Pomereu
 */
public class TextReplaceFrame extends javax.swing.JFrame {

    public static final String CR_LF = System.getProperty("line.separator");
        
    public static boolean DEBUG = Debug.isSet(TextReplaceFrame.class);
        
    private ClipboardManager clipboard = null;
        
    private Window parent = null;
    private JEditorPane jTextPane = null;
    
    private String theTitle = MessagesManager.get("replace");
    
    /**
     * Creates new form SearchFrame
     */
    public TextReplaceFrame(Window parent, JEditorPane jTextPane) {
        initComponents();
        
        this.parent = parent;
        this.jTextPane = jTextPane;
        
        initializeIt();
        this.setVisible(true);
    }

        /**
     * This is the method to include in *our* constructor(s)
     */
    public void initializeIt() {
        
        //Dimension dim = new Dimension (498, 200);
        //this.setPreferredSize(dim);
        
        try {
            this.setIconImage(ImageParmsUtil.getAppIcon());
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }
        
        this.setTitle(theTitle);
        
        // Add a Clipboard Manager
        clipboard = new ClipboardManager(jPanelMain);
                        
        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);

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

        this.keyListenerAdder();

        /*
        if (new File(ParmsUtil.getDebugDir() + File.separator + "replace-prefill.txt").exists()) {
            jTextFieldSearch.setText("venais");
            jTextFieldReplace.setText("COCO");
        }
        */
        
        jLabelSearch.setText(MessagesManager.get("search"));
        jLabelReplace.setText(MessagesManager.get("replace"));
        jButtonReplace.setText(MessagesManager.get("replace"));
        jButtonReplaceAll.setText(MessagesManager.get("replace_all"));
        jButtonCancel.setText(MessagesManager.get("cancel"));
        jButtonNext.setText(MessagesManager.get("next"));
        jCheckBoxWordOnly.setText(MessagesManager.get("whole_word_only"));
        jCheckBoxRespectCase.setText(MessagesManager.get("case_sensitive"));
        
        this.jTextFieldSearch.requestFocusInWindow(); 
        
        // Load and activate previous windows settings
        WindowSettingManager.load(this);
        
                // Because there are not in the same panel, resize search button witdh to search reset button
        jButtonNext.setPreferredSize(jButtonReplaceAll.getPreferredSize());
        jButtonNext.setSize(jButtonReplaceAll.getSize());
        jButtonNext.setMaximumSize(jButtonReplaceAll.getMaximumSize());
        jButtonNext.setMinimumSize(jButtonReplaceAll.getMinimumSize());
        
        jButtonReplace.setPreferredSize(jButtonReplaceAll.getPreferredSize());
        jButtonReplace.setSize(jButtonReplaceAll.getSize());
        jButtonReplace.setMaximumSize(jButtonReplaceAll.getMaximumSize());
        jButtonReplace.setMinimumSize(jButtonReplaceAll.getMinimumSize());
        
        jButtonCancel.setPreferredSize(jButtonReplaceAll.getPreferredSize());
        jButtonCancel.setSize(jButtonReplaceAll.getSize());
        jButtonCancel.setMaximumSize(jButtonReplaceAll.getMaximumSize());
        jButtonCancel.setMinimumSize(jButtonReplaceAll.getMinimumSize());

        pack();
        
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

            if (keyCode == KeyEvent.VK_ENTER) {
                actionNext();
            }
                        
            if (keyCode == KeyEvent.VK_ESCAPE) {
                closeOnExit();
            }

        }
    }
    
    private int lastSearchIndexForward = 0;
    
    private void actionNext() {

        boolean ignoreCase = !jCheckBoxRespectCase.isSelected();
        boolean wordOnly = jCheckBoxWordOnly.isSelected();
 
        String searchText = jTextFieldSearch.getText();
        if (searchText.length() == 0) {
            return;
        }

        while (true) {
            int length = jTextPane.getDocument().getLength();
            
            String text = null;
            try {
                text = jTextPane.getDocument().getText(0, length);
            } catch (BadLocationException ex) {
                throw new IllegalArgumentException(ex);
            }

            if (ignoreCase) {
                text = text.toLowerCase();
                searchText = searchText.toLowerCase();
            }

            debug("lastSearchIndexForward: " + lastSearchIndexForward);
            String subText = null;
            
            if (lastSearchIndexForward > text.length()) {
                lastSearchIndexForward = text.length();
            }
            subText = text.substring(lastSearchIndexForward);

            debug("subText: " + subText);
            
            int index1 = 0;
            index1 = subText.indexOf(searchText);        
   
            if (index1 == -1) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, MessagesManager.get("sorry_text_not_found"), Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                jTextPane.select(0,0);
                lastSearchIndexForward = 0;
                return;
            }
            
            char charBefore = ' ';
            char charAfter = ' ';
            
            if (index1 != 0) {
                charBefore = subText.charAt(index1 - 1);
            }
            
            if (index1 +  searchText.length() < subText.length()) {
                charAfter = subText.charAt(index1 + searchText.length());
            }

            boolean loopForNewSearch = false;
            // Before/after char can not be letter or number 
            if (wordOnly && ( StringUtils.isAlphanumeric("" + charBefore ) || StringUtils.isAlphanumeric("" + charAfter ))) {
                loopForNewSearch = true;
            }
            
            debug("");
            debug("subtext               : " + subText);
            debug("lastSearchIndexForward: " + index1);
            debug("index1                : " + index1);
            
            debug("charBefore: " + charBefore);
            debug("charAfter : " + charAfter);

            int indexInFullText = index1 + lastSearchIndexForward;
            final int searchTextLength = searchText.length();

            if (loopForNewSearch) {
                lastSearchIndexForward = indexInFullText + searchTextLength;
                continue;
            }
            
            if (index1 == -1) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, MessagesManager.get("sorry_text_not_found"), Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                lastSearchIndexForward = 0;
                jTextPane.select(0,0);
                return;
            } else {
                jTextPane.select(indexInFullText, indexInFullText + searchTextLength);
                debug("selection: " + jTextPane.getSelectionStart() + " / " + jTextPane.getSelectionEnd());
            }

            lastSearchIndexForward = indexInFullText + searchTextLength;
            return;
        }
        
    }
    
    private void actionReplace() {
        
        String searchText = jTextFieldSearch.getText();
        if (searchText.isEmpty()) {
            return;
        }
        
        if (jTextPane.getSelectionStart() == 0 && jTextPane.getSelectionEnd() == 0) {
            actionNext();
            return;
        }
        
        int length = jTextPane.getDocument().getLength();
        String text = null;
        try {
            text = jTextPane.getDocument().getText(0, length);
        } catch (BadLocationException ex) {
            throw new IllegalArgumentException(ex);
        }
        
        int oldSelectionStart = jTextPane.getSelectionStart();
        text = text.substring(0, jTextPane.getSelectionStart()) + jTextFieldReplace.getText() + text.substring(jTextPane.getSelectionEnd());
        
        jTextPane.setText(text);
        
        //this.jTextPane.moveCaretPosition(0);
        //this.jTextPane.setSelectionEnd(0);
        jTextPane.select(oldSelectionStart, oldSelectionStart + jTextFieldReplace.getText().length());
        
        lastSearchIndexForward = lastSearchIndexForward - jTextFieldSearch.getText().length() + jTextFieldReplace.getText().length();
        
    }

    private void actionReplaceAll() {
        
        jTextPane.select(0,0);
        lastSearchIndexForward = 0;
        
        boolean ignoreCase = !jCheckBoxRespectCase.isSelected();
        boolean wordOnly = jCheckBoxWordOnly.isSelected();
        
        int length = jTextPane.getDocument().getLength();
                   String text = null;
                   
        try {
            text = jTextPane.getDocument().getText(0, length);
        } catch (BadLocationException ex) {
            throw new IllegalArgumentException(ex);
        }

        String searchText = jTextFieldSearch.getText();
        if (searchText.isEmpty()) {
            return;
        }
        
        String replaceText = jTextFieldReplace.getText();                
        
        if (ignoreCase) {
            searchText = searchText.toLowerCase();
        }
        
        int lastSearchIndexForward = 0;
        
        int cpt = 0;
        int replacesDone = 0;
                    
        while (true) {
            
            int index = -1;
            String subText = text.substring(lastSearchIndexForward);
            if (ignoreCase) {
                subText = subText.toLowerCase();
            }

            debug("subText: " + subText);
            
            int index1 = 0;
            index1 = subText.indexOf(searchText);        
   
            if (index1 == -1) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                jTextPane.setText(text);
                JOptionPane.showMessageDialog(this, MessagesManager.get("number_of_replaces") + " " + replacesDone , Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                jTextPane.select(0,0);
                lastSearchIndexForward = 0;
                return;
            }
                        
            char charBefore = ' ';
            char charAfter = ' ';
            
            if (index1 != 0) {
                charBefore = subText.charAt(index1 - 1);
            }
            
            if (index1 +  searchText.length() < subText.length()) {
                charAfter = subText.charAt(index1 + searchText.length());
            }

            boolean loopForNewSearch = false;
            // Before/after char can not be letter or number 
            if (wordOnly && ( StringUtils.isAlphanumeric("" + charBefore ) || StringUtils.isAlphanumeric("" + charAfter ))) {
                loopForNewSearch = true;
            }
            
            debug("");
            debug("subtext               : " + subText);
            debug("lastSearchIndexForward: " + index1);
            debug("index1                : " + index1);
            
            //debug("charBefore: " + charBefore);
            //debug("charAfter : " + charAfter);

            int indexInFullText = index1 + lastSearchIndexForward;
            final int searchTextLength = searchText.length();

            if (loopForNewSearch) {
                lastSearchIndexForward = indexInFullText + searchTextLength;
                continue;
            }
            
            if (index1 == -1) {
                jTextPane.setText(text);
                JOptionPane.showMessageDialog(this, " - " + MessagesManager.get("number_of_replaces") + " " + replacesDone , Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                return;
            } else {
                replacesDone++;
                
                text = text.substring(0, indexInFullText) + replaceText + text.substring(indexInFullText + searchTextLength);
                debug("text after replace: " + CR_LF + text);
                //jTextPane.select(indexInFullText, indexInFullText + searchTextLength);
                //debug("selection: " + jTextPane.getSelectionStart() + " / " + jTextPane.getSelectionEnd());
            }

            lastSearchIndexForward = indexInFullText + replaceText.length();
            continue;
 
        }
        

    }

    
        
   private void actionCancel() {
        closeOnExit();
    }
        
    public void saveSettings() {
        WindowSettingManager.save(this);
    }

    private void closeOnExit() {
        saveSettings();
        this.dispose();
    }

    /**
     * debug tool
     */
    private static void debug(String s) {
        if (DEBUG) {
            System.out.println(new Date()  + " " + s);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanelMain = new javax.swing.JPanel();
        jPanelLineSep = new javax.swing.JPanel();
        jPanelSearchLine = new javax.swing.JPanel();
        jPanelBegin = new javax.swing.JPanel();
        jLabelSearch = new javax.swing.JLabel();
        jPanel5x5_2 = new javax.swing.JPanel();
        jTextFieldSearch = new javax.swing.JTextField();
        jPanel5x5_1 = new javax.swing.JPanel();
        jPanelButtonNext = new javax.swing.JPanel();
        jButtonNext = new javax.swing.JButton();
        jPanelReplaceLine1 = new javax.swing.JPanel();
        jPanelBegin2 = new javax.swing.JPanel();
        jLabelReplace = new javax.swing.JLabel();
        jPanel5x5_3 = new javax.swing.JPanel();
        jTextFieldReplace = new javax.swing.JTextField();
        jPanel5x5_4 = new javax.swing.JPanel();
        jPanelButtonReplace = new javax.swing.JPanel();
        jButtonReplace = new javax.swing.JButton();
        jPanelLineSep2 = new javax.swing.JPanel();
        jPanelOptions = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jPanelChoices = new javax.swing.JPanel();
        jCheckBoxWordOnly = new javax.swing.JCheckBox();
        jCheckBoxRespectCase = new javax.swing.JCheckBox();
        jPanel23 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanelRight = new javax.swing.JPanel();
        jPanelButtonReplaceAll = new javax.swing.JPanel();
        jButtonReplaceAll = new javax.swing.JButton();
        jPanelButtonCancel = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jPanelLineSep1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelLineSep.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelLineSep.setMinimumSize(new java.awt.Dimension(0, 10));

        javax.swing.GroupLayout jPanelLineSepLayout = new javax.swing.GroupLayout(jPanelLineSep);
        jPanelLineSep.setLayout(jPanelLineSepLayout);
        jPanelLineSepLayout.setHorizontalGroup(
            jPanelLineSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 546, Short.MAX_VALUE)
        );
        jPanelLineSepLayout.setVerticalGroup(
            jPanelLineSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelMain.add(jPanelLineSep);

        jPanelSearchLine.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelSearchLine.setMinimumSize(new java.awt.Dimension(276, 32));
        jPanelSearchLine.setPreferredSize(new java.awt.Dimension(92, 32));
        jPanelSearchLine.setLayout(new javax.swing.BoxLayout(jPanelSearchLine, javax.swing.BoxLayout.LINE_AXIS));

        jPanelBegin.setLayout(new javax.swing.BoxLayout(jPanelBegin, javax.swing.BoxLayout.LINE_AXIS));

        jLabelSearch.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelSearch.setText("Rechercher");
        jLabelSearch.setMaximumSize(new java.awt.Dimension(90, 16));
        jLabelSearch.setMinimumSize(new java.awt.Dimension(90, 16));
        jLabelSearch.setPreferredSize(new java.awt.Dimension(90, 16));
        jLabelSearch.setRequestFocusEnabled(false);
        jPanelBegin.add(jLabelSearch);

        jPanel5x5_2.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel5x5_2.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel5x5_2Layout = new javax.swing.GroupLayout(jPanel5x5_2);
        jPanel5x5_2.setLayout(jPanel5x5_2Layout);
        jPanel5x5_2Layout.setHorizontalGroup(
            jPanel5x5_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel5x5_2Layout.setVerticalGroup(
            jPanel5x5_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelBegin.add(jPanel5x5_2);

        jTextFieldSearch.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldSearch.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanelBegin.add(jTextFieldSearch);

        jPanel5x5_1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel5x5_1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel5x5_1.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel5x5_1Layout = new javax.swing.GroupLayout(jPanel5x5_1);
        jPanel5x5_1.setLayout(jPanel5x5_1Layout);
        jPanel5x5_1Layout.setHorizontalGroup(
            jPanel5x5_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel5x5_1Layout.setVerticalGroup(
            jPanel5x5_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelBegin.add(jPanel5x5_1);

        jPanelSearchLine.add(jPanelBegin);

        jPanelButtonNext.setMaximumSize(new java.awt.Dimension(170, 25));
        jPanelButtonNext.setMinimumSize(new java.awt.Dimension(170, 25));
        jPanelButtonNext.setPreferredSize(new java.awt.Dimension(170, 25));
        jPanelButtonNext.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 0));

        jButtonNext.setText("Suivant");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        jPanelButtonNext.add(jButtonNext);

        jPanelSearchLine.add(jPanelButtonNext);

        jPanelMain.add(jPanelSearchLine);

        jPanelReplaceLine1.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelReplaceLine1.setMinimumSize(new java.awt.Dimension(276, 32));
        jPanelReplaceLine1.setPreferredSize(new java.awt.Dimension(92, 32));
        jPanelReplaceLine1.setLayout(new javax.swing.BoxLayout(jPanelReplaceLine1, javax.swing.BoxLayout.LINE_AXIS));

        jPanelBegin2.setLayout(new javax.swing.BoxLayout(jPanelBegin2, javax.swing.BoxLayout.LINE_AXIS));

        jLabelReplace.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelReplace.setText("Remplacer");
        jLabelReplace.setMaximumSize(new java.awt.Dimension(90, 16));
        jLabelReplace.setMinimumSize(new java.awt.Dimension(90, 16));
        jLabelReplace.setPreferredSize(new java.awt.Dimension(90, 16));
        jPanelBegin2.add(jLabelReplace);

        jPanel5x5_3.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel5x5_3.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel5x5_3Layout = new javax.swing.GroupLayout(jPanel5x5_3);
        jPanel5x5_3.setLayout(jPanel5x5_3Layout);
        jPanel5x5_3Layout.setHorizontalGroup(
            jPanel5x5_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel5x5_3Layout.setVerticalGroup(
            jPanel5x5_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelBegin2.add(jPanel5x5_3);

        jTextFieldReplace.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldReplace.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanelBegin2.add(jTextFieldReplace);

        jPanel5x5_4.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel5x5_4.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel5x5_4Layout = new javax.swing.GroupLayout(jPanel5x5_4);
        jPanel5x5_4.setLayout(jPanel5x5_4Layout);
        jPanel5x5_4Layout.setHorizontalGroup(
            jPanel5x5_4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel5x5_4Layout.setVerticalGroup(
            jPanel5x5_4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelBegin2.add(jPanel5x5_4);

        jPanelReplaceLine1.add(jPanelBegin2);

        jPanelButtonReplace.setMaximumSize(new java.awt.Dimension(170, 25));
        jPanelButtonReplace.setMinimumSize(new java.awt.Dimension(170, 25));
        jPanelButtonReplace.setPreferredSize(new java.awt.Dimension(170, 25));
        jPanelButtonReplace.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 0));

        jButtonReplace.setText("Remplacer");
        jButtonReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReplaceActionPerformed(evt);
            }
        });
        jPanelButtonReplace.add(jButtonReplace);

        jPanelReplaceLine1.add(jPanelButtonReplace);

        jPanelMain.add(jPanelReplaceLine1);

        jPanelLineSep2.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelLineSep2.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelLineSep2.setPreferredSize(new java.awt.Dimension(488, 5));

        javax.swing.GroupLayout jPanelLineSep2Layout = new javax.swing.GroupLayout(jPanelLineSep2);
        jPanelLineSep2.setLayout(jPanelLineSep2Layout);
        jPanelLineSep2Layout.setHorizontalGroup(
            jPanelLineSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 546, Short.MAX_VALUE)
        );
        jPanelLineSep2Layout.setVerticalGroup(
            jPanelLineSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelMain.add(jPanelLineSep2);

        jPanelOptions.setLayout(new javax.swing.BoxLayout(jPanelOptions, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft.setLayout(new javax.swing.BoxLayout(jPanelLeft, javax.swing.BoxLayout.X_AXIS));

        jPanel25.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelLeft.add(jPanel25);

        jPanelChoices.setLayout(new javax.swing.BoxLayout(jPanelChoices, javax.swing.BoxLayout.Y_AXIS));

        jCheckBoxWordOnly.setText("Mot entier uniquement");
        jPanelChoices.add(jCheckBoxWordOnly);

        jCheckBoxRespectCase.setText("Respecter la casse");
        jPanelChoices.add(jCheckBoxRespectCase);

        jPanelLeft.add(jPanelChoices);

        jPanel23.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelLeft.add(jPanel23);

        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 10));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 208, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelLeft.add(jPanel4);

        jPanelOptions.add(jPanelLeft);

        jPanelRight.setLayout(new javax.swing.BoxLayout(jPanelRight, javax.swing.BoxLayout.Y_AXIS));

        jPanelButtonReplaceAll.setMaximumSize(new java.awt.Dimension(170, 32));
        jPanelButtonReplaceAll.setMinimumSize(new java.awt.Dimension(170, 32));
        jPanelButtonReplaceAll.setPreferredSize(new java.awt.Dimension(170, 32));
        jPanelButtonReplaceAll.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 0));

        jButtonReplaceAll.setText("Remplacer tout");
        jButtonReplaceAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReplaceAllActionPerformed(evt);
            }
        });
        jPanelButtonReplaceAll.add(jButtonReplaceAll);

        jPanelRight.add(jPanelButtonReplaceAll);

        jPanelButtonCancel.setMaximumSize(new java.awt.Dimension(170, 32));
        jPanelButtonCancel.setMinimumSize(new java.awt.Dimension(170, 32));
        jPanelButtonCancel.setPreferredSize(new java.awt.Dimension(170, 32));
        jPanelButtonCancel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 0));

        jButtonCancel.setText("Annuler");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelButtonCancel.add(jButtonCancel);

        jPanelRight.add(jPanelButtonCancel);

        jPanelOptions.add(jPanelRight);

        jPanelMain.add(jPanelOptions);

        jPanelLineSep1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelLineSep1.setMinimumSize(new java.awt.Dimension(0, 10));

        javax.swing.GroupLayout jPanelLineSep1Layout = new javax.swing.GroupLayout(jPanelLineSep1);
        jPanelLineSep1.setLayout(jPanelLineSep1Layout);
        jPanelLineSep1Layout.setHorizontalGroup(
            jPanelLineSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 546, Short.MAX_VALUE)
        );
        jPanelLineSep1Layout.setVerticalGroup(
            jPanelLineSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelMain.add(jPanelLineSep1);

        getContentPane().add(jPanelMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
          actionNext();
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonReplaceAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReplaceAllActionPerformed
        actionReplaceAll();
    }//GEN-LAST:event_jButtonReplaceAllActionPerformed

    private void jButtonReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReplaceActionPerformed
        actionReplace();
    }//GEN-LAST:event_jButtonReplaceActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        actionCancel();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TextReplaceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TextReplaceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TextReplaceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TextReplaceFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TextReplaceFrame(null, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonReplace;
    private javax.swing.JButton jButtonReplaceAll;
    private javax.swing.JCheckBox jCheckBoxRespectCase;
    private javax.swing.JCheckBox jCheckBoxWordOnly;
    private javax.swing.JLabel jLabelReplace;
    private javax.swing.JLabel jLabelSearch;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5x5_1;
    private javax.swing.JPanel jPanel5x5_2;
    private javax.swing.JPanel jPanel5x5_3;
    private javax.swing.JPanel jPanel5x5_4;
    private javax.swing.JPanel jPanelBegin;
    private javax.swing.JPanel jPanelBegin2;
    private javax.swing.JPanel jPanelButtonCancel;
    private javax.swing.JPanel jPanelButtonNext;
    private javax.swing.JPanel jPanelButtonReplace;
    private javax.swing.JPanel jPanelButtonReplaceAll;
    private javax.swing.JPanel jPanelChoices;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelLineSep;
    private javax.swing.JPanel jPanelLineSep1;
    private javax.swing.JPanel jPanelLineSep2;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelOptions;
    private javax.swing.JPanel jPanelReplaceLine1;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSearchLine;
    private javax.swing.JTextField jTextFieldReplace;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables



}
