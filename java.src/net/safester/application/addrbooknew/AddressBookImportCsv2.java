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

import static net.safester.application.addrbooknew.AddressBookImportCsv1.ADDR_HEIGHT;
import static net.safester.application.addrbooknew.AddressBookImportCsv1.ADDR_WIDTH;
import static net.safester.application.addrbooknew.tools.AddressBookUtil.getSeparator;

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
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.UIManager;
import javax.swing.text.NumberFormatter;

import com.swing.util.SwingUtil;

import net.safester.application.Help;
import net.safester.application.addrbooknew.tools.MobileUtil;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.WindowSettingManager;

/**
 * Main class & frame to import external address book.
 *
 * @author Nicolas de Pomereu
 */
public class AddressBookImportCsv2 extends javax.swing.JDialog {

    public static final String CR_LF = System.getProperty("line.separator");

    /**
     * The parent JFrame
     */
    private Window parent = null;

    /**
     * The file to use
     */
    private File file;

    private int colCount = -1;
    private String separator;
    
    private final static boolean ONLY_NAME_AND_EMAIL = false;

    private Connection connection = null;
    private int userNumber = -1;
    
    /**
     * Creates new form NewsFrame
     */
    public AddressBookImportCsv2(Window parent, File file, Connection connection, int userNumber) {
        this.parent = parent;

        if (file == null) {
            throw new NullPointerException("file is null!");
        }

        if (! file.exists()) {
            throw new IllegalArgumentException("file does not exists: " + file);
        }

        this.file = file;
        this.connection = connection;
        this.userNumber = userNumber;
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

        this.setModal(true);
        this.setSize(AddressBookImportCsv1.ADDR_WIDTH, AddressBookImportCsv1.ADDR_HEIGHT);
        this.setPreferredSize(new Dimension(ADDR_WIDTH, ADDR_HEIGHT));

        if (parent != null) {
            this.setLocationRelativeTo(parent);
        }

        jLabelTitle.setText(MessagesManager.get("importing_contacts_from_a_csv_file"));
        this.jButtonPrevious.setText(MessagesManager.get("previouss"));
        this.jButtonNext.setText(MessagesManager.get("next"));
        this.jButtonClose.setText(MessagesManager.get("cancel"));
        
        JFormattedTextField txt = ((JSpinner.NumberEditor) jSpinnerEmailCol.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        JFormattedTextField txt2 = ((JSpinner.NumberEditor) jSpinnerLastnameCol.getEditor()).getTextField();
        ((NumberFormatter) txt2.getFormatter()).setAllowsInvalid(false);

        JFormattedTextField txt3 = ((JSpinner.NumberEditor) jSpinnerFirstnameCol.getEditor()).getTextField();
        ((NumberFormatter) txt3.getFormatter()).setAllowsInvalid(false);

        JFormattedTextField txt4 = ((JSpinner.NumberEditor) jSpinnerCompany.getEditor()).getTextField();
        ((NumberFormatter) txt4.getFormatter()).setAllowsInvalid(false);

        JFormattedTextField txt5 = ((JSpinner.NumberEditor) jSpinnerMobileCol.getEditor()).getTextField();
        ((NumberFormatter) txt5.getFormatter()).setAllowsInvalid(false);
        
                
        colCount = 1; // Must be at least 1 to initialise values
        this.jSpinnerEmailCol.setValue(new Integer(1));
        this.jSpinnerLastnameCol.setValue(new Integer(1));
        this.jSpinnerFirstnameCol.setValue(new Integer(1));
        
        this.jSpinnerCompany.setValue(new Integer(1));
        this.jSpinnerMobileCol.setValue(new Integer(1));

        jTextFieldFile.setToolTipText(file.toString());

        extractColumnPositions();

        jCheckBoxFirstLineIsDesc.setSelected(true);
        jCheckBoxDisplayFirstNameBefore.setSelected(true);
        jCheckBoxDisplayFirstNameBeforeItemStateChanged(null);

        this.setTitle(this.jLabelTitle.getText());

        jButtonDisplayFile.putClientProperty("JButton.buttonType", "square");

        MessagesManager messages = new MessagesManager();
        this.jTextFieldFile.setText(file.getName());
        this.jLabeCsvFile.setText(messages.getMessage("file"));
        jButtonDisplayFile.setText(messages.getMessage("view_the_file_format"));
        jLabelNameCol.setText(messages.getMessage("last_name_column"));
        jLabelFirstCol.setText(messages.getMessage("first_name_column"));
        jLabelEmailCol.setText(messages.getMessage("email_column"));
        jCheckBoxFirstLineIsDesc.setText(messages.getMessage("first_line_is_desc"));
        jCheckBoxNameAndFirstSameColumn.setText(messages.getMessage("first_last_in_same_column"));
        
        jCheckBoxDisplayFirstNameBefore.setText(MessagesManager.get("display_firstname_before_lastname") + " (Ex: Smith John)");
        jCheckBoxDisplayFirstNameBeforeItemStateChanged(null);
        
        
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        jEditorPane.setText(Help.getHtmlHelpContent("snip/address_book_import_csv_2"));

        this.jButtonPrevious.setText("< " + messages.getMessage("prev"));
        this.jButtonNext.setText(messages.getMessage("next") + " >");
        this.jButtonClose.setText(messages.getMessage("cancel"));

        if (ONLY_NAME_AND_EMAIL) {
            jPanelSpinnerCompany.setVisible(false);
            jPanelSpinnerMobile.setVisible(false);
        }
        
        // These 2 stupid lines : only to force to display top of file first
        jEditorPane.moveCaretPosition(0);
        jEditorPane.setSelectionEnd(0);

        this.keyListenerAdder();
        this.setLocationByPlatform(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                jButtonCloseActionPerformed(null);
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                saveSettings();
            }

            public void componentResized(ComponentEvent e) {
                saveSettings();
            }
        });

        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();

