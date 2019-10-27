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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.util.List;

import javax.swing.SwingUtilities;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import com.safelogic.utilx.Debug;
import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.TableClipboardManager;
import net.safester.clientserver.UserSettingsExtractor;
import net.safester.clientserver.holder.TheUserSettingsHolder;
import net.safester.noobs.clientserver.UserSettingsLocal;

/**
 * Displays Log files per year/Month
 *
 * @author Nicolas de Pomereu
 */
public class SignatureFrame extends javax.swing.JFrame {

    public static boolean DEBUG = Debug.isSet(SignatureFrame.class);

    public static final String CR_LF = System.getProperty("line.separator");

    /**
     * Add a clipboard manager for content management
     */
    private ClipboardManager clipboard = null;
    private java.awt.Window parent = null;

    /**
     * the table clipboard manager for PDF Recipients
     */
    private TableClipboardManager tableClipboardManager2 = null;

    /**
     * So stupid! But needed for calls in threads
     */
    private SignatureFrame thisOne = this;
    private Connection connection;
    private int userNumber;
    private final String keyId;

    SignatureFrame(Frame frame, Connection connection, int userNumber, String keyId) {
	this.parent = frame;
	this.connection = connection;
	this.userNumber = userNumber;
	this.keyId = keyId;
	initComponents();
	initializeIt();
    }

    /**
     * This is the method to include in the constructor
     */
    public void initializeIt() {

	Dimension dim = new Dimension(498, 403);

	this.setSize(dim);
	this.setPreferredSize(dim);

	try {
	    this.setIconImage(ImageParmsUtil.getAppIcon());
	} catch (RuntimeException e1) {
	    e1.printStackTrace();
	}

	Toolkit.getDefaultToolkit().setDynamicLayout(true);

	// Add a Clipboard Manager
	clipboard = new ClipboardManager(this.getContentPane());

	jLabelTitle.setText(MessagesManager.get("signature"));
	jButtonOk.setText(MessagesManager.get("ok"));
	jButtonCancel.setText(MessagesManager.get("cancel"));

	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		loadData();
	    }
	});

	ButtonResizer br = new ButtonResizer(jPanelButtons);
	br.setWidthToMax();

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
		closeOnexit();
	    }
	});

