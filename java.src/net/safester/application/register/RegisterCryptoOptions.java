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
package net.safester.application.register;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.swing.util.LookAndFeelHelper;
import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.CryptoParms;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.DesktopWrapper;
import net.safester.application.util.HtmlTextUtil;
        
/**
 *
 * @author Alexandre Becquereau
 */
public class RegisterCryptoOptions extends javax.swing.JDialog {

    //private JXDatePicker datePicker;
    private MessagesManager messages = new MessagesManager();

    private Frame parent = null;

    private String algoAsym;
    private int asymKeyLength;
    private String algoSymmetric;

    /** Creates new form SafeShareItRegisterAdvancedSettingDialiog */

    public RegisterCryptoOptions(java.awt.Frame parent,
                                                     String algoAsym,
                                                     int asymKeyLength,
                                                     String algoSymmetric
                                                     )
    {

        this.parent = parent;
        this.algoAsym = algoAsym;
        this.asymKeyLength = asymKeyLength;
        this.algoSymmetric = algoSymmetric;

        initComponents();
        initCompany();

    }

    private void initCompany()
    {
        this.setLocationRelativeTo(parent);
        this.setTitle(messages.getMessage("cryptography_settings"));
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        jEditorPane.setText(HtmlTextUtil.getHtmlHelpContent("cryptography_settings"));

        jLabelTitle.setText(messages.getMessage("cryptography_settings"));
        jLabelAsymAlgo.setText(messages.getMessage("asymetric_algorithm"));
        jLabeAsymKeyLength.setText(this.messages.getMessage("key_size"));
        jComboBoxAlgoAsym.removeAllItems();
        
        for(int i = 0; i<CryptoParms.KEY_ALGOS_ASYM.length; i++)
        {
            jComboBoxAlgoAsym.addItem(CryptoParms.KEY_ALGOS_ASYM[i]);
        }

        jComboBoxKeyLength.removeAllItems();
        for(int i = 0; i<CryptoParms.KEY_LENGTHS_ASYM.length; i++)
        {
            jComboBoxKeyLength.addItem(CryptoParms.KEY_LENGTHS_ASYM[i]);
        }

        jLabelAlgoSymmetric.setText(messages.getMessage("key_sym_algo"));

        jComboBoxAlgoSymmetric.removeAllItems();
        for(int i=0;i<CryptoParms.KEY_ALGOS_SYM.length;i++)
        {
            jComboBoxAlgoSymmetric.addItem(CryptoParms.KEY_ALGOS_SYM[i]);
        }

        jComboBoxAlgoAsym.setSelectedItem(this.algoAsym);
        jComboBoxKeyLength.setSelectedItem(Integer.toString(asymKeyLength));
        jComboBoxAlgoSymmetric.setSelectedItem(this.algoSymmetric);

        jButtonOk.setText(messages.getMessage("ok"));
        jButtonClose.setText(messages.getMessage("cancel"));

        //jPanel9.remove(jPanelExpDate);
        ButtonResizer br = new ButtonResizer(jPanelButtons);
        br.setWidthToMax();

        addHyperLinkListener();
        keyListenerAdder();
        
        jPanelSouth1.setBackground(LookAndFeelHelper.getDefaultBackgroundColor());
        jPanelNorth1.setBackground(LookAndFeelHelper.getDefaultBackgroundColor());
        jPanelEast1.setBackground(LookAndFeelHelper.getDefaultBackgroundColor());
        jPanelWest1.setBackground(LookAndFeelHelper.getDefaultBackgroundColor());

        this.setSize(470,420);
    }

