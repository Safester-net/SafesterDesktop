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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.awakefw.sql.api.client.AwakeConnection;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.crypto.CryptoUtil;
import net.safester.clientserver.AutoresponderExtractor;
import net.safester.clientserver.AutoresponderLocal2;
import net.safester.clientserver.PgpKeyPairLocal;
import net.safester.clientserver.ServerParms;
import net.safester.clientserver.holder.PgpKeyPairHolder;

/**
 *
 * @author Nicolas de Pomereu
 */
public class AutoResponder extends javax.swing.JDialog {

    /** The parent Window */
    private ClipboardManager clipboardManager;
    private MessagesManager messages = new MessagesManager();

    private List<String> pubKeyBloc = new ArrayList<String>();

    private JFrame parent;

    private Connection connection;
    private int userNumber;
    private String keyId;
    private char[] passphrase;

    private Window thisOne;

    /** Creates new form FrameProxyParms */
    public AutoResponder(JFrame parent, Connection theConnection, int userNumber, String keyId, char[] passphrase) {

	this.parent = parent;

	// Use a dedicated Connection to avoid overlap of result files
	this.connection = ((AwakeConnection) theConnection).clone();

	this.userNumber = userNumber;
	this.keyId = keyId;
	this.passphrase = passphrase;

	thisOne = this;
	initComponents();
	initializeCompany();

	this.setLocationRelativeTo(parent);
	this.setVisible(true);

    }