        SwingUtil.resizeJComponentsForAll(rootPane);

        //this.setLocationRelativeTo(parent);
        WindowSettingManager.load(this);

        pack();
        this.setVisible(true);

        if (separator == null) {
            this.jButtonNext.setEnabled(false);
            String message = Help.getHelpContentAsText("snip/help_address_book_import_csv2");
            JOptionPane.showMessageDialog(rootPane, message, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
        }

    }

    public void saveSettings() {
        // For window size tunings
        //WindowSettingMgr.save(this);
    }

    /**
     * read the file in order to read the column positions
     */
    private void extractColumnPositions() {
        try {

            separator = getSeparator(file);
            if (separator == null) {
                return;
            }

            CsvAddressBook csvAddressBook = new CsvAddressBook(file, separator);
            colCount = csvAddressBook.countColumns();

            System.out.println("colCount: " + colCount);
            
            int emailCol = csvAddressBook.getProbableColumnForType(CsvAddressBook.TYPE_EMAIL);
            int lastnameCol = csvAddressBook.getProbableColumnForType(CsvAddressBook.TYPE_LASTNAME);
            int firstnameCol = csvAddressBook.getProbableColumnForType(CsvAddressBook.TYPE_FIRSTNAME);

            int companyCol = csvAddressBook.getProbableColumnForType(CsvAddressBook.TYPE_COMPANY);
            int mobileCol = csvAddressBook.getProbableColumnForType(CsvAddressBook.TYPE_MOBILE);
            //int secondaryCol = csvAddressBook.getProbableColumnForType(CsvAddressBook.TYPE_EMAIL_SECONDARY);

            emailCol++;
            lastnameCol++;
            firstnameCol++;
            companyCol++;
            mobileCol++;
            //secondaryCol++;

            this.jSpinnerEmailCol.setValue(Math.max(1, emailCol));
            this.jSpinnerLastnameCol.setValue(Math.max(1, lastnameCol));
            this.jSpinnerFirstnameCol.setValue(Math.max(1, firstnameCol));
            this.jSpinnerCompany.setValue(Math.max(1, companyCol));
            this.jSpinnerMobileCol.setValue(Math.max(1, mobileCol));
            //this.jSpinnerSecondaryEmailCol.setValue(secondaryCol);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, "Exception raised: " + ex.toString(), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }
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
                this.doNext();
            }

            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.dispose();
            }

            if (keyCode == KeyEvent.VK_N && e.isControlDown()) {
                doNext();
            }

