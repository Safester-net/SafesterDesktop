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


import com.google.api.services.people.v1.model.Person;
import net.safester.application.addrbooknew.tools.CryptAppUtil;
import net.safester.application.addrbooknew.tools.SessionUtil;
import net.safester.application.addrbooknew.tools.RecipientCellEditor;
import net.safester.application.addrbooknew.tools.RecipientEntriesTableCreator;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


import com.swing.util.SwingUtil;
import java.sql.Connection;
import java.text.DecimalFormat;
import static net.safester.application.addrbooknew.AddressBookImportCsv1.ADDR_HEIGHT;
import static net.safester.application.addrbooknew.AddressBookImportCsv1.ADDR_WIDTH;
import static net.safester.application.addrbooknew.AddressBookImportCsv2.CR_LF;
import net.safester.application.addrbooknew.gmail.AddressBookImportGmail;
import net.safester.application.addrbooknew.gmail.GoogleContacts;
import net.safester.application.addrbooknew.gmail.GoogleRecipientsBuilder;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;
import net.safester.application.photo.UsersAddressBookCache;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.TableClipboardManager;
import net.safester.application.util.TableUtil;
import net.safester.noobs.clientserver.AddressBookListTransfer;
import net.safester.noobs.clientserver.AddressBookLocal;
import net.safester.noobs.clientserver.AddressBookNewLocal;

/**
 * Main class & frame to import external address book.
 *
 * @author Nicolas de Pomereu
 */
public class AddressBookImportFinal extends javax.swing.JDialog {

    /**
     * The parent JFrame
     */
    private Window parent = null;

    /**
     * The names to import
     */
    private List<String> names = null;

    /**
     * The emails to import
     */
    private List<String> emails = null;

    /**
     * Add a clipboard manager for right button mouse control over input text
     * fields
     */
    public TableClipboardManager tableClipboardManager = null;

    public ClipboardManager clipboardManager = null;
    private Font m_font = new Font("Tahoma", Font.PLAIN, 13);

    private File file = null;

    private List<RecipientEntry> recipientEntries = new ArrayList<RecipientEntry>();
    private boolean doDisplayFirstBeforeLast = false;
    
    private static String LABEL_CSV_IMPORT = MessagesManager.get("importing_contacts_from_a_csv_file");
    private static String LABEL_OUTLOOK_IMPORT = MessagesManager.get("importing_contacts_from_outlook");
    private static String LABEL_GOOGLE_IMPORT = MessagesManager.get("importing_contacts_from_gmail");
    
    private static String HELP_DURING_IMPORT_CSV = MessagesManager.get("please_wait_while_import_csv");
    private static String HELP_DURING_IMPORT_OUTLOOK = MessagesManager.get("please_wait_while_import_outlook");
    private static String HELP_DURING_IMPORT_GOOGLE = MessagesManager.get("please_wait_while_import_gmail");
       
    private static String HELP_AFTER_IMPORT = MessagesManager.get("click_go_to_finalize_import");
    
    private static int ORIGIN_CSV_IMPORT = 1;
    private static int ORIGIN_OUTLOOK_IMPORT = 2;
    private static int ORIGIN_GOOGLE_IMPORT = 3;
                    
    private int origin = -1;
    private GoogleContacts googleContacts;
                
    private Connection connection = null;
    private int userNumber = -1;
    
    /**
     * Creates instance for CSV import
     * @param parent
     * @param recipientEntries
     * @param file
     * @param connection
     * @param userNumber
     */
    public AddressBookImportFinal(Window parent, List<RecipientEntry> recipientEntries, File file, Connection connection, int userNumber) {

        this.parent = parent;

        if (recipientEntries == null) {
            throw new NullPointerException("pdfRecipients is null!");
        }

        this.recipientEntries = recipientEntries;
        this.file = file;

        origin = ORIGIN_CSV_IMPORT;
        this.connection = connection;
        this.userNumber = userNumber;
        
        initComponents();
        initializeCompany();
    }
    
    /**
     * Creates instance for Outlook Import
     * @param parent
     * @param recipientEntries the value of recipientEntries
     */
    public AddressBookImportFinal(Window parent, List<RecipientEntry> recipientEntries, Connection connection, int userNumber) {

        this.parent = parent;
        
        if (recipientEntries == null) {
            throw new NullPointerException("pdfRecipients is null!");
        }

        this.recipientEntries = recipientEntries;
        origin = ORIGIN_OUTLOOK_IMPORT;
        this.connection = connection;
        this.userNumber = userNumber;
                
        initComponents();
        initializeCompany();
    }
    
