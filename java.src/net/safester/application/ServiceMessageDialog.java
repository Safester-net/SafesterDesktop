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

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.lang3.StringUtils;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.client.AwakeUrl;
import org.awakefw.sql.api.client.AwakeConnection;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.DesktopWrapper;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.UserPrefManager;
import net.safester.clientserver.util.TestAwakeConnection;


/**
 * This dialog is displayed on connection if a host /newstart/message_service.html file exists.
 * Then, it's content is displayed on a discardable JDialog
 * 
 * @author Nicolas de Pomereu
 */
public class ServiceMessageDialog extends javax.swing.JDialog {

    private MessagesManager messages = new MessagesManager();

    private Connection connection = null;

    Frame parent;
    
    private boolean messageServiceOn = true;
    
    public ServiceMessageDialog(java.awt.Frame parent, Connection theConnection) throws Exception {
        super(parent);
        initComponents();

      // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        this.parent = parent;
        initCompany();
        this.setLocationRelativeTo(parent);
    }

    private void initCompany() throws Exception {
        
        try
        {
            this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

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
                
        this.setTitle(messages.getMessage("service_message"));

        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                try {
                    final URL url = e.getURL();
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        DesktopWrapper.browse(url);
                    }
                } catch (Exception ex) {
                    JOptionPaneNewCustom.showException(rootPane, ex);
                }

            }
        });

        
        boolean doDiscard = UserPrefManager.getBooleanPreference(UserPrefManager.EXPIRED_TRIAL_DIALOG_DISCARD + "_" + net.safester.application.version.Version.VERSION);        
        
        if (!doDiscard) {            
            jEditorPane.setText(this.getHtmlContent());
        }
        else {
             messageServiceOn = false;
        }
            
        this.jButtonOk.setText(messages.getMessage("ok"));
                        
        jCheckBoxDiscard.setText(messages.getMessage("DO_NOT_SHOW_ME_AGAIN_MSG"));
        
        ButtonResizer br = new ButtonResizer(jPanelButton);
        br.setWidthToMax();
        this.setSize(600, 600);

        WindowSettingManager.load(this);

    }

    /**
     *
     * @return the content that is mixed: header is local & product table is on safester.net
     */
    private String getHtmlContent() throws IOException
    {
        String content = null;

        try
        {
            AwakeConnection awakeConnection =  (AwakeConnection)connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            String host = awakeFileSession.getUrl();
            host = StringUtils.substringBeforeLast(host, "/");

            URL url = new URL(host + "/newstart/service_message.html");
            
            //content += awakeFileSession.getUrlContent(url);
            AwakeUrl awakeUrl = new AwakeUrl(awakeFileSession.getHttpProxy(), awakeFileSession.getHttpProtocolParameters());
            content = awakeUrl.download(url);

        }
        catch (FileNotFoundException fnfe)
        {
            messageServiceOn = false;
        }

        return content;
    }

    /**
     * @return the messageServiceOn
     */
    public boolean isMessageServiceOn() {
        return messageServiceOn;
    }
        
    
    /** Close window ans says we are logged out */
    public void close()
    {
        WindowSettingManager.save(this);
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

        buttonGroupProduct = new javax.swing.ButtonGroup();
        jPanelNorth = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelTitle = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane = new javax.swing.JEditorPane();
        jPanelSepBottom = new javax.swing.JPanel();
        jPanelDiscard = new javax.swing.JPanel();
        jCheckBoxDiscard = new javax.swing.JCheckBox();
        jPanelSepBottom1 = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanelButton = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelWest.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelEast.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setBackground(new java.awt.Color(255, 255, 255));
        jPanelCenter.setRequestFocusEnabled(false);
        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelTitle.setMaximumSize(new java.awt.Dimension(32767, 70));
        jPanelTitle.setMinimumSize(new java.awt.Dimension(80, 70));
        jPanelTitle.setPreferredSize(new java.awt.Dimension(80, 70));
        jPanelTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/logo-safester-transparent_small.png"))); // NOI18N
        jPanelTitle.add(jLabelTitle);

        jPanelCenter.add(jPanelTitle);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jEditorPane.setBorder(null);
        jEditorPane.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jEditorPane);

        jPanelCenter.add(jScrollPane1);

        jPanelSepBottom.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelSepBottom.setMinimumSize(new java.awt.Dimension(10, 4));
        jPanelSepBottom.setPreferredSize(new java.awt.Dimension(10, 4));
        jPanelCenter.add(jPanelSepBottom);

        jPanelDiscard.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelDiscard.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jCheckBoxDiscard.setText("jCheckBoxDiscard");
        jPanelDiscard.add(jCheckBoxDiscard);

        jPanelCenter.add(jPanelDiscard);

        jPanelSepBottom1.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelSepBottom1.setMinimumSize(new java.awt.Dimension(10, 4));
        jPanelSepBottom1.setPreferredSize(new java.awt.Dimension(10, 4));
        jPanelCenter.add(jPanelSepBottom1);

        jPanelSep.setMinimumSize(new java.awt.Dimension(10, 6));
        jPanelSep.setPreferredSize(new java.awt.Dimension(10, 6));
        jPanelSep.setLayout(new javax.swing.BoxLayout(jPanelSep, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSep.add(jSeparator2);

        jPanelCenter.add(jPanelSep);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new javax.swing.BoxLayout(jPanelSouth, javax.swing.BoxLayout.LINE_AXIS));

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        jPanel8.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel8.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel8.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanel7.add(jPanel8);

        jPanelSouth.add(jPanel7);

        jPanelButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonOk.setText("jButtonOk");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelButton.add(jButtonOk);

        jPanel5.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel5.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel5.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelButton.add(jPanel5);

        jPanelSouth.add(jPanelButton);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed

        if (jCheckBoxDiscard.isSelected())
        {
            UserPrefManager.setPreference(
                    UserPrefManager.EXPIRED_TRIAL_DIALOG_DISCARD + "_" + net.safester.application.version.Version.VERSION  , true);
        }
        
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])  {

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                Connection connection = TestAwakeConnection.getConnection();

                ServiceMessageDialog dialog = null;
                try {
                    dialog = new ServiceMessageDialog(new javax.swing.JFrame(), connection);
                } catch (Exception ex) {
                    Logger.getLogger(ServiceMessageDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
                
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
    private javax.swing.ButtonGroup buttonGroupProduct;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JCheckBox jCheckBoxDiscard;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelDiscard;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSepBottom;
    private javax.swing.JPanel jPanelSepBottom1;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables


}