//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                loadSignatureFile();
//            }
//        });
	this.keyListenerAdder();

	SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);

	this.jTextArea.moveCaretPosition(0);
	this.jTextArea.setSelectionEnd(0);

	// Load and activate previous windows settings
	// Defaults to upper left border
	WindowSettingManager.load(this);
	this.setTitle(jLabelTitle.getText());

	pack();

	this.setVisible(true);

    }

    public void loadData() {
	UserSettingsExtractor userSettingsExtractor = new UserSettingsExtractor(connection, userNumber);
	try {
	    UserSettingsLocal userSettingsLocal = userSettingsExtractor.get();
	    String signature = userSettingsLocal.getSignature();

	    System.out.println("signature: " + signature + ":");

	    if (signature == null || signature.equalsIgnoreCase("null") || signature.isEmpty()) {
		signature = " " + CR_LF + userSettingsLocal.getUserName() + CR_LF + keyId + CR_LF;
	    }

	    jTextArea.setText(signature);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    JOptionPaneNewCustom.showException(this, ex);
	}
    }

    public void saveSettings() {
	WindowSettingManager.save(this);
    }

    public void closeOnexit() {
	saveSettings();
	dispose();
    }

    private void actionOk() {

	this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	try {

	    String signature = HtmlConverter.toHtml(this.jTextArea.getText());

	    AwakeConnection awakeConnection = (AwakeConnection) connection;
	    AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

	    awakeFileSession.call("net.safester.server.UserSettingsUpdater.updateSignature", userNumber, signature,
		    connection);

	    TheUserSettingsHolder theUserSettingsHolder = new TheUserSettingsHolder(connection, userNumber);
	    theUserSettingsHolder.reset();

	} catch (Exception ex) {
	    ex.printStackTrace();
	    this.setCursor(Cursor.getDefaultCursor());
	    JOptionPaneNewCustom.showException(this, ex);
	} finally {
	    this.setCursor(Cursor.getDefaultCursor());
	}

	closeOnexit();
    }

    private void actionCancel() {
	closeOnexit();
    }

    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
	List<Component> components = SwingUtil.getAllComponants(this);

	for (int i = 0; i < components.size(); i++) {
	    Component comp = components.get(i);

	    comp.addKeyListener(new KeyAdapter() {
		public void keyReleased(KeyEvent e) {
		    keyReleased_actionPerformed(e);
		}
	    });
	}
    }

    private void keyReleased_actionPerformed(KeyEvent e) {
	// debug("this_keyReleased(KeyEvent e) " + e.getComponent().getName());

	int id = e.getID();
	if (id == KeyEvent.KEY_RELEASED) {
	    int keyCode = e.getKeyCode();

	    if (keyCode == KeyEvent.VK_ESCAPE) {
		this.dispose();
	    }
	}
    }

    /**
     * debug tool
     */
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

	jPanelNorth = new javax.swing.JPanel();
	jLabelTitle = new javax.swing.JLabel();
	jPanelSepLine2New = new javax.swing.JPanel();
	jPanel22 = new javax.swing.JPanel();
	jSeparator2 = new javax.swing.JSeparator();
	jPanel23 = new javax.swing.JPanel();
	jPanelBorderSep2 = new javax.swing.JPanel();
	jPanelSignatureMain = new javax.swing.JPanel();
	jPanelSignatureLeft = new javax.swing.JPanel();
	jPanelSignature = new javax.swing.JPanel();
	jScrollPane1 = new javax.swing.JScrollPane();
	jTextArea = new javax.swing.JTextArea();
	jPanelSignatureRight = new javax.swing.JPanel();
	jPanelSepBlanc3 = new javax.swing.JPanel();
	jPanelSepLine2New1 = new javax.swing.JPanel();
	jPanel24 = new javax.swing.JPanel();
	jSeparator3 = new javax.swing.JSeparator();
	jPanel25 = new javax.swing.JPanel();
	jPanelButtons = new javax.swing.JPanel();
	jButtonOk = new javax.swing.JButton();
	jButtonCancel = new javax.swing.JButton();
	jPanel1 = new javax.swing.JPanel();

	setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
	setTitle("Aide");
	getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

	jPanelNorth.setMaximumSize(new java.awt.Dimension(32767, 65));
	jPanelNorth.setMinimumSize(new java.awt.Dimension(217, 65));
	jPanelNorth.setPreferredSize(new java.awt.Dimension(217, 65));
	jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 12));

	jLabelTitle.setIcon(new javax.swing.ImageIcon(
		getClass().getResource("/net/safester/application/images/files_2/32x32/contract.png"))); // NOI18N
	jLabelTitle.setText("Créer ou modifier la signature");
	jLabelTitle.setToolTipText("");
	jPanelNorth.add(jLabelTitle);

	getContentPane().add(jPanelNorth);

	jPanelSepLine2New.setMaximumSize(new java.awt.Dimension(32787, 10));
	jPanelSepLine2New.setMinimumSize(new java.awt.Dimension(0, 10));
	jPanelSepLine2New.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New, javax.swing.BoxLayout.LINE_AXIS));

	jPanel22.setMaximumSize(new java.awt.Dimension(10, 10));

	javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
	jPanel22.setLayout(jPanel22Layout);
	jPanel22Layout.setHorizontalGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 10, Short.MAX_VALUE));
	jPanel22Layout.setVerticalGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 5, Short.MAX_VALUE));

	jPanelSepLine2New.add(jPanel22);
	jPanelSepLine2New.add(jSeparator2);

	jPanel23.setMaximumSize(new java.awt.Dimension(10, 10));

	javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
	jPanel23.setLayout(jPanel23Layout);
	jPanel23Layout.setHorizontalGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 10, Short.MAX_VALUE));
	jPanel23Layout.setVerticalGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 5, Short.MAX_VALUE));

	jPanelSepLine2New.add(jPanel23);

	getContentPane().add(jPanelSepLine2New);

	jPanelBorderSep2.setMaximumSize(new java.awt.Dimension(32767, 10));
	jPanelBorderSep2.setMinimumSize(new java.awt.Dimension(20, 10));
	jPanelBorderSep2.setPreferredSize(new java.awt.Dimension(20, 10));
	jPanelBorderSep2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
	getContentPane().add(jPanelBorderSep2);

	jPanelSignatureMain.setLayout(new javax.swing.BoxLayout(jPanelSignatureMain, javax.swing.BoxLayout.LINE_AXIS));

	jPanelSignatureLeft.setMaximumSize(new java.awt.Dimension(10, 10));
	jPanelSignatureMain.add(jPanelSignatureLeft);

	jPanelSignature.setLayout(new javax.swing.BoxLayout(jPanelSignature, javax.swing.BoxLayout.Y_AXIS));

	jTextArea.setColumns(20);
	jTextArea.setRows(5);
	jTextArea.setMargin(new java.awt.Insets(5, 5, 5, 5));
	jScrollPane1.setViewportView(jTextArea);

	jPanelSignature.add(jScrollPane1);

	jPanelSignatureMain.add(jPanelSignature);

	jPanelSignatureRight.setMaximumSize(new java.awt.Dimension(10, 10));
	jPanelSignatureMain.add(jPanelSignatureRight);

	getContentPane().add(jPanelSignatureMain);

	jPanelSepBlanc3.setMaximumSize(new java.awt.Dimension(32767, 10));
	jPanelSepBlanc3.setName(""); // NOI18N
	jPanelSepBlanc3.setPreferredSize(new java.awt.Dimension(1000, 10));
	getContentPane().add(jPanelSepBlanc3);

	jPanelSepLine2New1.setMaximumSize(new java.awt.Dimension(32787, 10));
	jPanelSepLine2New1.setMinimumSize(new java.awt.Dimension(0, 10));
	jPanelSepLine2New1.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New1, javax.swing.BoxLayout.LINE_AXIS));

	jPanel24.setMaximumSize(new java.awt.Dimension(10, 10));

	javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
	jPanel24.setLayout(jPanel24Layout);
	jPanel24Layout.setHorizontalGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 10, Short.MAX_VALUE));
	jPanel24Layout.setVerticalGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 5, Short.MAX_VALUE));

	jPanelSepLine2New1.add(jPanel24);
	jPanelSepLine2New1.add(jSeparator3);

	jPanel25.setMaximumSize(new java.awt.Dimension(10, 10));

	javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
	jPanel25.setLayout(jPanel25Layout);
	jPanel25Layout.setHorizontalGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 10, Short.MAX_VALUE));
	jPanel25Layout.setVerticalGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 5, Short.MAX_VALUE));

	jPanelSepLine2New1.add(jPanel25);

	getContentPane().add(jPanelSepLine2New1);

	jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 45));
	jPanelButtons.setPreferredSize(new java.awt.Dimension(518, 45));
	jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 5, 10));

	jButtonOk.setText("Valider");
	jButtonOk.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		jButtonOkActionPerformed(evt);
	    }
	});
	jPanelButtons.add(jButtonOk);

	jButtonCancel.setText("Fermer");
	jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		jButtonCancelActionPerformed(evt);
	    }
	});
	jPanelButtons.add(jButtonCancel);

	jPanel1.setMaximumSize(new java.awt.Dimension(1, 1));
	jPanel1.setMinimumSize(new java.awt.Dimension(1, 1));
	jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

	javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	jPanel1.setLayout(jPanel1Layout);
	jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 1, Short.MAX_VALUE));
	jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGap(0, 1, Short.MAX_VALUE));

	jPanelButtons.add(jPanel1);

	getContentPane().add(jPanelButtons);

	pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonOkActionPerformed
	actionOk();
    }// GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonCancelActionPerformed
	actionCancel();
    }// GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {

	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {

		// new SignatureFrame(null);
	    }
	});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanelBorderSep2;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSepBlanc3;
    private javax.swing.JPanel jPanelSepLine2New;
    private javax.swing.JPanel jPanelSepLine2New1;
    private javax.swing.JPanel jPanelSignature;
    private javax.swing.JPanel jPanelSignatureLeft;
    private javax.swing.JPanel jPanelSignatureMain;
    private javax.swing.JPanel jPanelSignatureRight;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextArea jTextArea;
    // End of variables declaration//GEN-END:variables

}