            if (keyCode == KeyEvent.VK_P && e.isControlDown()) {
                jButtonPreviousActionPerformed(null);
            }

        }
    }

    /**
     * Build the Lists: list names + first names and emails
     */
    private void doNext() {
        try {
            CsvAddressBook csvAddressBook = new CsvAddressBook(file, separator);

            int colEmail = (Integer) jSpinnerEmailCol.getValue() - 1;
            int colLastname = (Integer) jSpinnerLastnameCol.getValue() - 1;
            int colFirstname = (Integer) jSpinnerFirstnameCol.getValue() - 1;
            int colCompany = (Integer) jSpinnerCompany.getValue() - 1;
            int colMobile = (Integer) jSpinnerMobileCol.getValue() - 1;
            //int colSecondaryEmail = (Integer) jSpinnerSecondaryEmailCol.getValue() - 1;

            csvAddressBook.parseFile(colCount);

            List<String> emails = csvAddressBook.getColumnContent(colEmail);

            List<String> names = new Vector<String>();

            if (jCheckBoxNameAndFirstSameColumn.isSelected()) {
                names = csvAddressBook.getColumnContent(colLastname);
            } else {
                List<String> lastnames = csvAddressBook.getColumnContent(colLastname);
                List<String> firstnames = csvAddressBook.getColumnContent(colFirstname);

                for (int i = 0; i < lastnames.size(); i++) {
                    String name = null;
                    if (jCheckBoxDisplayFirstNameBefore.isSelected()) {
                        name = firstnames.get(i) + " " + lastnames.get(i);
                    } else {
                        name = lastnames.get(i) + " " + firstnames.get(i);
                    }
                    names.add(name);
                }
            }

            List<String> companies = csvAddressBook.getColumnContent(colCompany);
            List<String> mobiles = csvAddressBook.getColumnContent(colMobile);

//            List<String> secondaryEmails = new Vector<String>();
//            if (colSecondaryEmail > 0) {
//                secondaryEmails = csvAddressBook.getColumnContent(colSecondaryEmail);
//            }

            // Remove first line if it is field description
            if (jCheckBoxFirstLineIsDesc.isSelected()) {
                names.remove(0);
                emails.remove(0);
                companies.remove(0);
                mobiles.remove(0);

//                if (! secondaryEmails.isEmpty()) {
//                    secondaryEmails.remove(0);
//                }
            }

            List<RecipientEntry> recipientEntries = new ArrayList<RecipientEntry>();

            for (int i = 0; i < emails.size(); i++) {
                String email = emails.get(i);
                String name = names.get(i);
                String company = companies.get(i);
                String mobile = mobiles.get(i);
                String secondaryEmail = null;

//                if (! secondaryEmails.isEmpty()) {
//                    secondaryEmail = secondaryEmails.get(i);
//                }

                // Format mobile by removing special chars
                mobile = MobileUtil.removeSpecialCharacters(mobile);

                RecipientEntry pdfRecipient = new RecipientEntry(email, name, company, mobile, secondaryEmail);
                recipientEntries.add(pdfRecipient);
            }

            this.dispose();
            new AddressBookImportFinal(parent, recipientEntries, file, this.connection, this.userNumber );

        } catch (Exception ex) {
            ex.printStackTrace();;
            JOptionPane.showMessageDialog(rootPane, "Exception raised: " + ex.toString(), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    /*
     * The spinner value must alxats be in [1, 99]
     */
    private void jSpinnerStateChanged(JSpinner jSpinner) {
        if (jSpinner == null) {
            throw new IllegalArgumentException("jSpinner can not be null!");
        }
        Integer nb_passes = (Integer) jSpinner.getValue();

        if (nb_passes < 1) {
            jSpinner.setValue(new Integer(1));
        }

        if (nb_passes > colCount) {
            jSpinner.setValue(colCount);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSepLine2New2 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel23 = new javax.swing.JPanel();
        jPanelSepBlank = new javax.swing.JPanel();
        jPanelEditorPane = new javax.swing.JPanel();
        jPanelSepBlank1 = new javax.swing.JPanel();
        jEditorPane = new javax.swing.JEditorPane();
        jPanelSepBlank2 = new javax.swing.JPanel();
        jPanelSep7 = new javax.swing.JPanel();
        jPanelFile = new javax.swing.JPanel();
        jPanelSepBlank4 = new javax.swing.JPanel();
        jLabeCsvFile = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTextFieldFile = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButtonDisplayFile = new javax.swing.JButton();
        jPanelFileDisplay = new javax.swing.JPanel();
        jPanelCheckBox1 = new javax.swing.JPanel();
        jPanelSepBlank5 = new javax.swing.JPanel();
        jCheckBoxFirstLineIsDesc = new javax.swing.JCheckBox();
        jPanelCheckBox = new javax.swing.JPanel();
        jPanelSepBlank6 = new javax.swing.JPanel();
        jCheckBoxNameAndFirstSameColumn = new javax.swing.JCheckBox();
        jPanelSpinnerEmail = new javax.swing.JPanel();
        jPanelSepBlank7 = new javax.swing.JPanel();
        jSpinnerEmailCol = new javax.swing.JSpinner();
        jPanel7 = new javax.swing.JPanel();
        jLabelEmailCol = new javax.swing.JLabel();
        jPanelSpinnerLastName = new javax.swing.JPanel();
        jPanelSepBlank8 = new javax.swing.JPanel();
        jSpinnerLastnameCol = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        jLabelNameCol = new javax.swing.JLabel();
        jPanelSpinnerFirstName = new javax.swing.JPanel();
        jPanelSepBlank9 = new javax.swing.JPanel();
        jSpinnerFirstnameCol = new javax.swing.JSpinner();
        jPanel9 = new javax.swing.JPanel();
        jLabelFirstCol = new javax.swing.JLabel();
        jPanelSpinnerCompany = new javax.swing.JPanel();
        jPanelSepBlank12 = new javax.swing.JPanel();
        jSpinnerCompany = new javax.swing.JSpinner();
        jPanel11 = new javax.swing.JPanel();
        jLabelCompany = new javax.swing.JLabel();
        jPanelSpinnerMobile = new javax.swing.JPanel();
        jPanelSepBlank10 = new javax.swing.JPanel();
        jSpinnerMobileCol = new javax.swing.JSpinner();
        jPanel10 = new javax.swing.JPanel();
        jLabelMobileCol = new javax.swing.JLabel();
        jPanelCheckBoxInsertName = new javax.swing.JPanel();
        jPanelSepBlank11 = new javax.swing.JPanel();
        jCheckBoxDisplayFirstNameBefore = new javax.swing.JCheckBox();
        jPanelFill = new javax.swing.JPanel();
        jPanelSepBlank3 = new javax.swing.JPanel();
        jPanelSepLine2New = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        jPanel25 = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jButtonPrevious = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 12));

        jLabelTitle.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/icons8-csv-32.png"))); // NOI18N
        jLabelTitle.setText("Importer des Contacts depuis un fichier CSV");
        jPanelNorth.add(jLabelTitle);

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSepLine2New2.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New2.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New2.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel22.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSepLine2New2.add(jPanel22);
        jPanelSepLine2New2.add(jSeparator2);

        jPanel23.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSepLine2New2.add(jPanel23);

        jPanelCenter.add(jPanelSepLine2New2);

        jPanelSepBlank.setMaximumSize(new java.awt.Dimension(50, 10));
        jPanelSepBlank.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSepBlank.setPreferredSize(new java.awt.Dimension(50, 10));

        javax.swing.GroupLayout jPanelSepBlankLayout = new javax.swing.GroupLayout(jPanelSepBlank);
        jPanelSepBlank.setLayout(jPanelSepBlankLayout);
        jPanelSepBlankLayout.setHorizontalGroup(
            jPanelSepBlankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanelSepBlankLayout.setVerticalGroup(
            jPanelSepBlankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSepBlank);

        jPanelEditorPane.setLayout(new javax.swing.BoxLayout(jPanelEditorPane, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepBlank1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank1.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank1Layout = new javax.swing.GroupLayout(jPanelSepBlank1);
        jPanelSepBlank1.setLayout(jPanelSepBlank1Layout);
        jPanelSepBlank1Layout.setHorizontalGroup(
            jPanelSepBlank1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank1Layout.setVerticalGroup(
            jPanelSepBlank1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelEditorPane.add(jPanelSepBlank1);

        jEditorPane.setEditable(false);
        jEditorPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jEditorPane.setText("Nous avons détecté les colonnes correspondant aux Email, Nom, Prénom, N° de Mobile et Email Secondaire. Veuillez confirmer ou modifier les valeurs détectées :");
        jEditorPane.setOpaque(false);
        jPanelEditorPane.add(jEditorPane);

        jPanelSepBlank2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank2.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank2Layout = new javax.swing.GroupLayout(jPanelSepBlank2);
        jPanelSepBlank2.setLayout(jPanelSepBlank2Layout);
        jPanelSepBlank2Layout.setHorizontalGroup(
            jPanelSepBlank2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank2Layout.setVerticalGroup(
            jPanelSepBlank2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelEditorPane.add(jPanelSepBlank2);

        jPanelCenter.add(jPanelEditorPane);

        jPanelSep7.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep7.setPreferredSize(new java.awt.Dimension(421, 5));

        javax.swing.GroupLayout jPanelSep7Layout = new javax.swing.GroupLayout(jPanelSep7);
        jPanelSep7.setLayout(jPanelSep7Layout);
        jPanelSep7Layout.setHorizontalGroup(
            jPanelSep7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
        );
        jPanelSep7Layout.setVerticalGroup(
            jPanelSep7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep7);

        jPanelFile.setLayout(new javax.swing.BoxLayout(jPanelFile, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepBlank4.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank4.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank4Layout = new javax.swing.GroupLayout(jPanelSepBlank4);
        jPanelSepBlank4.setLayout(jPanelSepBlank4Layout);
        jPanelSepBlank4Layout.setHorizontalGroup(
            jPanelSepBlank4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank4Layout.setVerticalGroup(
            jPanelSepBlank4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelFile.add(jPanelSepBlank4);

        jLabeCsvFile.setText("Fichier ");
        jPanelFile.add(jLabeCsvFile);

        jPanel1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel1.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelFile.add(jPanel1);

        jTextFieldFile.setEditable(false);
        jTextFieldFile.setText("jTextFieldFile");
        jTextFieldFile.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldFile.setPreferredSize(new java.awt.Dimension(250, 20));
        jTextFieldFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFileActionPerformed(evt);
            }
        });
        jPanelFile.add(jTextFieldFile);

        jPanel2.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel2.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelFile.add(jPanel2);

        jButtonDisplayFile.setForeground(new java.awt.Color(0, 0, 255));
        jButtonDisplayFile.setText("Afficher le format du fichier");
        jButtonDisplayFile.setBorderPainted(false);
        jButtonDisplayFile.setContentAreaFilled(false);
        jButtonDisplayFile.setFocusPainted(false);
        jButtonDisplayFile.setMargin(new java.awt.Insets(2, 10, 2, 14));
        jButtonDisplayFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonDisplayFileMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonDisplayFileMouseExited(evt);
            }
        });
        jButtonDisplayFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisplayFileActionPerformed(evt);
            }
        });
        jPanelFile.add(jButtonDisplayFile);

        jPanelCenter.add(jPanelFile);

        jPanelFileDisplay.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelFileDisplay.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelFileDisplay.setPreferredSize(new java.awt.Dimension(0, 5));
        jPanelFileDisplay.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));
        jPanelCenter.add(jPanelFileDisplay);

        jPanelCheckBox1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanelSepBlank5.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank5.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank5Layout = new javax.swing.GroupLayout(jPanelSepBlank5);
        jPanelSepBlank5.setLayout(jPanelSepBlank5Layout);
        jPanelSepBlank5Layout.setHorizontalGroup(
            jPanelSepBlank5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank5Layout.setVerticalGroup(
            jPanelSepBlank5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCheckBox1.add(jPanelSepBlank5);

        jCheckBoxFirstLineIsDesc.setText("La première ligne décrit le fichier");
        jCheckBoxFirstLineIsDesc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxFirstLineIsDescItemStateChanged(evt);
            }
        });
        jPanelCheckBox1.add(jCheckBoxFirstLineIsDesc);

        jPanelCenter.add(jPanelCheckBox1);

        jPanelCheckBox.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanelSepBlank6.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank6.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank6Layout = new javax.swing.GroupLayout(jPanelSepBlank6);
        jPanelSepBlank6.setLayout(jPanelSepBlank6Layout);
        jPanelSepBlank6Layout.setHorizontalGroup(
            jPanelSepBlank6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank6Layout.setVerticalGroup(
            jPanelSepBlank6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCheckBox.add(jPanelSepBlank6);

        jCheckBoxNameAndFirstSameColumn.setText("Les noms et prénoms sont dans la même colonne");
        jCheckBoxNameAndFirstSameColumn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxNameAndFirstSameColumnItemStateChanged(evt);
            }
        });
        jPanelCheckBox.add(jCheckBoxNameAndFirstSameColumn);

        jPanelCenter.add(jPanelCheckBox);

        jPanelSpinnerEmail.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanelSepBlank7.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank7.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank7Layout = new javax.swing.GroupLayout(jPanelSepBlank7);
        jPanelSepBlank7.setLayout(jPanelSepBlank7Layout);
        jPanelSepBlank7Layout.setHorizontalGroup(
            jPanelSepBlank7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank7Layout.setVerticalGroup(
            jPanelSepBlank7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSpinnerEmail.add(jPanelSepBlank7);

        jSpinnerEmailCol.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        jSpinnerEmailCol.setToolTipText("");
        jSpinnerEmailCol.setMinimumSize(new java.awt.Dimension(45, 22));
        jSpinnerEmailCol.setPreferredSize(new java.awt.Dimension(45, 22));
        jSpinnerEmailCol.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerEmailColStateChanged(evt);
            }
        });
        jPanelSpinnerEmail.add(jSpinnerEmailCol);

        jPanel7.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel7.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSpinnerEmail.add(jPanel7);

        jLabelEmailCol.setText("Colonne Email");
        jPanelSpinnerEmail.add(jLabelEmailCol);

        jPanelCenter.add(jPanelSpinnerEmail);

        jPanelSpinnerLastName.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanelSepBlank8.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank8.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank8Layout = new javax.swing.GroupLayout(jPanelSepBlank8);
        jPanelSepBlank8.setLayout(jPanelSepBlank8Layout);
        jPanelSepBlank8Layout.setHorizontalGroup(
            jPanelSepBlank8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank8Layout.setVerticalGroup(
            jPanelSepBlank8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSpinnerLastName.add(jPanelSepBlank8);

        jSpinnerLastnameCol.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        jSpinnerLastnameCol.setMinimumSize(new java.awt.Dimension(45, 22));
        jSpinnerLastnameCol.setPreferredSize(new java.awt.Dimension(45, 22));
        jSpinnerLastnameCol.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerLastnameColStateChanged(evt);
            }
        });
        jPanelSpinnerLastName.add(jSpinnerLastnameCol);

        jPanel8.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel8.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSpinnerLastName.add(jPanel8);

        jLabelNameCol.setText("Colonne Nom");
        jPanelSpinnerLastName.add(jLabelNameCol);

        jPanelCenter.add(jPanelSpinnerLastName);

        jPanelSpinnerFirstName.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanelSepBlank9.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank9.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank9Layout = new javax.swing.GroupLayout(jPanelSepBlank9);
        jPanelSepBlank9.setLayout(jPanelSepBlank9Layout);
        jPanelSepBlank9Layout.setHorizontalGroup(
            jPanelSepBlank9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank9Layout.setVerticalGroup(
            jPanelSepBlank9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSpinnerFirstName.add(jPanelSepBlank9);

        jSpinnerFirstnameCol.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        jSpinnerFirstnameCol.setMinimumSize(new java.awt.Dimension(45, 22));
        jSpinnerFirstnameCol.setPreferredSize(new java.awt.Dimension(45, 22));
        jSpinnerFirstnameCol.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerFirstnameColStateChanged(evt);
            }
        });
        jPanelSpinnerFirstName.add(jSpinnerFirstnameCol);

        jPanel9.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel9.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSpinnerFirstName.add(jPanel9);

        jLabelFirstCol.setText("Colonne Prénom");
        jPanelSpinnerFirstName.add(jLabelFirstCol);

        jPanelCenter.add(jPanelSpinnerFirstName);

        jPanelSpinnerCompany.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanelSepBlank12.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank12.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank12Layout = new javax.swing.GroupLayout(jPanelSepBlank12);
        jPanelSepBlank12.setLayout(jPanelSepBlank12Layout);
        jPanelSepBlank12Layout.setHorizontalGroup(
            jPanelSepBlank12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank12Layout.setVerticalGroup(
            jPanelSepBlank12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSpinnerCompany.add(jPanelSepBlank12);

        jSpinnerCompany.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        jSpinnerCompany.setMinimumSize(new java.awt.Dimension(45, 22));
        jSpinnerCompany.setPreferredSize(new java.awt.Dimension(45, 22));
        jSpinnerCompany.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerCompanyStateChanged(evt);
            }
        });
        jPanelSpinnerCompany.add(jSpinnerCompany);

        jPanel11.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel11.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSpinnerCompany.add(jPanel11);

        jLabelCompany.setText("Colonne Société");
        jPanelSpinnerCompany.add(jLabelCompany);

        jPanelCenter.add(jPanelSpinnerCompany);

        jPanelSpinnerMobile.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanelSepBlank10.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank10.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank10Layout = new javax.swing.GroupLayout(jPanelSepBlank10);
        jPanelSepBlank10.setLayout(jPanelSepBlank10Layout);
        jPanelSepBlank10Layout.setHorizontalGroup(
            jPanelSepBlank10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank10Layout.setVerticalGroup(
            jPanelSepBlank10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSpinnerMobile.add(jPanelSepBlank10);

        jSpinnerMobileCol.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        jSpinnerMobileCol.setMinimumSize(new java.awt.Dimension(45, 22));
        jSpinnerMobileCol.setPreferredSize(new java.awt.Dimension(45, 22));
        jSpinnerMobileCol.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerMobileColStateChanged(evt);
            }
        });
        jPanelSpinnerMobile.add(jSpinnerMobileCol);

        jPanel10.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel10.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSpinnerMobile.add(jPanel10);

        jLabelMobileCol.setText("Colonne N° de Mobile");
        jPanelSpinnerMobile.add(jLabelMobileCol);

        jPanelCenter.add(jPanelSpinnerMobile);

        jPanelCheckBoxInsertName.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanelSepBlank11.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank11.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank11Layout = new javax.swing.GroupLayout(jPanelSepBlank11);
        jPanelSepBlank11.setLayout(jPanelSepBlank11Layout);
        jPanelSepBlank11Layout.setHorizontalGroup(
            jPanelSepBlank11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank11Layout.setVerticalGroup(
            jPanelSepBlank11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCheckBoxInsertName.add(jPanelSepBlank11);

        jCheckBoxDisplayFirstNameBefore.setText("Insérer le prénom avant le nom");
        jCheckBoxDisplayFirstNameBefore.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxDisplayFirstNameBeforeItemStateChanged(evt);
            }
        });
        jPanelCheckBoxInsertName.add(jCheckBoxDisplayFirstNameBefore);

        jPanelCenter.add(jPanelCheckBoxInsertName);

        javax.swing.GroupLayout jPanelFillLayout = new javax.swing.GroupLayout(jPanelFill);
        jPanelFill.setLayout(jPanelFillLayout);
        jPanelFillLayout.setHorizontalGroup(
            jPanelFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
        );
        jPanelFillLayout.setVerticalGroup(
            jPanelFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelFill);

        jPanelSepBlank3.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSepBlank3.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelSepBlank3Layout = new javax.swing.GroupLayout(jPanelSepBlank3);
        jPanelSepBlank3.setLayout(jPanelSepBlank3Layout);
        jPanelSepBlank3Layout.setHorizontalGroup(
            jPanelSepBlank3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanelSepBlank3Layout.setVerticalGroup(
            jPanelSepBlank3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSepBlank3);

        jPanelSepLine2New.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New, javax.swing.BoxLayout.LINE_AXIS));

        jPanel24.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSepLine2New.add(jPanel24);
        jPanelSepLine2New.add(jSeparator5);

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

        jPanelSepLine2New.add(jPanel25);

        jPanelCenter.add(jPanelSepLine2New);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonPrevious.setText("< Précédent");
        jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonPrevious);

        jButtonNext.setText("Suivant >");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonNext);

        jButtonClose.setText("Annuler");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonClose);

        jPanel6.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSouth.add(jPanel6);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed

    this.dispose();
}//GEN-LAST:event_jButtonCloseActionPerformed