    /**
     * This is the method to include in *our* constructor
     */
    public void initializeCompany() {
	clipboardManager = new ClipboardManager(rootPane);

	Dimension dim = new Dimension(495, 495);
	this.setSize(dim);
	this.setPreferredSize(dim);

	buttonGroup1.add(jRadioButtonResponderOff);
	buttonGroup1.add(jRadioButtonResponderOn);

	try {
	    this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
	} catch (Exception e1) {
	    e1.printStackTrace();
	}

	this.setTitle(messages.getMessage("vacation_responder"));
	jLabelLogoTitle.setText(this.getTitle());

	jRadioButtonResponderOff.setSelected(true);

	jRadioButtonResponderOff.setText(messages.getMessage("vacation_responder_off"));
	jRadioButtonResponderOn.setText(messages.getMessage("vacation_responder_on"));

	jLabelDateFrom.setText(messages.getMessage("from"));
	jLabelDateUntil.setText(messages.getMessage("until"));
	jLabelSubject.setText(messages.getMessage("subject"));
	jLabelMessage.setText(messages.getMessage("message"));

	jTextAreaBody.setFont(jTextFieldSubject.getFont());

	jButtonApply.setText(messages.getMessage("ok"));
	jButtonClose.setText(messages.getMessage("cancel"));

	// Set the Send preferences for user Preferences

	jRadioButtonResponderOnItemStateChanged(null);

	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		setStoredPreferences();
		jRadioButtonResponderOnItemStateChanged(null);
	    }
	});

	// jRadioButtonUseProxyItemStateChanged(null);

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
		WindowSettingManager.save(thisOne);
		close();
	    }
	});

	this.keyListenerAdder();

	SwingUtil.resizeJComponentsForAll(rootPane);

	this.setLocationByPlatform(true);
	this.setLocationRelativeTo(parent);
	WindowSettingManager.load(this);

	this.jTextAreaBody.setCaretPosition(0);

    }

    /**
     * Set the Send Preferences found in User Preferences (Registry)
     */
    private void setStoredPreferences() {
	try {
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	    AutoresponderExtractor autoresponderExtractor = new AutoresponderExtractor(connection, userNumber);
	    AutoresponderLocal2 autoresponderLocal2 = autoresponderExtractor.get();

//	    PgpKeyPairTransfer pgpKeyPairTransfer = new PgpKeyPairTransfer(connection,
//		    ServerParms.getMasterKeyUserNumber());
//	    PgpKeyPairLocal pgpMasterKeyPairLocal = pgpKeyPairTransfer.get();
//	    pgpKeyPairTransfer = new PgpKeyPairTransfer(connection, userNumber);
//	    PgpKeyPairLocal pgpKeyPairForUser = pgpKeyPairTransfer.get();
	    
	    PgpKeyPairHolder pgpKeyPairHolder = new PgpKeyPairHolder(connection, ServerParms.getMasterKeyUserNumber());
	    PgpKeyPairLocal pgpMasterKeyPairLocal = pgpKeyPairHolder.get();
	    
	    pgpKeyPairHolder = new PgpKeyPairHolder(connection, userNumber);
	    PgpKeyPairLocal pgpKeyPairForUser = pgpKeyPairHolder.get();
	       
	    // Build the public key block for list for user + master key
	    pubKeyBloc = new ArrayList<String>();
	    pubKeyBloc.add(pgpMasterKeyPairLocal.getPublicKeyPgpBlock());
	    pubKeyBloc.add(pgpKeyPairForUser.getPublicKeyPgpBlock());

	    if (autoresponderLocal2.getDtBegin() < 0 && autoresponderLocal2.getDtExpire() < 0 ) {
		this.jRadioButtonResponderOff.setSelected(true);
		jXDatePickerBegin.setDate(new java.util.Date());
		jXDatePickerEnd.setDate(new java.util.Date());
		this.setCursor(Cursor.getDefaultCursor());
		return;
	    }

	    this.jRadioButtonResponderOn.setSelected(autoresponderLocal2.getResponderOn());
	    this.jXDatePickerBegin.setDate(new Date(autoresponderLocal2.getDtBegin()));
	    this.jXDatePickerEnd.setDate(new Date(autoresponderLocal2.getDtExpire()));

	    String encryptedSubject = autoresponderLocal2.getSubject();
	    String encryptedBody = autoresponderLocal2.getBody();

	    String subject = CryptoUtil.decrypt(encryptedSubject, pgpKeyPairForUser.getPrivateKeyPgpBlock(),
		    passphrase);
	    String body = CryptoUtil.decrypt(encryptedBody, pgpKeyPairForUser.getPrivateKeyPgpBlock(), passphrase);

	    this.jTextFieldSubject.setText(subject);
	    this.jTextAreaBody.setText(body);

	} catch (Exception exception) {
	    this.setCursor(Cursor.getDefaultCursor());
	    exception.printStackTrace();
	    JOptionPaneNewCustom.showException(this, exception);
	}

	this.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Done if OK Button hit
     */
    private void actionOk() {
	if (jTextAreaBody.getText().isEmpty() && jRadioButtonResponderOn.isSelected()) {
	    String errorMsg = messages.getMessage("error");
	    JOptionPane.showMessageDialog(this, messages.getMessage("vacation_responded_enabled_no_response"), errorMsg,
		    JOptionPane.ERROR_MESSAGE);
	    return;
	}

	if (jXDatePickerBegin.getDate() == null) {
	    String errorMsg = messages.getMessage("error");
	    JOptionPane.showMessageDialog(this, messages.getMessage("please_select_a_from_date"), errorMsg,
		    JOptionPane.ERROR_MESSAGE);
	    return;
	}

	if (jXDatePickerEnd.getDate() == null) {
	    String errorMsg = messages.getMessage("error");
	    JOptionPane.showMessageDialog(this, messages.getMessage("please_select_an_until_date"), errorMsg,
		    JOptionPane.ERROR_MESSAGE);
	    return;
	}

	if (jXDatePickerBegin.getDate().after(jXDatePickerEnd.getDate())) {
	    String errorMsg = messages.getMessage("error");
	    JOptionPane.showMessageDialog(this, messages.getMessage("from_date_must_be_before_until"), errorMsg,
		    JOptionPane.ERROR_MESSAGE);
	    return;
	}

	this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	try {
	    AutoresponderExtractor autoresponderExtractor = new AutoresponderExtractor(connection, userNumber);

	    AutoresponderLocal2 autoresponderLocal2 = new AutoresponderLocal2();
	    autoresponderLocal2.setUserNumber(userNumber);
	    autoresponderLocal2.setResponderOn(jRadioButtonResponderOn.isSelected());
	    autoresponderLocal2.setDtBegin(jXDatePickerBegin.getDate().getTime());
	    autoresponderLocal2.setDtExpire(jXDatePickerEnd.getDate().getTime());

	    String subject = jTextFieldSubject.getText();
	    String body = jTextAreaBody.getText();

	    String encryptedSubject = CryptoUtil.encrypt(subject, pubKeyBloc);
	    String encryptedBody = CryptoUtil.encrypt(body, pubKeyBloc);

	    autoresponderLocal2.setSubject(encryptedSubject);
	    autoresponderLocal2.setBody(encryptedBody);

	    // Do the delete + insert
	    autoresponderExtractor.update(autoresponderLocal2);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    this.setCursor(Cursor.getDefaultCursor());
	    JOptionPaneNewCustom.showException(this, ex);
	}

	this.setCursor(Cursor.getDefaultCursor());
	close();
    }

    private void close() {
	WindowSettingManager.save(this);
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
	// System.out.println("this_keyReleased(KeyEvent e) " +
	// e.getComponent().getName());
	int id = e.getID();
	if (id == KeyEvent.KEY_RELEASED) {
	    int keyCode = e.getKeyCode();

	    if (keyCode == KeyEvent.VK_ESCAPE) {
		actionCancel();
	    }

	    if (keyCode == KeyEvent.VK_F1) {
		// jButtonHelpActionPerformed(null);
	    }
	}
    }

    /**
     * Done if Cancel Button hit
     */
    private void actionCancel() {
	close();
    }

    public void saveSettings() {
	WindowSettingManager.save(this);
    }

    private void enableZones(boolean enable) {
	this.jLabelDateFrom.setEnabled(enable);
	this.jXDatePickerBegin.setEnabled(enable);
	this.jLabelDateUntil.setEnabled(enable);
	this.jXDatePickerEnd.setEnabled(enable);
	this.jLabelSubject.setEnabled(enable);
	this.jTextFieldSubject.setEnabled(enable);
	this.jLabelMessage.setEnabled(enable);
	this.jTextAreaBody.setEnabled(enable);

	// System.out.println("jTextFieldSubject.getBackground(): " +
	// jTextFieldSubject.getBackground());
	Color color = jTextFieldSubject.getBackground();
	this.jTextAreaBody.setBackground(color);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanelUp = new javax.swing.JPanel();
        jPanelBorderTop = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabelLogoTitle = new javax.swing.JLabel();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelSepLine1 = new javax.swing.JPanel();
        jPanelSep4 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelSep5 = new javax.swing.JPanel();
        jPanelMain = new javax.swing.JPanel();
        jPanelLetf2 = new javax.swing.JPanel();
        jPaneMain = new javax.swing.JPanel();
        jPanelResponderOffOn = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jRadioButtonResponderOff = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jRadioButtonResponderOn = new javax.swing.JRadioButton();
        jPanelResponderDetails = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();
        jPanelResponderMain = new javax.swing.JPanel();
        jPanelDates = new javax.swing.JPanel();
        jLabelDateFrom = new javax.swing.JLabel();
        jXDatePickerBegin = new org.jdesktop.swingx.JXDatePicker();
        jPanel1 = new javax.swing.JPanel();
        jLabelDateUntil = new javax.swing.JLabel();
        jXDatePickerEnd = new org.jdesktop.swingx.JXDatePicker();
        jPanelSubjectContainer = new javax.swing.JPanel();
        jPanelLabelSubject = new javax.swing.JPanel();
        jLabelSubject = new javax.swing.JLabel();
        jPanelSep5b = new javax.swing.JPanel();
        jPanelSubject = new javax.swing.JPanel();
        jTextFieldSubject = new javax.swing.JTextField();
        jPanelBody = new javax.swing.JPanel();
        jPanelLabelMessage = new javax.swing.JPanel();
        jLabelMessage = new javax.swing.JLabel();
        jPanelSep5b1 = new javax.swing.JPanel();
        jScrollPaneBody = new javax.swing.JScrollPane();
        jTextAreaBody = new javax.swing.JTextArea();
        jPanelRight2 = new javax.swing.JPanel();
        jPanelSep3 = new javax.swing.JPanel();
        jPanelSepLine = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSep = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonApply = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(440, 440));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelUp.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanelUp);

        jPanelBorderTop.setLayout(new javax.swing.BoxLayout(jPanelBorderTop, javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setMaximumSize(new java.awt.Dimension(32767, 38));
        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 3));

        jLabelLogoTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelLogoTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/telephone.png"))); // NOI18N
        jLabelLogoTitle.setText("Vacation Responder");
        jPanel12.add(jLabelLogoTitle);

        jPanel6.add(jPanel12);

        jPanelBorderTop.add(jPanel6);

        getContentPane().add(jPanelBorderTop);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(1000, 10));
        getContentPane().add(jPanelSep2);

        jPanelSepLine1.setLayout(new javax.swing.BoxLayout(jPanelSepLine1, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep4.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepLine1.add(jPanelSep4);

        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.add(jSeparator3);

        jPanelSep5.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepLine1.add(jPanelSep5);

        getContentPane().add(jPanelSepLine1);

        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLetf2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelMain.add(jPanelLetf2);

        jPaneMain.setLayout(new javax.swing.BoxLayout(jPaneMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelResponderOffOn.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelResponderOffOn.setLayout(new javax.swing.BoxLayout(jPanelResponderOffOn, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jRadioButtonResponderOff.setText("Vacation responder off");
        jPanel2.add(jRadioButtonResponderOff);

        jPanelResponderOffOn.add(jPanel2);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jRadioButtonResponderOn.setText("Vacation responder on");
        jRadioButtonResponderOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonResponderOnItemStateChanged(evt);
            }
        });
        jPanel3.add(jRadioButtonResponderOn);

        jPanelResponderOffOn.add(jPanel3);

        jPaneMain.add(jPanelResponderOffOn);

        jPanelResponderDetails.setLayout(new javax.swing.BoxLayout(jPanelResponderDetails, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft.setMaximumSize(new java.awt.Dimension(20, 10));
        jPanelLeft.setMinimumSize(new java.awt.Dimension(20, 10));
        jPanelLeft.setPreferredSize(new java.awt.Dimension(20, 10));
        jPanelLeft.setLayout(new javax.swing.BoxLayout(jPanelLeft, javax.swing.BoxLayout.LINE_AXIS));
        jPanelResponderDetails.add(jPanelLeft);

        jPanelResponderMain.setLayout(new javax.swing.BoxLayout(jPanelResponderMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelDates.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelDates.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelDateFrom.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelDateFrom.setText("From");
        jLabelDateFrom.setMaximumSize(new java.awt.Dimension(50, 16));
        jLabelDateFrom.setMinimumSize(new java.awt.Dimension(50, 16));
        jLabelDateFrom.setPreferredSize(new java.awt.Dimension(50, 16));
        jPanelDates.add(jLabelDateFrom);
        jPanelDates.add(jXDatePickerBegin);
        jPanelDates.add(jPanel1);

        jLabelDateUntil.setText("Until");
        jPanelDates.add(jLabelDateUntil);
        jPanelDates.add(jXDatePickerEnd);

        jPanelResponderMain.add(jPanelDates);

        jPanelSubjectContainer.setMaximumSize(new java.awt.Dimension(2147483647, 32));
        jPanelSubjectContainer.setMinimumSize(new java.awt.Dimension(73, 32));
        jPanelSubjectContainer.setPreferredSize(new java.awt.Dimension(126, 32));
        jPanelSubjectContainer.setLayout(new javax.swing.BoxLayout(jPanelSubjectContainer, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLabelSubject.setMaximumSize(new java.awt.Dimension(55, 24));
        jPanelLabelSubject.setMinimumSize(new java.awt.Dimension(55, 24));
        jPanelLabelSubject.setPreferredSize(new java.awt.Dimension(55, 24));
        jPanelLabelSubject.setLayout(new javax.swing.BoxLayout(jPanelLabelSubject, javax.swing.BoxLayout.LINE_AXIS));

        jLabelSubject.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelSubject.setText("Subject");
        jLabelSubject.setMaximumSize(new java.awt.Dimension(55, 16));
        jLabelSubject.setMinimumSize(new java.awt.Dimension(55, 16));
        jLabelSubject.setPreferredSize(new java.awt.Dimension(55, 16));
        jPanelLabelSubject.add(jLabelSubject);

        jPanelSubjectContainer.add(jPanelLabelSubject);

        jPanelSep5b.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanelSep5b.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSep5b.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelSubjectContainer.add(jPanelSep5b);

        jPanelSubject.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPanelSubject.setMinimumSize(new java.awt.Dimension(6, 31));
        jPanelSubject.setPreferredSize(new java.awt.Dimension(59, 31));
        jPanelSubject.setLayout(new javax.swing.BoxLayout(jPanelSubject, javax.swing.BoxLayout.LINE_AXIS));

        jTextFieldSubject.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSubjectActionPerformed(evt);
            }
        });
        jPanelSubject.add(jTextFieldSubject);

        jPanelSubjectContainer.add(jPanelSubject);

        jPanelResponderMain.add(jPanelSubjectContainer);

        jPanelBody.setLayout(new javax.swing.BoxLayout(jPanelBody, javax.swing.BoxLayout.X_AXIS));

        jPanelLabelMessage.setMaximumSize(new java.awt.Dimension(55, 24));
        jPanelLabelMessage.setMinimumSize(new java.awt.Dimension(55, 24));
        jPanelLabelMessage.setPreferredSize(new java.awt.Dimension(55, 24));
        jPanelLabelMessage.setLayout(new javax.swing.BoxLayout(jPanelLabelMessage, javax.swing.BoxLayout.LINE_AXIS));

        jLabelMessage.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelMessage.setText("Message");
        jLabelMessage.setMaximumSize(new java.awt.Dimension(55, 16));
        jLabelMessage.setMinimumSize(new java.awt.Dimension(55, 16));
        jLabelMessage.setPreferredSize(new java.awt.Dimension(55, 16));
        jPanelLabelMessage.add(jLabelMessage);

        jPanelBody.add(jPanelLabelMessage);

        jPanelSep5b1.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanelSep5b1.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSep5b1.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelBody.add(jPanelSep5b1);

        jScrollPaneBody.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPaneBody.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextAreaBody.setLineWrap(true);
        jTextAreaBody.setWrapStyleWord(true);
        jScrollPaneBody.setViewportView(jTextAreaBody);

        jPanelBody.add(jScrollPaneBody);

        jPanelResponderMain.add(jPanelBody);

        jPanelResponderDetails.add(jPanelResponderMain);

        jPaneMain.add(jPanelResponderDetails);

        jPanelMain.add(jPaneMain);

        jPanelRight2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelMain.add(jPanelRight2);

        getContentPane().add(jPanelMain);

        jPanelSep3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSep3.setPreferredSize(new java.awt.Dimension(1000, 10));
        getContentPane().add(jPanelSep3);

        jPanelSepLine.setLayout(new javax.swing.BoxLayout(jPanelSepLine, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepLine.add(jPanelSep1);

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine.add(jSeparator2);

        jPanelSep.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepLine.add(jPanelSep);

        getContentPane().add(jPanelSepLine);

        jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonApply.setText("OK");
        jButtonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApplyActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonApply);

        jButtonClose.setText("Cancel");
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

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonCloseActionPerformed
	actionCancel();
    }// GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonApplyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonApplyActionPerformed
	actionOk();
    }// GEN-LAST:event_jButtonApplyActionPerformed

    private void jRadioButtonResponderOnItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_jRadioButtonResponderOnItemStateChanged
	if (jRadioButtonResponderOn.isSelected()) {
	    enableZones(true);
	    jTextFieldSubject.requestFocusInWindow();
	} else {
	    enableZones(false);
	}
    }// GEN-LAST:event_jRadioButtonResponderOnItemStateChanged

    private void jTextFieldSubjectActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextFieldSubjectActionPerformed
	// TODO add your handling code here:
    }// GEN-LAST:event_jTextFieldSubjectActionPerformed

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
		} catch (Exception ex) {
		    System.out.println("Failed loading L&F: ");
		    System.out.println(ex);
		}
	    }
	});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonApply;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JLabel jLabelDateFrom;
    private javax.swing.JLabel jLabelDateUntil;
    private javax.swing.JLabel jLabelLogoTitle;
    private javax.swing.JLabel jLabelMessage;
    private javax.swing.JLabel jLabelSubject;
    private javax.swing.JPanel jPaneMain;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanelBody;
    private javax.swing.JPanel jPanelBorderTop;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelDates;
    private javax.swing.JPanel jPanelLabelMessage;
    private javax.swing.JPanel jPanelLabelSubject;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelLetf2;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelResponderDetails;
    private javax.swing.JPanel jPanelResponderMain;
    private javax.swing.JPanel jPanelResponderOffOn;
    private javax.swing.JPanel jPanelRight2;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSep4;
    private javax.swing.JPanel jPanelSep5;
    private javax.swing.JPanel jPanelSep5b;
    private javax.swing.JPanel jPanelSep5b1;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSepLine1;
    private javax.swing.JPanel jPanelSubject;
    private javax.swing.JPanel jPanelSubjectContainer;
    private javax.swing.JPanel jPanelUp;
    private javax.swing.JRadioButton jRadioButtonResponderOff;
    private javax.swing.JRadioButton jRadioButtonResponderOn;
    private javax.swing.JScrollPane jScrollPaneBody;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextArea jTextAreaBody;
    private javax.swing.JTextField jTextFieldSubject;
    private org.jdesktop.swingx.JXDatePicker jXDatePickerBegin;
    private org.jdesktop.swingx.JXDatePicker jXDatePickerEnd;
    // End of variables declaration//GEN-END:variables

}
