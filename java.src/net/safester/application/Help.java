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

/****************************************************************/ 
/*                      Help                               */
/*                                                              */ 
/****************************************************************/ 
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.safester.application.messages.LanguageManager;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.DesktopWrapper;
import net.safester.application.tool.WindowSettingManager;

import com.safelogic.utilx.io.stream.LineInputStream;
import com.swing.util.SwingUtil;
import java.io.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;
import org.awakefw.file.api.util.HtmlConverter;
/** 
 * Summary description for Help
 * 
 */ 
public class Help extends JFrame
{ 
    // Variables declaration 
    private JPanel contentPane; 
    //----- 
    private JPanel jPanelSouth; 
    //----- 
    private JPanel jPanelNorth; 
    //----- 
    private JPanel jPanelWest; 
    //----- 
    private JPanel jPanelEst; 
    //----- 
    private JPanel jPanel7; 
    //----- 
    private JButton jButtonClose; 
    private JPanel jPanel11; 
    //----- 
    private JPanel jPanel13; 
    //----- 
    private JPanel jPanel14; 
    //----- 
    private JEditorPane jEditorPane; 
    private JScrollPane jScrollPane; 
    private JPanel jPanel15; 
    //----- 
    // End of variables declaration 


    public Help()
    { 
        super(); 
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
        jPanelSouth = new JPanel(); 
        //----- 
        jPanelNorth = new JPanel(); 
        //----- 
        jPanelWest = new JPanel(); 
        //----- 
        jPanelEst = new JPanel(); 
        //----- 
        jPanel7 = new JPanel(); 
        //----- 
        jButtonClose = new JButton(); 
        jPanel11 = new JPanel(); 
        //----- 
        jPanel13 = new JPanel(); 
        //----- 
        jPanel14 = new JPanel(); 
        //----- 
        jEditorPane = new JEditorPane(); 
        jScrollPane = new JScrollPane(); 
        jPanel15 = new JPanel(); 
        //----- 

        // 
        // contentPane 
        // 
        contentPane.setLayout(new BorderLayout(0, 0)); 
        contentPane.add(jPanelNorth, BorderLayout.NORTH); 
        contentPane.add(jPanelWest, BorderLayout.EAST); 
        contentPane.add(jPanelEst, BorderLayout.WEST); 
        contentPane.add(jPanelSouth, BorderLayout.SOUTH); 
        contentPane.add(jPanel15, BorderLayout.CENTER); 
        // 
        // jPanelSouth 
        // 
        jPanelSouth.setLayout(new BorderLayout(0, 0)); 
        jPanelSouth.add(jPanel7, BorderLayout.CENTER); 
        // 
        // jPanelNorth 
        // 
        jPanelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
        jPanelNorth.setMinimumSize(new Dimension(10, 12)); 
        jPanelNorth.setPreferredSize(new Dimension(10, 12)); 
        // 
        // jPanelWest 
        // 
        jPanelWest.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
        jPanelWest.setMaximumSize(new Dimension(12, 32767)); 
        jPanelWest.setMinimumSize(new Dimension(12, 10)); 
        jPanelWest.setPreferredSize(new Dimension(12, 10)); 
        // 
        // jPanelEst 
        // 
        jPanelEst.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
        jPanelEst.setMaximumSize(new Dimension(12, 32767)); 
        jPanelEst.setMinimumSize(new Dimension(12, 10)); 
        jPanelEst.setPreferredSize(new Dimension(12, 10)); 
        // 
        // jPanel7 
        // 
        jPanel7.setLayout(new BorderLayout(0, 0)); 
        jPanel7.add(jPanel11, BorderLayout.CENTER); 
        jPanel7.add(jPanel13, BorderLayout.WEST); 
        jPanel7.add(jPanel14, BorderLayout.EAST); 
        // 
        // jButtonClose 
        // 
        jButtonClose.setText("jButtonClose"); 
        jButtonClose.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) 
            { 
                jButtonClose_actionPerformed(e); 
            } 

        }); 
        // 
        // jPanel11 
        // 
        jPanel11.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 12)); 
        jPanel11.add(jButtonClose, 0); 
        // 
        // jPanel13 
        // 
        jPanel13.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
        jPanel13.setMinimumSize(new Dimension(12, 10)); 
        jPanel13.setPreferredSize(new Dimension(12, 10)); 
        // 
        // jPanel14 
        // 
        jPanel14.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
        jPanel14.setMinimumSize(new Dimension(12, 10)); 
        jPanel14.setPreferredSize(new Dimension(12, 10)); 
        // 
        // jEditorPane 
        // 
        jEditorPane.setText("jEditorPane"); 
        // 
        // jScrollPane 
        // 
        jScrollPane.setViewportView(jEditorPane); 
        // 
        // jPanel15 
        // 
        jPanel15.setLayout(new BorderLayout(0, 0)); 
        jPanel15.add(jScrollPane, BorderLayout.CENTER); 
        // 
        // Help
        //
        this.setTitle("Aide"); 
        this.setLocation(new Point(247, 72)); 
        this.setSize(new Dimension(372, 440)); 
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

    /** The debug flag */
    protected boolean DEBUG = false; //Debug.isSet(this);

    public static final String CR_LF = System.getProperty("line.separator") ;

    /** Messages in national language */
    private MessagesManager messages = new  MessagesManager();

    /** Add a clipboard manager for help text */
    private ClipboardManager clipboard = null;

    /**
     * 
     * @param w             The calling Frame
     * @param helpContentKeyWord   The Help Content key word
     */
    public Help(Frame w, String helpContentKeyWord)
    {         
        initializeComponent();         
        initializeCompany(helpContentKeyWord);
    } 

    /**
     * This is the method to include in the constructor
     * @param helpContentKeyWord   The Help Content key word
     */
    public void initializeCompany(String helpContentKeyWord)
    {

        Toolkit.getDefaultToolkit().setDynamicLayout(true);

        // Add a Clipboard Manager
        clipboard = new ClipboardManager(contentPane);  

        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);      

        jEditorPane.setText(getHtmlHelpContent(helpContentKeyWord));

        //System.out.println(jEditorPane.getMargin());
        //jEditorPane.setMargin(new Insets(3, 10, 3, 10));

        jButtonClose.setText(this.messages.getMessage("CLOSE_BUTTON"));

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

        //      If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                jButtonClose_actionPerformed(null);
            }
        });

        keyListenerAdder();

        // Load and activate previous windows settings
        // Defaults to upper left border
        WindowSettingManager.load(this, new Point(1, 1));

        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(messages.getMessage("HELP_MENU"));

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


        // These 2 stupid lines : only to Force to display top of file first
        jEditorPane.moveCaretPosition(0); 
        jEditorPane.setSelectionEnd(0);

        this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        this.setVisible(true);

    }

    public void saveSettings()
    {
        WindowSettingManager.save(this);
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
                //                public void keyReleased(KeyEvent e) 
                //                { 
                //                    keyReleased_actionPerformed(e); 
                //                } 
                public void keyPressed(KeyEvent e) 
                { 
                    keyPressed_actionPerformed(e); 
                } 
            }); 
        }
    }    

    /**
     * Return  the HTML content of a  HTML resource file in the message file package
     * @param helpContentKeyWord    The key word for help file retrieve
     * @return  the HTML content of a  HTML resource file in the message file package
     * @throws IOException
     */
    public static String getHtmlHelpContent(String helpContentKeyWord)
    {
        String htmlContent;
        try
        {
            String language = LanguageManager.getLanguage();

            String resource = "/" + MessagesManager.MESSAGE_FILES_PACKAGE;        
            resource = resource.replace(".", "/");

            // KEEP THIS CODE AS MODEL
            //java.net.URL myURL 
            //  = ResourceBundleTest.class.getResource("/com/safelogic/pgp/test/MyResource_fr.properties");

            if (helpContentKeyWord != null)
            {
                helpContentKeyWord = helpContentKeyWord.toLowerCase();
            }

            String helpFile =  helpContentKeyWord + "_" + language + ".html";
            String urlResource = resource + "/" + helpFile;

            //debug(urlResource);

            java.net.URL myURL = Help.class.getResource(urlResource);

            if (myURL == null)
            {                
                return "<font face=\"Arial\" size=4><br>" 
                + "<b>Please apologize. <br>  " 
                + "Help is not yet available for this topic. </b> <br>" 
                + "<br>"
                + "(" +  helpFile + ")";               
            }

            InputStream is = myURL.openStream(); 

            BufferedInputStream bisIn = new BufferedInputStream(is) ;
            LineInputStream lisIn = new LineInputStream(bisIn) ;

            String sLine = new String() ;

            htmlContent = "";

            while( (sLine = lisIn.readLine()) != null)
            {
                sLine = sLine.trim() ;
                //debug(sLine);

                htmlContent += sLine + CR_LF;
            }

            lisIn.close() ;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            htmlContent = e.getMessage();
        }

        return htmlContent;
    }   
    /**
     * Returns the content of a help text file as text
     * @param helpFileRaw   raw name of help file the string _fr.txt or _en.txt will be added to get the full name
     * @return  
     */
    public static String getHelpContentAsText(String helpFileRaw) {

        InputStream is = null;
        ByteArrayOutputStream out = null;

        try {

            String helpFileTxt = helpFileRaw + "_" + LanguageManager.getLanguage() + ".txt";
            
            is = Parms.class.getResourceAsStream("helpfiles/" + helpFileTxt);
            out = new ByteArrayOutputStream();

            try {
                IOUtils.copy(is, out);
                String text = out.toString("ISO-8859-1");
                
                if (isHtmlEncoded(text)) {
                    text = HtmlConverter.fromHtml(text);
                }

                return text;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return ioe.getMessage();
            }
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
        }
    }
    
            /**
     * Minimalist method to test if a string is HTML encoded
     * @param text
     * @return 
     */
    private static boolean isHtmlEncoded(String text) {
        if (text.contains("&") && text.contains(";")) {
            return true;
        } else {
            return false;
        }
    }
    
   
    ///////////////////////////////////////////////////////////////////////////
    // BUTTONS PART
    /////////////////////////////////////////////////////////////////////////// 


    private void jButtonClose_actionPerformed(ActionEvent e) 
    {         
        this.dispose();
    } 

    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    /////////////////////////////////////////////////////////////////////////// 

    private void keyPressed_actionPerformed(KeyEvent e) 
    {            
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED) 
        { 
            //System.out.println("in keyReleased_actionPerformed(KeyEvent e) ");

            int keyCode = e.getKeyCode();    
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_ENTER)
            {
                jButtonClose_actionPerformed(null);
            }                       
        }              
    } 

    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
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

        new Help(null, null);

    } 
    //= End of Testing =     

} 

