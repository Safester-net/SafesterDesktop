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
package net.safester.application.util;


/****************************************************************/ 
/*                      JDialogDiscardableMessage                             */
/*                                                              */ 
/****************************************************************/ 
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ClipboardManager;

import com.safelogic.pgp.api.util.crypto.Sha1;
import com.swing.util.SwingUtil;
import java.awt.Color;
/** 
 * Summary description for JDialogDiscardableMessage
 * 
 */ 
public class JDialogDiscardableMessage extends JDialog
{ 
    // Variables declaration 
    private JPanel contentPane; 
    //----- 
    private JPanel jPanel1; 
    //----- 
    private JPanel jPanel3; 
    //----- 
    private JLabel jLabelLogo; 
    private JPanel jPanel5; 
    //----- 
    private JTextArea jTextAreaMessage; 
    private JPanel jPanel7; 
    //----- 
    private JPanel jPanel18; 
    //----- 
    private JSeparator jSeparator3; 
    private JPanel jPanel19; 
    //----- 
    private JPanel jPanel20; 
    //----- 
    private JPanel jPanel21; 
    //----- 
    private JPanel jPanel22; 
    //----- 
    private JCheckBox jCheckBoxDiscardMessage; 
    private JPanel jPanel23; 
    //----- 
    private JButton jButtonOk; 
    private JPanel jPanel24; 
    //----- 
    private JPanel jPanel15; 
    //----- 
    // End of variables declaration 
 
 
    public JDialogDiscardableMessage(Frame w)
    { 
        super(w); 
        initializeComponent(); 
        // 
        // TODO: Add any constructor code after initializeComponent call 
        // 
 
        this.setVisible(true); 
    } 
 
