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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.awakefw.file.api.util.HtmlConverter;

import net.safester.noobs.clientserver.AddressBookListTransfer;
import net.safester.noobs.clientserver.AddressBookNewLocal;

public class AddressBookUploader {

    //Connection to the db
    private Connection connection;

    //User number of address book's owner
    private int userNumber;

    //Lists of names and emails to import
    private List<RecipientEntry> recipientEntries;

    /**
     * Constructor
     * @param theConnection Connection to db
     * @param theUserNumber  Address book's owner
     * @param recipientEntries
     */
    public AddressBookUploader(Connection theConnection, int theUserNumber, List<RecipientEntry> recipientEntries){
        if(theConnection == null){
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if(recipientEntries == null ){
            throw new IllegalArgumentException("recipientEntries can't be null");
        }
        if(theUserNumber < 0){
                throw new IllegalArgumentException("Invalid user number: " + theUserNumber);
        }

        this.recipientEntries = recipientEntries;
        this.connection = theConnection;
        this.userNumber = theUserNumber;

    }

    /**
     * Import data on server
     * @throws SQLException
     */
    public void importOnServer() throws SQLException{

        AddressBookListTransfer addressBookListTransfer = new AddressBookListTransfer(connection, userNumber);
        
        //Get current max id of address book record for user
        int maxId = addressBookListTransfer.getMaxId();

        //Get current list of addressBook
        List<AddressBookNewLocal> addressBookLocals = new ArrayList<>();
       
        for (int i = 0; i< recipientEntries.size(); i++) {
            String name = recipientEntries.get(i).getName();
            String email = recipientEntries.get(i).getEmailAddress();
            String company = recipientEntries.get(i).getCompany();
            String cellPhone = recipientEntries.get(i).getMobile();
            
            name = cutField(name, 128);
            email = cutField(email, 254);
            company = cutField(company, 128);
            cellPhone = cutField(cellPhone, 64);
            
            //Create addressBookLocal object
            AddressBookNewLocal addressBookLocal = new AddressBookNewLocal();
            addressBookLocal.setEmail(email);
            addressBookLocal.setName(HtmlConverter.toHtml(name));
            addressBookLocal.setCompany(HtmlConverter.toHtml(company));
            addressBookLocal.setCellPhone(cellPhone);
            
            addressBookLocal.setUserNumber(userNumber);
            addressBookLocal.setAddressBookId(maxId++);
            addressBookLocals.add(addressBookLocal);
        }

        //transfer list to server
        addressBookListTransfer.putList(addressBookLocals);
    }

    public String cutField(String field, int length) {
        if (field != null && field.length() > length) {
            field = field.substring(0, length - 1);
        }
        return field;
    }
}