     /**
     * Creates instance for Gmails Import
     * @param parent
     * @param googleContacts
     * @param doDisplayFirstBeforeLast
     * @param connection
     */
    
    public AddressBookImportFinal(Window parent, GoogleContacts googleContacts, boolean doDisplayFirstBeforeLast, Connection connection, int userNumber) {

        this.parent = parent;
        
        if (googleContacts == null) {
            throw new NullPointerException("googleContacts is null!");
        }
        
        this.googleContacts = googleContacts;
        this.doDisplayFirstBeforeLast = doDisplayFirstBeforeLast;
        this.connection = connection;
        this.userNumber = userNumber;
        
        origin = ORIGIN_GOOGLE_IMPORT;
                
        initComponents();
        initializeCompany();
    }
    
    
    /**
     * This is the method to include in the constructor
     */
    public void initializeCompany() {

        try {
            this.setIconImage(ImageParmsUtil.getAppIcon());
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }
        
        //click_go_to_finalize_import
        //allow_creation_of_duplicates
        //do_not_import_duplicates 
        //replace_dulicates_with_items_imported
        
        jLabelTitle.setText(MessagesManager.get("importing_contacts"));
        jLabelHelp.setText(MessagesManager.get("click_go_to_finalize_import"));
        jRadioButtonDuplicatesCreate.setText(MessagesManager.get("allow_creation_of_duplicates"));
        jRadioButtonDuplicatesNoImport.setText(MessagesManager.get("do_not_import_duplicates"));
        jRadioButtonDuplicatesReplace.setText(MessagesManager.get("replace_dulicates_with_items_imported"));
        
        jButtonPrevious.setText("< " + MessagesManager.get("previous"));
        jButtonClose.setText(MessagesManager.get("close"));
        jButtonClose.setText(MessagesManager.get("cancel"));
        
        this.setModal(true);
        buttonGroupOptions.add(jRadioButtonDuplicatesCreate);
        buttonGroupOptions.add(jRadioButtonDuplicatesReplace);
        buttonGroupOptions.add(jRadioButtonDuplicatesNoImport);

        jRadioButtonDuplicatesReplace.setSelected(true);
        
        clipboardManager = new ClipboardManager(rootPane);

        this.setSize(AddressBookImportCsv1.ADDR_WIDTH, AddressBookImportCsv1.ADDR_HEIGHT);
        this.setPreferredSize(new Dimension(ADDR_WIDTH, ADDR_HEIGHT));

        /*
        if (parent != null) {
            this.setLocationRelativeTo(parent);
        }
        */

        if (origin == ORIGIN_CSV_IMPORT) {
            jLabelTitle.setText(LABEL_CSV_IMPORT);
            jLabelTitle.setIcon(Parms.createImageIcon(Parms.ICON_IMPORT_CSV_PATH));
            jLabelHelp.setText(HELP_DURING_IMPORT_CSV);
        } else if (origin == ORIGIN_OUTLOOK_IMPORT) {
            jLabelTitle.setText(LABEL_OUTLOOK_IMPORT);
            jLabelTitle.setIcon(Parms.createImageIcon(Parms.ICON_IMPORT_OUTLOOK_PATH));
            jLabelHelp.setText(HELP_DURING_IMPORT_OUTLOOK);
        } else if (origin == ORIGIN_GOOGLE_IMPORT) {
            jLabelTitle.setIcon(Parms.createImageIcon(Parms.ICON_IMPORT_GMAIL_PATH));
            jLabelTitle.setText(LABEL_GOOGLE_IMPORT);
            jLabelHelp.setText(HELP_DURING_IMPORT_GOOGLE);
        }
                
        List<RecipientEntry> recipientsInit = new ArrayList<RecipientEntry>();
        createTableRecipientEntries(recipientsInit);
        
        createTableRecipientEntriesInThread();
       
        this.setTitle(this.jLabelTitle.getText());

        // These 2 stupid lines : only to force to display top of file first
        this.keyListenerAdder();
        this.setLocationByPlatform(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                jButtonCloseActionPerformed(null);
            }
        });

        ButtonResizer br = new ButtonResizer(jPanelButtons);
        br.setWidthToMax();

        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);
                
        this.setLocationRelativeTo(parent);
        WindowSettingManager.load(this);
        
        // Because of dynamic Swing fields settings on language by indowSettingMgr.load(this);
        // We misy reset jLabelTitle
        jLabelTitle.setText(this.getTitle());

        this.setVisible(true);
    }

    public void createTableRecipientEntriesInThread() {

        Thread t = new Thread() {
            public void run() {

                if (origin == ORIGIN_CSV_IMPORT) {
                    // Direct creation of table
                    createRecipientEntriesFromCsv();
                } else if (origin == ORIGIN_OUTLOOK_IMPORT) {
                    createRecipientEntriesFromOutlook();
                } else if (origin == ORIGIN_GOOGLE_IMPORT) {
                    createPdfRecipientsFromGoogle();
                }
                else {
                    throw new IllegalArgumentException("origin is invalid: " + origin);
                }
            }

        };
        t.start();
        
    }
                    
    private void createRecipientEntriesFromCsv() {
        try {
            this.setCursor(Cursor
                    .getPredefinedCursor(Cursor.WAIT_CURSOR));

            this.setButtonsEnabled(false);

            createTableRecipientEntries(recipientEntries);

            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception ex) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Impossible to access datas of current CSV file. " + CR_LF
                    + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setButtonsEnabled(true);
            jLabelHelp.setText(HELP_AFTER_IMPORT);
        }
    }
    
    private void createRecipientEntriesFromOutlook() {

        try {
            this.setCursor(Cursor
                    .getPredefinedCursor(Cursor.WAIT_CURSOR));

            this.setButtonsEnabled(false);            
            
            createTableRecipientEntries(recipientEntries);
            
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception ex) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Impossible to acess Outlook Office data. " + CR_LF
                    + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
        }
        finally {
            this.setButtonsEnabled(true);
            jLabelHelp.setText(HELP_AFTER_IMPORT);
        }
                    
    }

    
    private void createPdfRecipientsFromGoogle() {
    
        try {
            this.setCursor(Cursor
                    .getPredefinedCursor(Cursor.WAIT_CURSOR));

            this.setButtonsEnabled(false);            

            List<Person> persons = this.googleContacts.getPersons();
            
            GoogleRecipientsBuilder googlePdfRecipientsBuilder = new GoogleRecipientsBuilder(persons, doDisplayFirstBeforeLast);
            this.recipientEntries = googlePdfRecipientsBuilder.build();
            
            createTableRecipientEntries(recipientEntries);
            
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception ex) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Impossible to access Gmail Contacts. " + CR_LF
                    + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
        }
        finally {
            this.setButtonsEnabled(true);
            jLabelHelp.setText(HELP_AFTER_IMPORT);
        }
        
    }
     
    
    private void setButtonsEnabled(boolean enabled) {
        
        jPanelOptions.setEnabled(enabled);
        jRadioButtonDuplicatesCreate.setEnabled(enabled);
        jRadioButtonDuplicatesNoImport.setEnabled(enabled);
        jRadioButtonDuplicatesReplace.setEnabled(enabled);
        
        List<Component> components = SwingUtil.getAllComponants(jPanelButtons);
               
        for (int i = 0; i < components.size(); i++)
        {
            Component component = (Component) components.get(i);
            
            if (component instanceof JButton)
            {                
                JButton currentButton = (JButton)component; 
                currentButton.setEnabled(enabled);
            }
        }
    }
        
    public void createTableRecipientEntries(List<RecipientEntry> pdfRecipients) {
                        
        // This class will fo all the format checks
        RecipientEntriesTableCreator pdfRecipientsTableCreator = new RecipientEntriesTableCreator(pdfRecipients);
        pdfRecipientsTableCreator.setTableEditable(false);
        pdfRecipientsTableCreator.setTableSortable(false);

        jTable1 = pdfRecipientsTableCreator.create();

        jTable1.requestFocusInWindow();

        // Sey colors to be clean with all environments
        // jTable1.setSelectionBackground(PgeepColor.LIGHT_BLUE);    
        // jTable1.setSelectionForeground(Color.BLACK);
        jTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        TableColumn col = jTable1.getColumnModel().getColumn(0);
        col.setCellEditor(new RecipientCellEditor(0));

        
        //col = jTable1.getColumnModel().getColumn(2);
        //col.setCellEditor(new RecipientCellEditor(2));

        TableUtil.selectRowWhenMouverOverRecipients(jTable1);
                
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String count = myFormatter.format(jTable1.getModel().getRowCount());
        jLabelCount.setText(count+ " Contacts");
                
        jTable1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TableModel tm1 = (TableModel) e.getSource();
                DecimalFormat myFormatter = new DecimalFormat("###,###");
                String count = myFormatter.format(tm1.getRowCount() );
                jLabelCount.setText(count+ " Contacts");
            }
        });
        
        //col = jTable1.getColumnModel().getColumn(3);
        //col.setCellEditor(new RecipientCellEditor(3));

        //jTable1.setToolTipText("Tooltip pour la Table");
        jScrollPane1.setViewportView(jTable1);

        Color tableBackground = null;
        tableBackground = jTable1.getBackground();
        jTable1.getParent().setBackground(tableBackground);

        jTable1.setIntercellSpacing(new Dimension(5, 1));

        // HACK
        //tableClipboardManager = new TableClipboardManager(this, jTable1, true);
        tableClipboardManager = new TableClipboardManager(jTable1);

        // Datatips : Whenever the mouse cursor is over a cell whose content 
        // is partially hidden, a popup with the complete cell content is shown
        // Please see https://datatips.dev.java.net/
        //DataTipManager.get().register(jTable1);           
        // Add Drag function to drag files
        //TableTransferHandler tableTransferHandler = new TableTransferHandler(true);
        // Add Drag capabilities to Centers
        //jTable1.setDragEnabled(true);
        //jTable1.setTransferHandler(tableTransferHandler); // refuse to drop inside
        //jTable1.getModel().addTableModelListener(this);    
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
            
            if (keyCode == KeyEvent.VK_P && e.isControlDown()) {
                jButtonPreviousActionPerformed(null);
            }
        }
    }

    /**
     * Import the names and emails list onto the address book
     */
    private void go() {
                
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            
            List<RecipientEntry> finalRecipientEntries = new ArrayList<RecipientEntry>();
            
            AddressBookListTransfer addressBookListTransfer = new AddressBookListTransfer(connection, userNumber);
            //Get current list of addressBook
            List<AddressBookNewLocal> addressBookLocals = addressBookListTransfer.getList();
            
            List<RecipientEntry> serverRecipientEntries = new ArrayList<>();
            for (AddressBookNewLocal addressBookLocal : addressBookLocals) {
                serverRecipientEntries.add(new RecipientEntry(addressBookLocal.getEmail(), addressBookLocal.getName(),
                        addressBookLocal.getCompany(), addressBookLocal.getCellPhone(), null));
            }
            
            if (jRadioButtonDuplicatesCreate.isSelected()) {
                finalRecipientEntries.addAll(serverRecipientEntries);
                finalRecipientEntries.addAll(recipientEntries);
                
            }
            else if (jRadioButtonDuplicatesReplace.isSelected()) {
                
                for (RecipientEntry serverRecipientEntry : serverRecipientEntries) {
                    if (! recipientEntries.contains(serverRecipientEntry)) {
                        finalRecipientEntries.add(serverRecipientEntry);
                    }
                }
                
                finalRecipientEntries.addAll(recipientEntries);

            }
            else if (jRadioButtonDuplicatesNoImport.isSelected()) {
                
                finalRecipientEntries.addAll(serverRecipientEntries);
                  
                for (RecipientEntry newRecipientEntry : recipientEntries) {
                    if (! serverRecipientEntries.contains(newRecipientEntry)) {
                        finalRecipientEntries.add(newRecipientEntry);
                    }
                }
                
            }
            
            // Ok do the import in remoe DB
            AddressBookUploader addressBookUploader = new AddressBookUploader(connection, userNumber, finalRecipientEntries);
            addressBookUploader.importOnServer();
            
            UsersAddressBookCache.clear();
            
            this.dispose();
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, MessagesManager.get("contacts_successfully_imported"), Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);

            
            
        } catch (Exception exception) {
             this.setCursor(Cursor.getDefaultCursor());
             JOptionPaneNewCustom.showException(this, exception);
        }
                
      }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupOptions = new javax.swing.ButtonGroup();
        jPanelNorth = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelCenterMain = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSepLine2 = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jPanelLabel = new javax.swing.JPanel();
        jLabelHelp = new javax.swing.JLabel();
        jPanelSep = new javax.swing.JPanel();
        jPanelOptions = new javax.swing.JPanel();
        jPanelOptionsLine2 = new javax.swing.JPanel();
        jRadioButtonDuplicatesCreate = new javax.swing.JRadioButton();
        jPanelOptionsLine1 = new javax.swing.JPanel();
        jRadioButtonDuplicatesReplace = new javax.swing.JRadioButton();
        jPanelOptionsLine3 = new javax.swing.JPanel();
        jRadioButtonDuplicatesNoImport = new javax.swing.JRadioButton();
        jPanelSep2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanelCount = new javax.swing.JPanel();
        jLabelCount = new javax.swing.JLabel();
        jPanelSepLine3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jSeparator6 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonPrevious = new javax.swing.JButton();
        jButtonGo = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jPanel0Pixels1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 12));

        jLabelTitle.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/book_telephone.png"))); // NOI18N
        jLabelTitle.setText("Importer des Contacts");
        jPanelNorth.add(jLabelTitle);

        getContentPane().add(jPanelNorth);

        jPanelCenterMain.setLayout(new javax.swing.BoxLayout(jPanelCenterMain, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel1.setMinimumSize(new java.awt.Dimension(10, 11));
        jPanel1.setPreferredSize(new java.awt.Dimension(10, 11));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenterMain.add(jPanel1);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSepLine2.setMaximumSize(new java.awt.Dimension(32787, 5));
        jPanelSepLine2.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelSepLine2.setPreferredSize(new java.awt.Dimension(0, 5));
        jPanelSepLine2.setLayout(new javax.swing.BoxLayout(jPanelSepLine2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSepLine2.add(jSeparator5);

        jPanelCenter.add(jPanelSepLine2);

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanel2.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanel2.setPreferredSize(new java.awt.Dimension(376, 5));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 737, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanel2);

        jPanelLabel.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanelLabel.setMinimumSize(new java.awt.Dimension(377, 35));
        jPanelLabel.setPreferredSize(new java.awt.Dimension(377, 35));
        jPanelLabel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 10));

        jLabelHelp.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelHelp.setText("Cliquer sur Go! pour finaliser l'import :");
        jLabelHelp.setPreferredSize(new java.awt.Dimension(400, 16));
        jPanelLabel.add(jLabelHelp);

        jPanelCenter.add(jPanelLabel);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelSep.setPreferredSize(new java.awt.Dimension(376, 5));

        javax.swing.GroupLayout jPanelSepLayout = new javax.swing.GroupLayout(jPanelSep);
        jPanelSep.setLayout(jPanelSepLayout);
        jPanelSepLayout.setHorizontalGroup(
            jPanelSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 737, Short.MAX_VALUE)
        );
        jPanelSepLayout.setVerticalGroup(
            jPanelSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep);

        jPanelOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        jPanelOptions.setLayout(new javax.swing.BoxLayout(jPanelOptions, javax.swing.BoxLayout.Y_AXIS));

        jPanelOptionsLine2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jRadioButtonDuplicatesCreate.setSelected(true);
        jRadioButtonDuplicatesCreate.setText("Autoriser la création de doublons");
        jPanelOptionsLine2.add(jRadioButtonDuplicatesCreate);

        jPanelOptions.add(jPanelOptionsLine2);

        jPanelOptionsLine1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jRadioButtonDuplicatesReplace.setText("Remplacer les doublons pas les éléments importés ");
        jPanelOptionsLine1.add(jRadioButtonDuplicatesReplace);

        jPanelOptions.add(jPanelOptionsLine1);

        jPanelOptionsLine3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jRadioButtonDuplicatesNoImport.setText("Ne pas importer les doublons ");
        jPanelOptionsLine3.add(jRadioButtonDuplicatesNoImport);

        jPanelOptions.add(jPanelOptionsLine3);

        jPanelCenter.add(jPanelOptions);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSep2.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(376, 10));

        javax.swing.GroupLayout jPanelSep2Layout = new javax.swing.GroupLayout(jPanelSep2);
        jPanelSep2.setLayout(jPanelSep2Layout);
        jPanelSep2Layout.setHorizontalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 737, Short.MAX_VALUE)
        );
        jPanelSep2Layout.setVerticalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep2);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanelCenter.add(jScrollPane1);

        jPanelCenterMain.add(jPanelCenter);

        jPanel3.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel3.setMinimumSize(new java.awt.Dimension(10, 11));
        jPanel3.setPreferredSize(new java.awt.Dimension(10, 11));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenterMain.add(jPanel3);

        getContentPane().add(jPanelCenterMain);

        jPanelCount.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanelCount.setName(""); // NOI18N
        jPanelCount.setPreferredSize(new java.awt.Dimension(593, 35));
        jPanelCount.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 8));

        jLabelCount.setText("jLabelCount");
        jLabelCount.setPreferredSize(new java.awt.Dimension(200, 16));
        jPanelCount.add(jLabelCount);

        getContentPane().add(jPanelCount);

        jPanelSepLine3.setMaximumSize(new java.awt.Dimension(32787, 5));
        jPanelSepLine3.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine3.setLayout(new javax.swing.BoxLayout(jPanelSepLine3, javax.swing.BoxLayout.LINE_AXIS));

        jPanel4.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine3.add(jPanel4);
        jPanelSepLine3.add(jSeparator6);

        jPanel5.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine3.add(jPanel5);

        getContentPane().add(jPanelSepLine3);

        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonPrevious.setText("< Précédent");
        jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonPrevious);

        jButtonGo.setText("Go!");
        jButtonGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGoActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonGo);

        jButtonClose.setText("Annuler");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonClose);

        jPanel0Pixels1.setMaximumSize(new java.awt.Dimension(0, 0));
        jPanel0Pixels1.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel0Pixels1Layout = new javax.swing.GroupLayout(jPanel0Pixels1);
        jPanel0Pixels1.setLayout(jPanel0Pixels1Layout);
        jPanel0Pixels1Layout.setHorizontalGroup(
            jPanel0Pixels1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel0Pixels1Layout.setVerticalGroup(
            jPanel0Pixels1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelButtons.add(jPanel0Pixels1);

        getContentPane().add(jPanelButtons);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousActionPerformed
    this.dispose();
    
    if (origin == ORIGIN_CSV_IMPORT) {
        AddressBookImportCsv2 addressBookImport2 = new AddressBookImportCsv2(parent, file, connection, userNumber);
    }
    else if (origin == ORIGIN_OUTLOOK_IMPORT) {
        FolderChooserNew folderChooserNew;
        try {
            folderChooserNew = new FolderChooserNew(parent, this.connection, this.userNumber);
            folderChooserNew.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Impossible to choose an Contact folder in Outlook Office. " + CR_LF
                    + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            //OutlookUtilMoyosoft.outlookDispose(outlook);
            return;
        }

    } else if (origin == ORIGIN_GOOGLE_IMPORT) {
        AddressBookImportGmail addressBookImportGmail = new AddressBookImportGmail(parent, connection, userNumber);
    }
    else {
        throw new IllegalArgumentException("origin is invalid: " + origin);
    }

}//GEN-LAST:event_jButtonPreviousActionPerformed

private void jButtonGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGoActionPerformed
    go();
}//GEN-LAST:event_jButtonGoActionPerformed

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed

    //OutlookUtilMoyosoft.outlookDispose(outlook);
    this.dispose();

}//GEN-LAST:event_jButtonCloseActionPerformed

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

                JOptionPane.showMessageDialog(null, "No implemented yet");

                //new AddressBookImportCsv3(null, null, null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupOptions;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonGo;
    private javax.swing.JButton jButtonPrevious;
    private javax.swing.JLabel jLabelCount;
    private javax.swing.JLabel jLabelHelp;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel0Pixels1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCenterMain;
    private javax.swing.JPanel jPanelCount;
    private javax.swing.JPanel jPanelLabel;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelOptions;
    private javax.swing.JPanel jPanelOptionsLine1;
    private javax.swing.JPanel jPanelOptionsLine2;
    private javax.swing.JPanel jPanelOptionsLine3;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSepLine2;
    private javax.swing.JPanel jPanelSepLine3;
    private javax.swing.JRadioButton jRadioButtonDuplicatesCreate;
    private javax.swing.JRadioButton jRadioButtonDuplicatesNoImport;
    private javax.swing.JRadioButton jRadioButtonDuplicatesReplace;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables



}