    private void addHyperLinkListener()
    {
        // Hyperlink listener that will open a new Broser with the given URL
        jEditorPane.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent r)
            {
                // System.out.println(r.getDescription());
                // System.out.println(r.getURL());

                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    if (r.getDescription().startsWith("com."))
                    {
                        try
                        {
                            Class<?> c = Class.forName(r.getDescription());
                            Object theObject = c.newInstance();
                        }
                        catch (Exception e)
                        {
                            // e.printStackTrace();
                        }
                    }
                    else
                    {
                        DesktopWrapper.browse(r.getURL());
                    }
                }
            }

        });
    }

    /**
     * Universal key listener
     */
    private void keyListenerAdder()
    {
        java.util.List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++)
        {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e)
                {
                    this_keyPressed(e);
                }
            });
        }
    }

    private void this_keyPressed(KeyEvent e)
    {
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED)
        {
            //System.out.println("Key Realeased");
            //System.out.println("TextFieldUserEmail.getText():" + jTextFieldUserEmail.getText() + ":");

            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE)
            {
                this.dispose();
                return;
            }

            if (keyCode == KeyEvent.VK_ENTER)
            {
                doIt();
                return;
            }

            if (keyCode == KeyEvent.VK_F1)
            {
               // helpKeys();
            }

        }
    }



    private void doIt()
    {
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        
        this.algoAsym = (String)this.jComboBoxAlgoAsym.getSelectedItem();
        this.asymKeyLength = Integer.parseInt((String)this.jComboBoxKeyLength.getSelectedItem());
        this.algoSymmetric = (String)this.jComboBoxAlgoSymmetric .getSelectedItem();
       
        this.dispose();

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
        jPanelNorth = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelUp = new javax.swing.JPanel();
        jLabelMiniIcon = new javax.swing.JLabel();
        jLabelTitle = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelBlank = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelBlank1 = new javax.swing.JPanel();
        jPanelEditorPane = new javax.swing.JPanel();
        jEditorPane = new javax.swing.JEditorPane();
        jPanelNorth1 = new javax.swing.JPanel();
        jPanelSouth1 = new javax.swing.JPanel();
        jPanelEast1 = new javax.swing.JPanel();
        jPanelWest1 = new javax.swing.JPanel();
        jPanelBlank3 = new javax.swing.JPanel();
        jPanelCryptpValues = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabelAsymAlgo = new javax.swing.JLabel();
        jComboBoxAlgoAsym = new javax.swing.JComboBox();
        jPanel35 = new javax.swing.JPanel();
        jLabeAsymKeyLength = new javax.swing.JLabel();
        jComboBoxKeyLength = new javax.swing.JComboBox();
        jPanel14 = new javax.swing.JPanel();
        jLabelAlgoSymmetric = new javax.swing.JLabel();
        jComboBoxAlgoSymmetric = new javax.swing.JComboBox();
        jPanelBlank2 = new javax.swing.JPanel();
        jPanelBlank4 = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jPanel30 = new javax.swing.JPanel();
        jPanelRight = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        jPanelNorth.setMinimumSize(new java.awt.Dimension(10, 12));

        javax.swing.GroupLayout jPanelNorthLayout = new javax.swing.GroupLayout(jPanelNorth);
        jPanelNorth.setLayout(jPanelNorthLayout);
        jPanelNorthLayout.setHorizontalGroup(
            jPanelNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 547, Short.MAX_VALUE)
        );
        jPanelNorthLayout.setVerticalGroup(
            jPanelNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelUp.setMaximumSize(new java.awt.Dimension(32767, 48));
        jPanelUp.setMinimumSize(new java.awt.Dimension(83, 48));
        jPanelUp.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelMiniIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/key_plus.png"))); // NOI18N
        jPanelUp.add(jLabelMiniIcon);

        jLabelTitle.setText("jLabelTitle");
        jPanelUp.add(jLabelTitle);

        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanelUp.add(jSeparator3);

        jPanelCenter.add(jPanelUp);

        jPanelBlank.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelBlank.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelBlank.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelBlankLayout = new javax.swing.GroupLayout(jPanelBlank);
        jPanelBlank.setLayout(jPanelBlankLayout);
        jPanelBlankLayout.setHorizontalGroup(
            jPanelBlankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 527, Short.MAX_VALUE)
        );
        jPanelBlankLayout.setVerticalGroup(
            jPanelBlankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelBlank);

        jPanelSep.setMinimumSize(new java.awt.Dimension(390, 105));
        jPanelSep.setLayout(new javax.swing.BoxLayout(jPanelSep, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanelSep.add(jSeparator2);

        jPanelCenter.add(jPanelSep);

        jPanelBlank1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelBlank1.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelBlank1.setPreferredSize(new java.awt.Dimension(527, 10));

        javax.swing.GroupLayout jPanelBlank1Layout = new javax.swing.GroupLayout(jPanelBlank1);
        jPanelBlank1.setLayout(jPanelBlank1Layout);
        jPanelBlank1Layout.setHorizontalGroup(
            jPanelBlank1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 527, Short.MAX_VALUE)
        );
        jPanelBlank1Layout.setVerticalGroup(
            jPanelBlank1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelBlank1);

        jPanelEditorPane.setBackground(new java.awt.Color(255, 255, 255));
        jPanelEditorPane.setLayout(new java.awt.BorderLayout());

        jEditorPane.setBorder(null);
        jPanelEditorPane.add(jEditorPane, java.awt.BorderLayout.CENTER);

        jPanelNorth1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelNorth1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelNorth1.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelNorth1.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelEditorPane.add(jPanelNorth1, java.awt.BorderLayout.PAGE_START);

        jPanelSouth1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelSouth1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSouth1.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSouth1.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelEditorPane.add(jPanelSouth1, java.awt.BorderLayout.SOUTH);

        jPanelEast1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelEast1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelEast1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelEast1.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelEditorPane.add(jPanelEast1, java.awt.BorderLayout.EAST);

        jPanelWest1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelWest1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelWest1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelWest1.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelEditorPane.add(jPanelWest1, java.awt.BorderLayout.WEST);

        jPanelCenter.add(jPanelEditorPane);

        jPanelBlank3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelBlank3.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelBlank3.setPreferredSize(new java.awt.Dimension(527, 10));

        javax.swing.GroupLayout jPanelBlank3Layout = new javax.swing.GroupLayout(jPanelBlank3);
        jPanelBlank3.setLayout(jPanelBlank3Layout);
        jPanelBlank3Layout.setHorizontalGroup(
            jPanelBlank3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 527, Short.MAX_VALUE)
        );
        jPanelBlank3Layout.setVerticalGroup(
            jPanelBlank3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelBlank3);

        jPanelCryptpValues.setLayout(new javax.swing.BoxLayout(jPanelCryptpValues, javax.swing.BoxLayout.Y_AXIS));

        jPanel7.setMaximumSize(new java.awt.Dimension(32767, 64));
        jPanel7.setMinimumSize(new java.awt.Dimension(547, 64));
        jPanel7.setLayout(new java.awt.GridLayout(1, 0));

        jPanel9.setMaximumSize(new java.awt.Dimension(32767, 64));
        jPanel9.setMinimumSize(new java.awt.Dimension(547, 64));
        jPanel9.setPreferredSize(new java.awt.Dimension(356, 60));
        jPanel9.setLayout(new java.awt.GridLayout(2, 0));

        jPanel6.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanel6.setMinimumSize(new java.awt.Dimension(83, 24));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jLabelAsymAlgo.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelAsymAlgo.setText("jLabelAsymAlgo");
        jLabelAsymAlgo.setPreferredSize(new java.awt.Dimension(130, 15));
        jPanel6.add(jLabelAsymAlgo);

        jComboBoxAlgoAsym.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel6.add(jComboBoxAlgoAsym);

        jPanel35.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel35.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanel6.add(jPanel35);

        jLabeAsymKeyLength.setText("jLabeAsymKeyLength");
        jPanel6.add(jLabeAsymKeyLength);

        jComboBoxKeyLength.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel6.add(jComboBoxKeyLength);

        jPanel9.add(jPanel6);

        jPanel14.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanel14.setMinimumSize(new java.awt.Dimension(83, 24));
        jPanel14.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jLabelAlgoSymmetric.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelAlgoSymmetric.setText("jLabelKeyType");
        jLabelAlgoSymmetric.setPreferredSize(new java.awt.Dimension(130, 15));
        jPanel14.add(jLabelAlgoSymmetric);

        jComboBoxAlgoSymmetric.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxAlgoSymmetric.setEnabled(false);
        jPanel14.add(jComboBoxAlgoSymmetric);

        jPanel9.add(jPanel14);

        jPanel7.add(jPanel9);

        jPanelCryptpValues.add(jPanel7);

        jPanelCenter.add(jPanelCryptpValues);

        jPanelBlank2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelBlank2.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelBlank2.setPreferredSize(new java.awt.Dimension(527, 10));

        javax.swing.GroupLayout jPanelBlank2Layout = new javax.swing.GroupLayout(jPanelBlank2);
        jPanelBlank2.setLayout(jPanelBlank2Layout);
        jPanelBlank2Layout.setHorizontalGroup(
            jPanelBlank2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 527, Short.MAX_VALUE)
        );
        jPanelBlank2Layout.setVerticalGroup(
            jPanelBlank2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelBlank2);

        jPanelBlank4.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelBlank4.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelBlank4Layout = new javax.swing.GroupLayout(jPanelBlank4);
        jPanelBlank4.setLayout(jPanelBlank4Layout);
        jPanelBlank4Layout.setHorizontalGroup(
            jPanelBlank4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 527, Short.MAX_VALUE)
        );
        jPanelBlank4Layout.setVerticalGroup(
            jPanelBlank4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelBlank4);

        jPanelSep1.setMinimumSize(new java.awt.Dimension(390, 105));
        jPanelSep1.setLayout(new javax.swing.BoxLayout(jPanelSep1, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator4.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanelSep1.add(jSeparator4);

        jPanelCenter.add(jPanelSep1);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelButtons.setPreferredSize(new java.awt.Dimension(10, 43));
        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 6, 10));

        jButtonOk.setText("jButtonOk");
        jButtonOk.setActionCommand("jButton");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonOk);

        jButtonClose.setText("jButtonClose");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonClose);

        jPanel30.setMinimumSize(new java.awt.Dimension(0, 10));

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelButtons.add(jPanel30);

        getContentPane().add(jPanelButtons, java.awt.BorderLayout.SOUTH);

        jPanelRight.setPreferredSize(new java.awt.Dimension(10, 153));

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelRight, java.awt.BorderLayout.EAST);

        jPanelLeft.setPreferredSize(new java.awt.Dimension(10, 153));

        javax.swing.GroupLayout jPanelLeftLayout = new javax.swing.GroupLayout(jPanelLeft);
        jPanelLeft.setLayout(jPanelLeftLayout);
        jPanelLeftLayout.setHorizontalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelLeftLayout.setVerticalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelLeft, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        doIt();
}//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
      
        this.dispose();
}//GEN-LAST:event_jButtonCloseActionPerformed

    /**
     * @return the algoAsym
     */
    public String getAlgoAsym() {
        return algoAsym;
    }

    /**
     * @return the asymKeyLength
     */
    public int getAsymKeyLength() {
        return asymKeyLength;
    }

    /**
     * @return the algoSymmetric
     */
    public String getAlgoSymmetric() {
        return algoSymmetric;
    }
    
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
            public void run() {

                String algoAsym = CryptoParms.KEY_ALGOS_ASYM[0];
                int asymKeyLength = Integer.parseInt(CryptoParms.KEY_LENGTHS_ASYM[0]);
                String algoSymmetric = CryptoParms.KEY_ALGOS_SYM[0];
    
                RegisterCryptoOptions dialog
                        = new RegisterCryptoOptions(new javax.swing.JFrame(),
                                algoAsym, asymKeyLength, algoSymmetric);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JComboBox jComboBoxAlgoAsym;
    private javax.swing.JComboBox jComboBoxAlgoSymmetric;
    private javax.swing.JComboBox jComboBoxKeyLength;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabeAsymKeyLength;
    private javax.swing.JLabel jLabelAlgoSymmetric;
    private javax.swing.JLabel jLabelAsymAlgo;
    private javax.swing.JLabel jLabelMiniIcon;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelBlank;
    private javax.swing.JPanel jPanelBlank1;
    private javax.swing.JPanel jPanelBlank2;
    private javax.swing.JPanel jPanelBlank3;
    private javax.swing.JPanel jPanelBlank4;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCryptpValues;
    private javax.swing.JPanel jPanelEast1;
    private javax.swing.JPanel jPanelEditorPane;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelNorth1;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSouth1;
    private javax.swing.JPanel jPanelUp;
    private javax.swing.JPanel jPanelWest1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    // End of variables declaration//GEN-END:variables



}
