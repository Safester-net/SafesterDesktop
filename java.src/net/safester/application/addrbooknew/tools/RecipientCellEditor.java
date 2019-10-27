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
package net.safester.application.addrbooknew.tools;

/*
 * RecipientCellEditor is used by TableFTFEditDemo.java.
 */


import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.safelogic.utilx.Debug;

import net.safester.application.messages.MessagesManager;

/**
 * Implements a cell editor that uses a formatted text field
 * to edit Integer values.
 */
public class RecipientCellEditor extends DefaultCellEditor {
        
    JFormattedTextField ftf;
    NumberFormat integerFormat;
    //private Integer minimum, maximum;
    
    public boolean DEBUG = Debug.isSet(this);
    
    /** The celtype is CELL_TYPE_MOBILE or CELL_TYPE_EMAIL */
    public int cellIndex;

    private String mobileErrorMessage = null;
    
    /**
     * Constructor.
     * @param theCellIndex says which cell type control is on : mobile number (CELL_TYPE_MOBILE) or
     * email address (CELL_TYPE_EMAIL)
     */
    public RecipientCellEditor(int theCellIndex) {
        super(new JFormattedTextField());
        ftf = (JFormattedTextField)getComponent();
       
        DefaultFormatter format = new DefaultFormatter();
        format.setOverwriteMode(false);
        
        ftf.setFormatterFactory(
                new DefaultFormatterFactory(format));
        
        this.cellIndex = theCellIndex;
                
        /*
        minimum = new Integer(min);
        maximum = new Integer(max);

        //Set up the editor for the integer cells.
        integerFormat = NumberFormat.getIntegerInstance();
        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        intFormatter.setFormat(integerFormat);
        intFormatter.setMinimum(minimum);
        intFormatter.setMaximum(maximum);

        ftf.setFormatterFactory(
                new DefaultFormatterFactory(intFormatter));
        ftf.setValue(minimum);
        */
        
//        if (cellIndex == 2) {
//            MaskFormatter maskFormatter = null;
//            try {
//                maskFormatter = new MaskFormatter("## ## ## ## ##");
//            } catch (ParseException ex) {
//                Logger.getLogger(RecipientCellEditor.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            ftf = new JFormattedTextField(maskFormatter);
//            
//        }
        
        ftf.setHorizontalAlignment(JTextField.LEADING);
        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField'value focusLostBehavior property.)
        ftf.getInputMap().put(KeyStroke.getKeyStroke(
                                        KeyEvent.VK_ENTER, 0),
                                        "check");
        ftf.getActionMap().put("check", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String value = ftf.getText();
                //JOptionPane.showMessageDialog(null, "value: " + value);
                
                boolean isValid = false;
                
                
                if (cellIndex == 3) {
                    /*
                    value = MobileUtil.removeSpecialCharacters(value);
                    
                    MobilePhoneValidator mobilePhoneValidator = new MobilePhoneValidator(value);
                    isValid = mobilePhoneValidator.isValid();
                    mobileErrorMessage = mobilePhoneValidator.getErrorMessage();
                    */
                }
                else if (cellIndex == 0 || cellIndex == 4) {
                    isValid = CryptAppUtil.isValidEmailAddress(value);
                }
                
                
                // Allow empty cells
                if (value.isEmpty() && cellIndex != 0) {
                    isValid = true;
                }
                
		if (! isValid) { //The text is invalid.
                    if (userSaysRevert()) { //reverted
		        ftf.postActionEvent(); //inform the editor
		    }
                } else try {              //The text is valid,
                    ftf.setText(value);
                    ftf.commitEdit();     //so use it.
                    ftf.postActionEvent(); //stop editing
                } catch (java.text.ParseException exc) { }
            }
        });
    }

    //Override to invoke setValue on the formatted text field.
    
    @Override
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected,
            int row, int column) {
        JFormattedTextField ftf =
            (JFormattedTextField)super.getTableCellEditorComponent(
                table, value, isSelected, row, column);
       
        ftf.setValue(value);
        return ftf;
    }
    

    //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
        Object o = ftf.getValue();
        
        return o;
    }

    //Override to check whether the edit is valid,
    //setting the value if it is and complaining if
    //it isn't.  If it'value OK for the editor to go
    //away, we need to invoke the superclass'value version 
    //of this method so that everything gets cleaned up.
    
//    public boolean stopCellEditing() {
//        JFormattedTextField ftf = (JFormattedTextField)getComponent();
//       
//        if (ftf.isEditValid()) {
//            try {
//                ftf.commitEdit();
//            } catch (java.text.ParseException exc) { }
//	    
//        } else { //text is invalid
//            if (!userSaysRevert()) { //user wants to edit
//	        return false; //don't let the editor go away
//	    } 
//        }
//        return super.stopCellEditing();
//    }

    /** 
     * Lets the user know that the text they entered is 
     * bad. Returns true if the user elects to revert to
     * the last good value.  Otherwise, returns false, 
     * indicating that the user wants to continue editing.
     */
    protected boolean userSaysRevert() {
        Toolkit.getDefaultToolkit().beep();
        ftf.selectAll();
        
        Object[] options = {MessagesManager.get("system_edit"), MessagesManager.get("system_cancel")};
        
        String continueOrCancel = MessagesManager.get("continue_or_cancel");
        
         String messageTypeMobile =  mobileErrorMessage + "\n"
                 + continueOrCancel;
        
         String messageTypeEmail = MessagesManager.get("you_must_enter_a_valid_email") + "\n" + continueOrCancel;
         
         String message = null;
         
        if (cellIndex == 3) {
            message = messageTypeMobile;
        } else {
            message = messageTypeEmail;
        }
         
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(ftf), message,
            MessagesManager.get("invalid_input_value"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            options[1]);
	    
        if (answer == 1) { //Revert!
            ftf.setValue(ftf.getValue());
	    return true;
        }
	return false;
    }
}
