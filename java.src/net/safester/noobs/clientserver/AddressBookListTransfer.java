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
package net.safester.noobs.clientserver;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.safester.application.parms.CryptoParms;
import net.safester.clientserver.specs.ListTransfer;

/**
 * @author Nicolas de Pomereu
 *
 * Transfer an AddressBookLocal list (aka all the contacts for a user) between
 * PC <==> SQL Server.
 */
public class AddressBookListTransfer implements ListTransfer<AddressBookNewLocal> {

    public static boolean DEBUG = true;
        
    /**
     * The Jdbc connection
     */
    private Connection connection = null;

    /**
     * The user number
     */
    private int userNumber = 0;


    /**
     * Default Constructor.
     * <br>
     *
     * @param userNumber the user number of the address book holder
     * @param connection the JDBC connection
     */
    public AddressBookListTransfer(Connection connection, int userNumber) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        this.connection = connection;
        this.userNumber = userNumber;
    }

    /**
     * Get an AddressBookLocal list from the server using the criteria: User
     * Number
     */
    @Override
    public List<AddressBookNewLocal> getList()
            throws SQLException {
        /*
        AddressBookNew addressBook = new AddressBookNew();
        List<AddressBookNew> addressBooks = addressBook.find(
                connection,
                "WHERE user_number = ? ORDER BY address_book_id ASC",
                userNumber);

        List<AddressBookNewLocal> addressBookLocalList = new Vector<AddressBookNewLocal>();

        for (AddressBookNew theAddressBook : addressBooks) {
            AddressBookNewLocal addressBookLocal = new AddressBookNewLocal();
            addressBookLocal.setUserNumber(userNumber);
            addressBookLocal.setAddressBookId(theAddressBook.getAddressBookId());
            addressBookLocal.setEmail(theAddressBook.getEmail());
            addressBookLocal.setName(theAddressBook.getName());
            addressBookLocal.setCompany(theAddressBook.getCompany());
            addressBookLocal.setCellPhone(theAddressBook.getCellPhone());
            addressBookLocalList.add(addressBookLocal);
        }

        decrypt(addressBookLocalList);
        
        Collections.sort(addressBookLocalList);
        
        return addressBookLocalList;
        */
        

        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        String keyId = awakeFileSession.getUsername();

        String jsonString = null;
        try {
            jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.AddressBookNewApi.getList",  userNumber, keyId, connection);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        
        Gson gsonOut = new Gson();
        Type listOfAddressBookLocal = new TypeToken<List<AddressBookNewLocal>>() {
        }.getType();
        List<AddressBookNewLocal> addressBookLocalList = gsonOut.fromJson(jsonString, listOfAddressBookLocal);
        
        decrypt(addressBookLocalList);
        Collections.sort(addressBookLocalList);
        
        return addressBookLocalList;
                
        
    }

     /**
     * Add new entries to remote address book on the server using the criteria: User Number
     *
     * @param addressBookLocalList
     */
    public void putListForAdd(List<AddressBookNewLocal> addressBookLocalList)
            throws SQLException {
        formatForServer(addressBookLocalList);

        String jsonString = GsonUtilAddressBookLocal.listToGson(addressBookLocalList);

        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try {
            awakeFileSession.call("net.safester.server.AdressBookNewImporter.addAddressBook", jsonString, userNumber, connection);
        } catch (Exception e) {
            throw new SQLException(e);
        }

    }
    /**
     * Put AddressBookLocal list on the server using the criteria: User Number
     *
     * @param addressBookLocalList
     */
    @Override
    public void putList(List<AddressBookNewLocal> addressBookLocalList)
            throws SQLException {
        
        formatForServer(addressBookLocalList);

        String jsonString = GsonUtilAddressBookLocal.listToGson(addressBookLocalList);

        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        debug("putList:jsonString: " + jsonString);
        
        try {
            awakeFileSession.call("net.safester.server.AdressBookNewImporter.importAddressBook", jsonString, userNumber, connection);
        } catch (Exception e) {
            throw new SQLException(e);
        }

    }

    public void formatForServer(List<AddressBookNewLocal> addressBookLocalList) throws SQLException {
        for (AddressBookNewLocal addressBookNewLocal : addressBookLocalList) {
            try {
                String name = addressBookNewLocal.getName();
                String email = addressBookNewLocal.getEmail();
                String company = addressBookNewLocal.getCompany();
                String cellPhone = addressBookNewLocal.getCellPhone();

                if (CryptoParms.DO_ENCRYPT_ADDRESS_BOOK) {
                    // Email is never encrypted beause it requires JOIN for photos
                    //name = AddressBookEncryption.encrypt(name);
                    //company = AddressBookEncryption.encrypt(company);
                    //cellPhone = AddressBookEncryption.encrypt(cellPhone);
                }

                name = HtmlConverter.toHtml(name);
                company = HtmlConverter.toHtml(company);
                
                addressBookNewLocal.setName(name);
                addressBookNewLocal.setEmail(email);
                addressBookNewLocal.setCompany(company);
                addressBookNewLocal.setCellPhone(cellPhone);

            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    public void decrypt(List<AddressBookNewLocal> addressBookLocalList) throws SQLException {
        for (AddressBookNewLocal addressBookNewLocal : addressBookLocalList) {
            try {
                String name = addressBookNewLocal.getName();
                String email = addressBookNewLocal.getEmail();
                String company = addressBookNewLocal.getCompany();
                String cellPhone = addressBookNewLocal.getCellPhone();

                //try {name = AddressBookEncryption.decrypt(name); } catch (Exception ignore) { }
                //try {company = AddressBookEncryption.decrypt(company); } catch (Exception ignore) { }
                //try {cellPhone = AddressBookEncryption.decrypt(cellPhone); } catch (Exception ignore) { }

                addressBookNewLocal.setName(name);
                addressBookNewLocal.setEmail(email);
                addressBookNewLocal.setCompany(company);
                addressBookNewLocal.setCellPhone(cellPhone);

            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    public int getMaxId()
            throws SQLException {
	
	int maxId = 0;
	
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try {
            String maxIdStr = awakeFileSession.call("net.safester.server.hosts.newapi.AddressBookNewApi.getMaxId", userNumber, connection);
            maxId = Integer.parseInt(maxIdStr);
            return maxId;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private void debug(String string) {
        if (DEBUG) {
            System.out.println ( new Date() + " " + string);
        }
    }
}
