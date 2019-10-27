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

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.safester.clientserver.specs.ListTransfer;

/**
 * @author Nicolas de Pomereu
 *
 * Transfer an PhotoAddressBookLocal list (aka all the contacts for a user)
 * between PC <==> SQL Server.
 */
public class PhotoAddressBookListTransfer implements
        ListTransfer<PhotoAddressBookLocal>, Comparable<PhotoAddressBookLocal> {

    /**
     * The Jdbc connection
     */
    private Connection connection = null;

    /**
     * The user number
     */
    private int userNumber = 0;

    /**
     * Default Constructor. <br>
     *
     * @param userNumber the user number of the address book holder
     * @param connection the JDBC connection
     */
    public PhotoAddressBookListTransfer(Connection connection, int userNumber) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        this.connection = connection;
        this.userNumber = userNumber;
    }

    /**
     * Get an PhotoAddressBookLocal list from the server using the criteria:
     * User Number
     */
    @Override
    public List<PhotoAddressBookLocal> getList() throws SQLException {

        return getListSecure(connection, userNumber);
    }

    /**
     * @return @throws SQLException
     */
    private static synchronized List<PhotoAddressBookLocal> getListSecure(
            Connection connection, int userNumber) throws SQLException {

        // Returns the cache values if not null
        if (UsersAddressBookCache.get() != null) {
            return UsersAddressBookCache.get();
        }

        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        String jsonString = null;
        
        try {
            jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.PhotoAddressBookNewApi.getListSecure",
                    userNumber,
                    connection);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        
        Gson gsonOut = new Gson();
        Type listOfAddressBookLocal = new TypeToken<List<PhotoAddressBookLocal>>() {
        }.getType();
        List<PhotoAddressBookLocal> addressBookLocalList = gsonOut.fromJson(jsonString, listOfAddressBookLocal);
        
        decrypt(addressBookLocalList) ;

        Collections.sort(addressBookLocalList);
        
        // Put in the cache
        UsersAddressBookCache.put(addressBookLocalList);

        return addressBookLocalList;
    }

    private static void decrypt(List<PhotoAddressBookLocal> addressBookLocalList) throws SQLException {
        for (PhotoAddressBookLocal addressBookNewLocal : addressBookLocalList) {
            try {
                String name = addressBookNewLocal.getName();
                String email = addressBookNewLocal.getEmail();
                String company = addressBookNewLocal.getCompany();
                String cellPhone = addressBookNewLocal.getCell_phone();

                //try {name = AddressBookEncryption.decrypt(name); } catch (Exception ignore) { }
                //try {company = AddressBookEncryption.decrypt(company); } catch (Exception ignore) {}
                //try {cellPhone = AddressBookEncryption.decrypt(cellPhone); } catch (Exception ignore) {}
                                
                name = HtmlConverter.fromHtml(name);
                company = HtmlConverter.fromHtml(company);
                
                addressBookNewLocal.setName(name);
                addressBookNewLocal.setEmail(email);
                addressBookNewLocal.setCompany(company);
                addressBookNewLocal.setCell_phone(cellPhone);
                
                //System.out.println("addressBookNewLocal: " + addressBookNewLocal);

            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    /**
     * Get from the address book only the users who really exists on the system.
     * Does not take into account adresses without a real Safester account. <br>
     * The method tries to get the values from the cache for fast access.
     * (Method is called at each time a user wants to share a folder in Safester
     * Vault with FolderShareFrame
     *
     */
    /*    
    public List<PhotoAddressBookLocal> getExistingUsersList()
	    throws SQLException {

	return getExistingUsersListSecure(connection, userNumber);
    }
   
     */

    /**
     * Put PhotoAddressBookLocal list on the server using the criteria: User
     * Number
     */
    @Override
    public void putList(List<PhotoAddressBookLocal> addressBookLocalList)
            throws SQLException {

        throw new SQLException("Not Implemented. Use AddressBookListTransfer.putList()");
    }

    @Override
    public int compareTo(PhotoAddressBookLocal o) {
	// TODO Auto-generated method stub
	return 0;
    }

}