    /** 
     * This method is called from within the constructor to initialize the form. 
     * WARNING: Do NOT modify this code. The content of this method is always regenerated 
     * by the Windows Form Designer. Otherwise, retrieving design might not work properly. 
     * Tip: If you must revise this method, please backup this GUI file for JFrameBuilder 
     * to retrieve your design properly in future, before revising this method. 
     */ 
    private void initializeComponent() 
    { 
        contentPane = (JPanel)this.getContentPane(); 
        //----- 
        jPanel1 = new JPanel(); 
        //----- 
        jPanel3 = new JPanel(); 
        //----- 
        jLabelLogo = new JLabel(); 
        jPanel5 = new JPanel(); 
        //----- 
        jTextAreaMessage = new JTextArea(); 
        jPanel7 = new JPanel(); 
        //----- 
        jPanel18 = new JPanel(); 
        //----- 
        jSeparator3 = new JSeparator(); 
        jPanel19 = new JPanel(); 
        //----- 
        jPanel20 = new JPanel(); 
        //----- 
        jPanel21 = new JPanel(); 
        //----- 
        jPanel22 = new JPanel(); 
        //----- 
        jCheckBoxDiscardMessage = new JCheckBox(); 
        jPanel23 = new JPanel(); 
        //----- 
        jButtonOk = new JButton(); 
        jPanel24 = new JPanel(); 
        //----- 
        jPanel15 = new JPanel(); 
        //----- 
 
        // 
        // contentPane 
        // 
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS)); 
        contentPane.add(jPanel1, 0); 
        contentPane.add(jPanel3, 1); 
        contentPane.add(jPanel18, 2); 
        // 
        // jPanel1 
        // 
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.X_AXIS)); 
        jPanel1.add(jPanel5, 0); 
        jPanel1.add(jPanel7, 1); 
        // 
        // jPanel3 
        // 
        jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.Y_AXIS)); 
        jPanel3.add(jPanel19, 0); 
        jPanel3.add(jPanel22, 1); 
        jPanel3.setMaximumSize(new Dimension(32767, 45)); 
        // 
        // jLabelLogo 
        // 
        jLabelLogo.setIcon(new ImageIcon("I:\\_dev_pgeep\\java\\java.src\\com\\pgeep\\application\\images\\about-32x32.png")); 
        // 
        // jPanel5 
        // 
        jPanel5.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        jPanel5.add(jLabelLogo, 0); 
        jPanel5.setMaximumSize(new Dimension(52, 32767)); 
        // 
        // jTextAreaMessage 
        // 
        jTextAreaMessage.setMargin(new Insets(0, 5, 0, 5)); 
        jTextAreaMessage.setText("jTextArea"); 
        // 
        // jPanel7 
        // 
        jPanel7.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10)); 
        jPanel7.add(jTextAreaMessage, 0); 
        jPanel7.add(jPanel15, 1); 
        // 
        // jPanel18 
        // 
        jPanel18.setLayout(new BoxLayout(jPanel18, BoxLayout.X_AXIS)); 
        // 
        // jSeparator3 
        // 
        jSeparator3.setPreferredSize(new Dimension(100, 2)); 
        // 
        // jPanel19 
        // 
        jPanel19.setLayout(new BoxLayout(jPanel19, BoxLayout.X_AXIS)); 
        jPanel19.add(jPanel21, 0); 
        jPanel19.add(jSeparator3, 1); 
        jPanel19.add(jPanel20, 2); 
        jPanel19.setPreferredSize(new Dimension(120, 1)); 
        // 
        // jPanel20 
        // 
        jPanel20.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
        jPanel20.setMaximumSize(new Dimension(10, 32767)); 
        // 
        // jPanel21 
        // 
        jPanel21.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
        jPanel21.setMaximumSize(new Dimension(10, 32767)); 
        // 
        // jPanel22 
        // 
        jPanel22.setLayout(new BoxLayout(jPanel22, BoxLayout.X_AXIS)); 
        jPanel22.add(jPanel23, 0); 
        jPanel22.add(jPanel24, 1); 
        // 
        // jCheckBoxDiscardMessage 
        // 
        jCheckBoxDiscardMessage.setText("jCheckBoxDiscardMessage"); 
        jCheckBoxDiscardMessage.addItemListener(new ItemListener() { 
            public void itemStateChanged(ItemEvent e) 
            { 
                jCheckBoxDiscardMessage_itemStateChanged(e); 
            } 
 
        }); 
        // 
        // jPanel23 
        // 
        jPanel23.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        jPanel23.add(jCheckBoxDiscardMessage, 0); 
        // 
        // jButtonOk 
        // 
        jButtonOk.setText("jButtonOk"); 
        jButtonOk.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) 
            { 
                jButtonOk_actionPerformed(e); 
            } 
 
        }); 
        // 
        // jPanel24 
        // 
        jPanel24.setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 10)); 
        jPanel24.add(jButtonOk, 0); 
        // 
        // jPanel15 
        // 
        jPanel15.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
        jPanel15.setMinimumSize(new Dimension(5, 10)); 
        jPanel15.setPreferredSize(new Dimension(5, 10)); 
        // 
        // JDialogDiscardableMessage
        // 
        this.setTitle("PgeepShowDiscardableMessager - extends JFrame"); 
        this.setLocation(new Point(209, 215)); 
        this.setSize(new Dimension(486, 150)); 
    } 
      
    /////////////////////////////////////////////////////////////////////////////   
    //
    //     BEGIN CODE AREA 
    //     
    //     - No code to be included ABOVE THIS AREA!
    //     - Define our constructor(s) in this area.
    //     - Graphical componant aspect may and *must* be modified with JFrameBuilder 
    //       by following these easy rules.  
    /////////////////////////////////////////////////////////////////////////////    
   
    
    ///////////////////////////////////////////////////////////////////////////
    // Global fields and methods declared by us SafeLogic
    // DO NOT DECLARE FIELDS AND METHODS ABOVE BECAUSE OF CODE GENERATION
    ///////////////////////////////////////////////////////////////////////////
    
    /** The messages in i18n */
    private MessagesManager messages = new  MessagesManager();    
    
    /** Add a clipboard manager for right button mouse control over input text fields */
    private ClipboardManager clipboard = null;
    
    public JDialogDiscardableMessage(JFrame JFrameparent, String message)
    {
        super(JFrameparent, true);
        initializeComponent();

        initializeCompany(JFrameparent, message);
    }
    
    /**
     * This is the method to include in the constructor
     */
    private void initializeCompany(JFrame JFrameparent, String message)
    {
        UserPrefManager userPreferencesManager = new UserPrefManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());   
        
        Sha1 hashcode = new Sha1();
        String hashMessage = null;
        
        try
        {
            hashMessage = hashcode.getHexHash(message.getBytes());
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            hashMessage = "";
        }

        String strDiscardMessage = prefs.get(hashMessage,  Boolean.toString(false));
        if (strDiscardMessage.equalsIgnoreCase("true"))
        {
            this.dispose();
            return;
        }
        
        // Set the default look and feel
        //LookAndFeelMgr.setDefault(this);
                        
        jLabelLogo.setIcon(null);
        jLabelLogo.setIcon(Parms.createImageIcon(Parms.ABOUT_ICON));
        
        // Add a Clipboard Manager
        clipboard = new ClipboardManager(contentPane);
        
        //this.setSize(new Dimension(WindowSettingMgr.KEY_LIST_X, 
        //             (int)this.getSize().getHeight()));
             
        this.jTextAreaMessage.setMargin(new Insets(5, 5, 5, 5));
        this.jTextAreaMessage.setEditable(false);
        this.jTextAreaMessage.setText(message);
        
        jTextAreaMessage.setFont(jButtonOk.getFont());
        jTextAreaMessage.setBackground(Color.WHITE);
        
        Font bold = jTextAreaMessage.getFont().deriveFont(Font.BOLD);
        this.jTextAreaMessage.setFont(bold);
        
        // Buttons text
        jCheckBoxDiscardMessage.setText(messages.getMessage("DO_NOT_SHOW_ME_AGAIN_MSG"));        
        jButtonOk.setText(messages.getMessage("OK"));
        
        this.setTitle(messages.getMessage("WARNING"));
                