private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
    doNext();
}//GEN-LAST:event_jButtonNextActionPerformed

private void jButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousActionPerformed
    this.dispose();
    AddressBookImportCsv1 addressBookImport = new AddressBookImportCsv1(parent, connection, userNumber);
}//GEN-LAST:event_jButtonPreviousActionPerformed

private void jButtonDisplayFileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDisplayFileMouseEntered
    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_jButtonDisplayFileMouseEntered

private void jButtonDisplayFileMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDisplayFileMouseExited
    this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_jButtonDisplayFileMouseExited

private void jSpinnerLastnameColStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerLastnameColStateChanged
    jSpinnerStateChanged(jSpinnerLastnameCol);
}//GEN-LAST:event_jSpinnerLastnameColStateChanged

private void jSpinnerFirstnameColStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerFirstnameColStateChanged
    jSpinnerStateChanged(jSpinnerFirstnameCol);
}//GEN-LAST:event_jSpinnerFirstnameColStateChanged

private void jSpinnerEmailColStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerEmailColStateChanged
    jSpinnerStateChanged(jSpinnerEmailCol);
}//GEN-LAST:event_jSpinnerEmailColStateChanged

private void jButtonDisplayFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisplayFileActionPerformed

    new AddressBookImportCsvDisplay(parent, file, separator);
}//GEN-LAST:event_jButtonDisplayFileActionPerformed

private void jCheckBoxNameAndFirstSameColumnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxNameAndFirstSameColumnItemStateChanged
    if (jCheckBoxNameAndFirstSameColumn.isSelected()) {
        jSpinnerFirstnameCol.setEnabled(false);
        jLabelFirstCol.setEnabled(false);
        jCheckBoxDisplayFirstNameBefore.setSelected(false);
        jCheckBoxDisplayFirstNameBefore.setEnabled(false);
    } else {
        jSpinnerFirstnameCol.setEnabled(true);
        jLabelFirstCol.setEnabled(true);
        jCheckBoxDisplayFirstNameBefore.setSelected(true);
        jCheckBoxDisplayFirstNameBefore.setEnabled(true);
    }
}//GEN-LAST:event_jCheckBoxNameAndFirstSameColumnItemStateChanged

private void jCheckBoxFirstLineIsDescItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxFirstLineIsDescItemStateChanged
    // TODO add your handling code here:
}//GEN-LAST:event_jCheckBoxFirstLineIsDescItemStateChanged

private void jCheckBoxDisplayFirstNameBeforeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxDisplayFirstNameBeforeItemStateChanged
    if (jCheckBoxDisplayFirstNameBefore.isSelected()) {
        jCheckBoxDisplayFirstNameBefore.setText(MessagesManager.get("display_firstname_before_lastname") + " (Ex: John Smith)");
    } else {
        jCheckBoxDisplayFirstNameBefore.setText(MessagesManager.get("display_firstname_before_lastname") + " (Ex: Smith John)");
    }
}//GEN-LAST:event_jCheckBoxDisplayFirstNameBeforeItemStateChanged

