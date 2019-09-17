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
package net.safester.clientserver.holder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.awakefw.sql.api.client.AwakeConnection;


import net.safester.clientserver.PgpKeyPairLocal;

/*
 * @author Nicolas de Pomereu
 *
 *         Store in memory the key pair. Much better for user experience.
 *
 */
public class PgpKeyPairHolder {

    public static boolean DEBUG = true;

    /**
     * The static user settings in memory
     */
    private static Map<Integer, PgpKeyPairLocal> pgpKeyPairLocalMap = new HashMap<Integer, PgpKeyPairLocal>();

    /**
     * The Jdbc connection
     */
    private Connection connection = null;

    /**
     * The user number
     */
    private int userNumber = 0;

    /**
     * Constructor for pgp_key table
     *
     * @param connection the JDBC connection
     * @param userNumber the user numberr
     */
    public PgpKeyPairHolder(Connection theConnection, int userNumber) {
	if (theConnection == null) {
	    throw new IllegalArgumentException("Connection can\'t be null");
	}

	// Use a dedicated Connection to avoid overlap of result files
	if (theConnection instanceof AwakeConnection) {
	    this.connection = ((AwakeConnection) theConnection).clone();
	} else {
	    this.connection = theConnection;
	}

	this.userNumber = userNumber;
    }

    /**
     * Load the User Settings
     *
     * @throws SQLException
     */
    public void load() throws SQLException {
	loadSynchronized(connection, userNumber);
    }

    /**
     * Gets the PGP Key Pair from the server using new API.
     *
     * @param connection
     * @param userNumber
     * @return
     * @throws SQLException
     */
    public static PgpKeyPairLocal getFromServer(Connection connection, int userNumber) throws SQLException {

	String privateKeyBlock = new PrivateKeyGetter(connection).getPrivateKey(userNumber);
	String publicKeyBlock = new PublicKeyGetter(connection).getPublicKey(userNumber);

        // For master key, we accept unknown private key block for now
        if (privateKeyBlock == null) {
            privateKeyBlock = "**UNKNOWN**";
        }
        
	PgpKeyPairLocal pgpKeyPairLocal = new PgpKeyPairLocal(privateKeyBlock, publicKeyBlock);

	return pgpKeyPairLocal;
    }

    /**
     * Load with synchronisation
     *
     * @param connection the JDBC connection
     * @param userNumber the user numberr
     * @throws SQLException
     */
    private static synchronized void loadSynchronized(Connection connection, int userNumber) throws SQLException {
	if (pgpKeyPairLocalMap.get(userNumber) == null) {

	    PgpKeyPairLocal pgpKeyPairLocal = getFromServer(connection, userNumber);
	    pgpKeyPairLocalMap.put(userNumber, pgpKeyPairLocal);
	}
    }

    /**
     *
     * @return the user settings (from memory if available)
     * @throws SQLException
     */
    public PgpKeyPairLocal get() throws SQLException {
	load();
	return pgpKeyPairLocalMap.get(userNumber);
    }

    /**
     * Reset (when the user modify his settings)
     */
    public void reset() throws SQLException {
	pgpKeyPairLocalMap = new HashMap<Integer, PgpKeyPairLocal>();
	load();
    }

    @SuppressWarnings("unused")
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new java.util.Date() + " " + s);
	}
    }

}
