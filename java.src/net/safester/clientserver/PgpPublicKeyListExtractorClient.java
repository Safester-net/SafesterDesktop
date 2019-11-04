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
package net.safester.clientserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.safester.clientserver.holder.PublicKeyGetter;
import net.safester.clientserver.specs.ListExtractor;

/**
 * @author Nicolas de Pomereu
 *
 * Extract a List of PgpPublicKey from PC ==> SQL Server
 */
public class PgpPublicKeyListExtractorClient implements ListExtractor<PgpPublicKeyLocal> {

    private static final boolean DEBUG = false;

    /**
     * The Jdbc connection
     */
    private Connection connection = null;

    /**
     * The list of keyIds (aka the raw emails of the public key owners)
     */
    private Set<String> keyIds = null;
    private List<PgpPublicKeyLocal> pgpPublicKeyLocals;

    /**
     * if true, the key ids passed in constructor contains unknonw keys id on
     * server
     */
    private boolean containsUnknownKeysBool = false;

    /**
     * Default Constructor
     *
     * @param connection the JDBC connection
     * @param keyIds the list of keyIds (aka the raw emails of the public key
     * owners)
     *
     */
    public PgpPublicKeyListExtractorClient(Connection connection, Set<String> keyIds)
            throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        if (keyIds == null) {
            throw new IllegalArgumentException("keyIds can\'t be null");
        }

        this.connection = connection;
        this.keyIds = keyIds;

        this.initLists();
    }

    /**
     * Init the lists
     *
     * @throws SQLException
     */
    private void initLists() throws SQLException {

        List<String> keyIdAsList = new ArrayList<>(keyIds);
        Set<String> existingKeyIds = new HashSet<>();

        pgpPublicKeyLocals = new ArrayList<>();

        for (String keyId : keyIdAsList) {
            String publicKeyBlock = new PublicKeyGetter(connection).getPublicKey(keyId);

            if (publicKeyBlock != null && !publicKeyBlock.isEmpty()) {
                existingKeyIds.add(keyId);
                pgpPublicKeyLocals.add(new PgpPublicKeyLocal(publicKeyBlock));
            }

        }

        containsUnknownKeysBool = false;

        for (String keyId : keyIdAsList) {

            if (!existingKeyIds.contains(keyId)) {
                containsUnknownKeysBool = true;
                break;
            }
        }

    }

    /**
     * return a list of PgpPublicKeyLocal instances from the server
     */
    @Override
    public List<PgpPublicKeyLocal> getList() throws SQLException {
        return this.pgpPublicKeyLocals;
    }

    /**
     * @return true if the passed key ids emails list passed in constructor
     * contains keys unknown on the server.
     */
    public boolean containsUnknownKeys() {
        return containsUnknownKeysBool;
    }

    public void debug(String s) {
        if (DEBUG) {
            System.out.println(new Date() + " " + this.getClass().getName() + " " + s);
        }

    }

}