private void jTextFieldFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFileActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jTextFieldFileActionPerformed

    private void jSpinnerMobileColStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerMobileColStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jSpinnerMobileColStateChanged

    private void jSpinnerCompanyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerCompanyStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jSpinnerCompanyStateChanged

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

                File file = new File("C:\\Users\\Nicolas de Pomereu\\Desktop\\contacts-export.CSV");
                new AddressBookImportCsv2(null, file, null, -1).setVisible(true);

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonDisplayFile;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrevious;
    private javax.swing.JCheckBox jCheckBoxDisplayFirstNameBefore;
    private javax.swing.JCheckBox jCheckBoxFirstLineIsDesc;
    private javax.swing.JCheckBox jCheckBoxNameAndFirstSameColumn;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabeCsvFile;
    private javax.swing.JLabel jLabelCompany;
    private javax.swing.JLabel jLabelEmailCol;
    private javax.swing.JLabel jLabelFirstCol;
    private javax.swing.JLabel jLabelMobileCol;
    private javax.swing.JLabel jLabelNameCol;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCheckBox;
    private javax.swing.JPanel jPanelCheckBox1;
    private javax.swing.JPanel jPanelCheckBoxInsertName;
    private javax.swing.JPanel jPanelEditorPane;
    private javax.swing.JPanel jPanelFile;
    private javax.swing.JPanel jPanelFileDisplay;
    private javax.swing.JPanel jPanelFill;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep7;
    private javax.swing.JPanel jPanelSepBlank;
    private javax.swing.JPanel jPanelSepBlank1;
    private javax.swing.JPanel jPanelSepBlank10;
    private javax.swing.JPanel jPanelSepBlank11;
    private javax.swing.JPanel jPanelSepBlank12;
    private javax.swing.JPanel jPanelSepBlank2;
    private javax.swing.JPanel jPanelSepBlank3;
    private javax.swing.JPanel jPanelSepBlank4;
    private javax.swing.JPanel jPanelSepBlank5;
    private javax.swing.JPanel jPanelSepBlank6;
    private javax.swing.JPanel jPanelSepBlank7;
    private javax.swing.JPanel jPanelSepBlank8;
    private javax.swing.JPanel jPanelSepBlank9;
    private javax.swing.JPanel jPanelSepLine2New;
    private javax.swing.JPanel jPanelSepLine2New2;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelSpinnerCompany;
    private javax.swing.JPanel jPanelSpinnerEmail;
    private javax.swing.JPanel jPanelSpinnerFirstName;
    private javax.swing.JPanel jPanelSpinnerLastName;
    private javax.swing.JPanel jPanelSpinnerMobile;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSpinner jSpinnerCompany;
    private javax.swing.JSpinner jSpinnerEmailCol;
    private javax.swing.JSpinner jSpinnerFirstnameCol;
    private javax.swing.JSpinner jSpinnerLastnameCol;
    private javax.swing.JSpinner jSpinnerMobileCol;
    private javax.swing.JTextField jTextFieldFile;
    // End of variables declaration//GEN-END:variables
}