//      If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        
        keyListenerAdder();
                 
        this.setSize(new Dimension(496, 150));
        
        Dimension dim = this.getSize();        
        double width = (int) dim.getWidth();
        
        // 57 = size of logo (52) + 5 spacing
        int messageWidth = jTextAreaMessage.getPreferredSize().width + 57 + 20;
        
        //System.out.println("width   : " + width);
        //System.out.println("minWidth: " + minWidth);
        
        //if (width < minWidth )
        
        if (messageWidth > 300)
        {
            this.setSize(new Dimension((int)messageWidth + 20, dim.height));
            this.setPreferredSize(new Dimension((int)messageWidth + 20, dim.height));
            this.setMinimumSize(new Dimension((int)messageWidth + 20, dim.height));
        }           
        
        this.setLocationRelativeTo(JFrameparent);   
        System.out.println(this.getSize());
        
        this.setResizable(true); 
        this.setAlwaysOnTop(true);
        this.setVisible(true);
                
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    ///////////////////////////////////////////////////////////////////////////    
    
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
            int keyCode = e.getKeyCode();
          
            if (keyCode == KeyEvent.VK_ENTER)
            {
                jButtonOk_actionPerformed(null);
            }
            if (keyCode == KeyEvent.VK_ESCAPE)
            {
                close();
            }  
        }       
    }
	private void jCheckBoxDiscardMessage_itemStateChanged(ItemEvent e)
	{
		
	}

    private void close()
    {
        this.dispose();
    }
    
	private void jButtonOk_actionPerformed(ActionEvent e)
	{
        // Store it if we don't want to display the message anymore
	    if (this.jCheckBoxDiscardMessage.isSelected())
        {
            UserPrefManager userPreferencesManager = new UserPrefManager();
            Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());  
            
            Sha1 hashcode = new Sha1();
            String hashMessage = null;
            
            try
            {
                hashMessage = hashcode.getHexHash(this.jTextAreaMessage.getText().getBytes());
                prefs.put(hashMessage, Boolean.toString(true));    
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
                hashMessage = "";
            }

        }
        
        this.dispose();
	}
































 

//============================= Testing ================================//
//=                                                                    =//
//= The following main method is just for testing this class you built.=//
//= After testing,you may simply delete it.                            =//
//======================================================================//
	public static void main(String[] args)
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception ex)
		{
			System.out.println("Failed loading L&F: ");
			System.out.println(ex);
		}
        
		new JDialogDiscardableMessage(null, "message to display");
   	}
    
//= End of Testing =


}
