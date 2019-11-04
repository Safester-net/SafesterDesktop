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
package net.safester.application.photo;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.Base64;
import org.awakefw.sql.api.client.AwakeConnection;

import com.swing.util.SwingUtil;

import net.iharder.dnd.FileDrop;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.JFileChooserFactory;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.UserPhotoLocal;
import net.safester.noobs.clientserver.GsonUtil;

/**
 *
 * @author Nicolas de Pomereu
 */
public class PhotoAdder extends javax.swing.JDialog {

    public static String USERID_1 = "ndepomereu@kawansoft.com";
    public static String USERID_2 = "abecquereau@kawansoft.com";
    public static String USERID_3 = "patrick@safester.net";

    public static String TEST_USERID = USERID_3;

    public static boolean DEBUG = false;
    public static boolean TEST = false;

    /**
     * Messages in national language
     */
    private MessagesManager messages = new MessagesManager();

    ClipboardManager clipboardManager;
    private final PhotoAdder thisOne;

    /**
     * The ImageResizer
     */
    private ImageResizer imageResizer = null;
    private Connection connection;

    // Email of owner
    private String keyId;

    /**
     * Creates new form ImagePreview
     */
    public PhotoAdder(java.awt.Frame parent, Connection theConnection, String keyId, boolean modal) {
	super(parent, modal);

	thisOne = this;
	this.connection = theConnection;
	this.keyId = keyId;

	initComponents();
	initialize();

    }

    /**
     * This is the method to include in *our* constructor
     */
    public void initialize() {
	clipboardManager = new ClipboardManager(rootPane);

	this.setSize(new Dimension(350, 350));

	try {
	    this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
	} catch (Exception e1) {
	    e1.printStackTrace();
	}

	this.setTitle(messages.getMessage("add_photo"));
	this.jLabelPicture.setText(this.getTitle());

	this.jLabelDragAndDrop.setText(messages.getMessage("drag_and_drop_or_select_photo"));

	jButtonSelect.setText(this.messages.getMessage("select_file"));
	jButtonOk.setText(messages.getMessage("ok"));
	jButtonCancel.setText(messages.getMessage("cancel"));

	jButtonOk.setEnabled(false);

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

	this.keyListenerAdder();

	SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);

	/**
	 * Handle a Drag & drop for files Will open automatically the file encryption or
	 * file decryption Window
	 */
	new FileDrop(this, new FileDrop.Listener() {
	    public void filesDropped(java.io.File[] files) {

		File file = files[0];

		String fileName = file.toString().toLowerCase();

		if (!new JpegFileFilter().accept(file)) {
		    JOptionPane.showMessageDialog(null, messages.getMessage("only_jpeg_images_are_supported"));
		    return;
		}

		getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		byte[] image;
		try {
		    image = ImageResizer.loadImage(file);
		} catch (Exception ex) {
		    getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    JOptionPaneNewCustom.showException(null, ex);
		    return;
		}

		imageResizer = new ImageResizer(image);
		addImagetoPanel();

		getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	    } // end filesDropped
	});

	this.setLocationByPlatform(true);
	WindowSettingManager.load(this);

