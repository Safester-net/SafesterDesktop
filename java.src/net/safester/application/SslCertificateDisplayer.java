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
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

import org.awakefw.commons.api.client.HttpProxy;

import com.swing.util.SwingUtil;
import net.safester.application.http.dto.SystemInfoDTO;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.TableModelNonEditable;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.JTableUtil;
import net.safester.application.util.SslCertExtractor;
import net.safester.application.util.TableClipboardManager;
import net.safester.clientserver.ServerParms;

/**
 *
 * @author  Nicolas de Pomereu
 */
public class SslCertificateDisplayer extends javax.swing.JFrame {

    public static final String CR_LF = System.getProperty("line.separator");

    /** The parent JFrame */
    private JFrame parentJframe = null;
    private JFrame thisOne = null;
    
    private MessagesManager messages = new MessagesManager();

    /** Pop Up menu */
    JPopupMenu popupMenu;
    private Font m_font = new Font("Tahoma", Font.PLAIN, 13);
    
    /** Add a clipboard manager for right button mouse control over input text fields */
    public TableClipboardManager clipboard = null;

    /** The ssl host */
    private String host = null;

    /** The http proxy to use for access to host */
    private HttpProxy httpProxy = null;
    private SystemInfoDTO systemInfoDTO = null;
    
    /** Creates new form NewsFrame
     * @param parentJframe
     * @param host
     * @param httpProxy
     * @param systemInfoDTO */
    public SslCertificateDisplayer(JFrame parentJframe, String host, HttpProxy httpProxy, SystemInfoDTO systemInfoDTO) {
        this.parentJframe = parentJframe;

        if (host == null || ! host.toLowerCase().startsWith("https://"))
        {
            JOptionPane.showMessageDialog(this, messages.getMessage("no_ssl_info"));
            return;
        }

        initComponents();
        
        thisOne = this;
        this.host = host;
        this.httpProxy = httpProxy;
        this.systemInfoDTO = systemInfoDTO;

        initializeCompany();
        this.setVisible(true);
    }

    /**
     * This is the method to include in the constructor
     */
    private void initializeCompany() {

        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.setSize(464, 384);
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        
        if (parentJframe != null) {
            this.setLocationRelativeTo(parentJframe);
        }
        
        String titleMessage =  host + " - " + messages.getMessage("ssl_certificate_info");        
        this.jLabelTitle.setText(titleMessage);
        this.setTitle(titleMessage);

        this.jButtonClose.setText(messages.getMessage("ok"));   
        this.jButtonRemoteSystemInfo.setText(messages.getMessage("remote_system_info"));   
        
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
        

        this.setVisible(true);
    }

    /**
     * Will (re)create the JTable with all the public keys
     *
     */
    private void createTable() {
        try {
            jTable1 = create();
        } catch (Exception ex) {
            JOptionPaneNewCustom.showException(this, ex);
            return;
        }

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
     * Create a JTable with all system properties
     * @return a JTable with all system properties
     */
    public JTable create() throws IOException, CertificateEncodingException, NoSuchAlgorithmException, NoSuchProviderException
    {

        Object[] colName = null;
        int columnsNumber = 0;

        columnsNumber = 2;
        colName = new Object[columnsNumber];

        colName[0] = this.messages.getMessage("field");
        colName[1] = this.messages.getMessage("value");

        SslCertExtractor sslCertExtractor = new SslCertExtractor(host, httpProxy);
        Map<String, String> map = sslCertExtractor.getCertInfo();

        Iterator keys = sslCertExtractor.getCertInfo().keySet().iterator();

        List<String> listKeys = new ArrayList<String>();

        while(keys.hasNext())
        {
            String key      = (String)keys.next() ;
            listKeys.add(key);
        }

        // Collections.sort(listKeys); // NO SORT!

        int lineNumber = listKeys.size();

        Object [][] data = new Object[lineNumber][columnsNumber];

        for(int i = 0; i< listKeys.size(); i++)
        {
            String key      = listKeys.get(i);
            String value    = map.get(key);

            data[i][0] = key;
            data[i][1] = value;
        }

        JTable jTable1  = new JTable(new TableModelNonEditable(data , colName ));

        // Set the Table Header Display
        Font fontHeader = new Font(m_font.getName(), Font.PLAIN, m_font.getSize());
        JTableHeader jTableHeader = jTable1.getTableHeader();
        jTableHeader.setFont(fontHeader);
        jTable1.setTableHeader(jTableHeader);

        jTable1.setAutoCreateRowSorter(true);

        jTable1.setFont(m_font);
        jTable1.setColumnSelectionAllowed(false);
        jTable1.setRowSelectionAllowed(true);
        jTable1.setAutoscrolls(true);

        //jTable1.setColumnModel(new MyTableColumnModel());
        //jTable1.setAutoCreateColumnsFromModel(true);

        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(true);

        jTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Resize last column (if necessary)
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // use an Expansion factor of 1.3x for change between 12 and Arial,17
        JTableUtil.calcColumnWidths(jTable1, 1.00);

        return jTable1;
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
        jPanel3 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanelSep = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButtonRemoteSystemInfo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jPanelEast = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        jPanel3.setMaximumSize(new java.awt.Dimension(1, 1));
        jPanel3.setMinimumSize(new java.awt.Dimension(1, 1));
        jPanel3.setPreferredSize(new java.awt.Dimension(1, 1));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanelNorth.add(jPanel3);

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/lock_ok.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanelNorth.add(jLabelTitle);

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSep1.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelSep1.setMinimumSize(new java.awt.Dimension(0, 4));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(608, 4));

        javax.swing.GroupLayout jPanelSep1Layout = new javax.swing.GroupLayout(jPanelSep1);
        jPanelSep1.setLayout(jPanelSep1Layout);
        jPanelSep1Layout.setHorizontalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 676, Short.MAX_VALUE)
        );
        jPanelSep1Layout.setVerticalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 4, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep1);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Field", "Value"
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
            .addGap(0, 676, Short.MAX_VALUE)
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
            .addGap(0, 400, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelSouth.setLayout(new java.awt.GridLayout(1, 2));

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        jButtonRemoteSystemInfo.setText("Remote System Info");
        jButtonRemoteSystemInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoteSystemInfoActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonRemoteSystemInfo);

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
            .addGap(0, 400, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelEast, java.awt.BorderLayout.LINE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    dispose();
}//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonRemoteSystemInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoteSystemInfoActionPerformed

        String crLf = System.getProperty("line.separator");
        String message = "javaRuntimeName=" + systemInfoDTO.getJavaRuntimeName() + crLf + "javaVendor=" + systemInfoDTO.getJavaVendor() + crLf + "javaVersion=" + systemInfoDTO.getJavaVersion();

        JOptionPane.showMessageDialog(parentJframe, message, "Safester", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButtonRemoteSystemInfoActionPerformed

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

            @Override
            public void run() {
                String url = ServerParms.getHOST();
                new SslCertificateDisplayer(null, url, null, null);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonRemoteSystemInfo;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