	// Test if SQL displays back images
	if (TEST) {
	    displayBackTest();
	}

    }

    /**
     * Test method to check if we can display back a photo from the SQL database
     *
     * @throws SQLException
     */
    public void displayBackTest() {

	try {

	    AwakeConnection awakeConnection = (AwakeConnection) connection;
	    AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

	    String methodRemote = "net.safester.server.hosts.newapi.UserPhotoNewApi.get";
	    debug("methodRemote: " + methodRemote);

	    String jsonString = awakeFileSession.call(methodRemote, keyId, connection);

	    UserPhotoLocal userPhotoLocal = GsonUtil.userPhotoFromGson(jsonString);

	    if (userPhotoLocal.getUserEmail() == null) {
		return;
	    }

	    String photoBase64 = userPhotoLocal.getPhoto();
	    byte[] photo = Base64.base64ToByteArray(photoBase64);

	    imageResizer = new ImageResizer(photo);
	    addImagetoPanel();

	} catch (Exception ex) {
	    Logger.getLogger(PhotoAdder.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    private void actionOk() {

	try {
	    getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	    byte[] photo = imageResizer.getImage();
	    String photoBase64 = Base64.byteArrayToBase64(photo);

	    imageResizer.resizeToStickerSize();
	    byte[] thumbnail = imageResizer.getImage();

	    String thumbnailBase64 = Base64.byteArrayToBase64(thumbnail);

	    UserPhotoLocal userPhotoLocal = new UserPhotoLocal();
	    userPhotoLocal.setUserEmail(keyId);
	    userPhotoLocal.setPhoto(photoBase64);
	    userPhotoLocal.setThumbnail(thumbnailBase64);

	    String jsonString = GsonUtil.userPhotoToGson(userPhotoLocal);

	    debug("photoBase64.length()      : " + photoBase64.length());
	    debug("thumbnailBase64.length()  : " + thumbnailBase64.length());

	    AwakeConnection awakeConnection = (AwakeConnection) connection;
	    AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

	    String methodRemote = "net.safester.server.hosts.newapi.UserPhotoNewApi.put";
	    debug("methodRemote: " + methodRemote);

	    awakeFileSession.call(methodRemote, keyId, jsonString, connection);

	    getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    close();
	    JOptionPane.showMessageDialog(this, this.messages.getMessage("photo_added"));
	} catch (Exception e) {
	    getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    JOptionPaneNewCustom.showException(this, e, messages.getMessage("cannot_add_photo"));
	}
    }

    private void close() {
	WindowSettingManager.save(this);
	this.dispose();
	thisOne.dispose();
    }

    public void saveSettings() {
	WindowSettingManager.save(this);
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

	    if (keyCode == KeyEvent.VK_ENTER) {
		actionOk();
	    }

	    if (keyCode == KeyEvent.VK_ESCAPE) {
		close();
	    }

	    if (keyCode == KeyEvent.VK_F1) {
		// jButtonHelpActionPerformed(null);
	    }
	}
    }

    /**
     * Resize and add the image to the JPanel jPanelPhoto
     */
    private void addImagetoPanel() {

	// Reduce the image size if necessary
	imageResizer.resizeToMaximumAllowedSize();

	int widthPanelPhoto = jPanelPhotoMain.getPreferredSize().width;
	int heightPanelPhoto = jPanelPhotoMain.getPreferredSize().height;

	Point middlePoint = new Point((widthPanelPhoto / 2) - (imageResizer.getBufferedImage().getWidth() / 2),
		(heightPanelPhoto / 2) - (imageResizer.getBufferedImage().getHeight() / 2));

	JImagePanel imagePanel = new JImagePanel(imageResizer.getBufferedImage(),
		// (int)middlePoint.getX(), (int)middlePoint.getY());
		(int) middlePoint.getX(), 0);

	jPanelPhoto.removeAll();
	jPanelPhoto.add(imagePanel);

	jButtonOk.setEnabled(true);
	paintAll(getGraphics());

    }

    /**
     * debug tool
     */
    private void debug(String s) {
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

	jPanelUp = new javax.swing.JPanel();
	jLabelPicture = new javax.swing.JLabel();
	jPanelDragDrop = new javax.swing.JPanel();
	jLabelDragAndDrop = new javax.swing.JLabel();
	jPanelPhotoMain = new javax.swing.JPanel();
	jPanelLeft = new javax.swing.JPanel();
	jPanelPhoto = new javax.swing.JPanel();
	jPanelDefaultPhoto = new javax.swing.JPanel();
	jPanelButtonSelect = new javax.swing.JPanel();
	jButtonSelect = new javax.swing.JButton();
	jPanelRight = new javax.swing.JPanel();
	jPanel14 = new javax.swing.JPanel();
	jPanelSepBottom = new javax.swing.JPanel();
	jPanelButtons = new javax.swing.JPanel();
	jButtonOk = new javax.swing.JButton();
	jButtonCancel = new javax.swing.JButton();
	jPanel4 = new javax.swing.JPanel();

	setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

	jPanelUp.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

	jLabelPicture.setIcon(new javax.swing.ImageIcon(
		getClass().getResource("/net/safester/application/images/files_2/32x32/photo_portrait.png"))); // NOI18N
	jLabelPicture.setText("jLabelPicture");
	jPanelUp.add(jLabelPicture);

	getContentPane().add(jPanelUp);

	jPanelDragDrop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 9));

	jLabelDragAndDrop.setText("jLabelDragAndDrop");
	jPanelDragDrop.add(jLabelDragAndDrop);

	getContentPane().add(jPanelDragDrop);

	jPanelPhotoMain.setLayout(new javax.swing.BoxLayout(jPanelPhotoMain, javax.swing.BoxLayout.LINE_AXIS));

	jPanelLeft.setMaximumSize(new java.awt.Dimension(12, 12));
	jPanelLeft.setMinimumSize(new java.awt.Dimension(12, 12));
	jPanelLeft.setPreferredSize(new java.awt.Dimension(12, 12));
	jPanelLeft.setLayout(new javax.swing.BoxLayout(jPanelLeft, javax.swing.BoxLayout.LINE_AXIS));
	jPanelPhotoMain.add(jPanelLeft);

	jPanelPhoto.setLayout(new java.awt.BorderLayout());

	jPanelDefaultPhoto.setBackground(new java.awt.Color(255, 255, 255));
	jPanelDefaultPhoto.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
	jPanelPhoto.add(jPanelDefaultPhoto, java.awt.BorderLayout.CENTER);

	jPanelPhotoMain.add(jPanelPhoto);

	jPanelButtonSelect.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

	jButtonSelect.setText("jButtonSelect");
	jButtonSelect.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		jButtonSelectActionPerformed(evt);
	    }
	});
	jPanelButtonSelect.add(jButtonSelect);

	jPanelPhotoMain.add(jPanelButtonSelect);

	jPanelRight.setMaximumSize(new java.awt.Dimension(12, 12));
	jPanelRight.setMinimumSize(new java.awt.Dimension(12, 12));
	jPanelRight.setPreferredSize(new java.awt.Dimension(12, 12));
	jPanelRight.setRequestFocusEnabled(false);
	jPanelRight.setLayout(new javax.swing.BoxLayout(jPanelRight, javax.swing.BoxLayout.LINE_AXIS));
	jPanelPhotoMain.add(jPanelRight);

	getContentPane().add(jPanelPhotoMain);

	jPanel14.setMaximumSize(new java.awt.Dimension(10, 10));
	jPanel14.setLayout(new javax.swing.BoxLayout(jPanel14, javax.swing.BoxLayout.LINE_AXIS));
	getContentPane().add(jPanel14);

	jPanelSepBottom.setMaximumSize(new java.awt.Dimension(32767, 8));
	jPanelSepBottom.setMinimumSize(new java.awt.Dimension(10, 8));
	jPanelSepBottom.setPreferredSize(new java.awt.Dimension(10, 8));
	getContentPane().add(jPanelSepBottom);

	jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 43));
	jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

	jButtonOk.setText("jButtonOk");
	jButtonOk.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		jButtonOkActionPerformed(evt);
	    }
	});
	jPanelButtons.add(jButtonOk);

	jButtonCancel.setText("jButtonCancel");
	jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		jButtonCancelActionPerformed(evt);
	    }
	});
	jPanelButtons.add(jButtonCancel);

	jPanel4.setPreferredSize(new java.awt.Dimension(1, 10));
	jPanelButtons.add(jPanel4);

	getContentPane().add(jPanelButtons);

	pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonOkActionPerformed
	actionOk();
    }// GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonCancelActionPerformed
	close();
    }// GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonSelectActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSelectActionPerformed
	try {
	    JFileChooser jFileChooser = JFileChooserFactory.getInstance();

	    jFileChooser.setDialogTitle(this.messages.getMessage("select_a_jpgeg_file"));
	    jFileChooser.setMultiSelectionEnabled(false);
	    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

	    jFileChooser.addChoosableFileFilter(new JpegFileFilter());
	    jFileChooser.setAcceptAllFileFilterUsed(false);

	    int returnVal = jFileChooser.showOpenDialog(this);

	    if (returnVal != JFileChooser.APPROVE_OPTION) {
		return;
	    }

	    // Add the Photo
	    File imageFile = jFileChooser.getSelectedFile();

	    // Security check: no null fiels & no empty files
	    if (imageFile == null || imageFile.length() == 0) {
		return;
	    }

	    getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    byte[] image = ImageResizer.loadImage(imageFile);

	    // Reduce the image size if necessary
	    imageResizer = new ImageResizer(image);
	    addImagetoPanel();

	    getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	} catch (Exception ex) {
	    getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    JOptionPaneNewCustom.showException(this, ex);
	    return;
	}
    }// GEN-LAST:event_jButtonSelectActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
	/* Set the Nimbus look and feel */
	// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
	// (optional) ">
	/*
	 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
	 * look and feel. For details see
	 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
	 */
	try {
	    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
		if ("Nimbus".equals(info.getName())) {
		    javax.swing.UIManager.setLookAndFeel(info.getClassName());
		    break;
		}
	    }
	} catch (ClassNotFoundException ex) {
	    java.util.logging.Logger.getLogger(PhotoAdder.class.getName()).log(java.util.logging.Level.SEVERE, null,
		    ex);
	} catch (InstantiationException ex) {
	    java.util.logging.Logger.getLogger(PhotoAdder.class.getName()).log(java.util.logging.Level.SEVERE, null,
		    ex);
	} catch (IllegalAccessException ex) {
	    java.util.logging.Logger.getLogger(PhotoAdder.class.getName()).log(java.util.logging.Level.SEVERE, null,
		    ex);
	} catch (javax.swing.UnsupportedLookAndFeelException ex) {
	    java.util.logging.Logger.getLogger(PhotoAdder.class.getName()).log(java.util.logging.Level.SEVERE, null,
		    ex);
	}
	// </editor-fold>

	JFrame.setDefaultLookAndFeelDecorated(true);
	JDialog.setDefaultLookAndFeelDecorated(true);
	try {
	    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	} catch (Exception ex) {
	    System.out.println("Failed loading L&F: ");
	    System.out.println(ex);
	}

	/* Create and display the dialog */
	java.awt.EventQueue.invokeLater(new Runnable() {

	    public void run() {
		try {
		    Connection connection = null; // LocalConnection.get();

		    if (connection == null) {
			throw new SQLException("Connection is null!");
		    }

		    PhotoAdder dialog = new PhotoAdder(new javax.swing.JFrame(), connection, TEST_USERID, true);

		    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
			    System.exit(0);
			}
		    });
		    dialog.setVisible(true);
		} catch (SQLException ex) {
		    Logger.getLogger(PhotoAdder.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonSelect;
    private javax.swing.JLabel jLabelDragAndDrop;
    private javax.swing.JLabel jLabelPicture;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelButtonSelect;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelDefaultPhoto;
    private javax.swing.JPanel jPanelDragDrop;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelPhoto;
    private javax.swing.JPanel jPanelPhotoMain;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSepBottom;
    private javax.swing.JPanel jPanelUp;
    // End of variables declaration//GEN-END:variables
}
