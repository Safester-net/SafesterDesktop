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
import java.util.Set;

import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.util.EmailCompletionBuilder;


/**
 * Store in memory the completion list per user. Much better for user experience.
 * @author Nicolas de Pomereu
 */
public class UserCompletionHolder {

    /** The lexicon store for multi accounts */
    private static Map<Integer, Set<String>> LEXICON = new HashMap<Integer, Set<String>>();;
    
    /** The user number */
    private int userNumber = 0;
    
    /** The Jdbc connection */
    private Connection connection = null;

    public UserCompletionHolder(Connection connection, int userNumber) {
        this.connection = connection;
        this.userNumber = userNumber;        
    }

    /**
     * Loads the lexicon once in session
     */
    public void load()
    {
        loadSynchronized(connection, userNumber);
    }
    
    /** Load the lexicon once in session */
    public Set<String> getLexicon()
    {
        load();
        return LEXICON.get(userNumber);
    }

   /**
    * Statis lexicon loader
    * @param connection     the Jdbc connection
    * @param userNumber     the User Number
    */
    private static synchronized void loadSynchronized(Connection connection, int userNumber)
    {
        // May be already loaded
        if (LEXICON.get(userNumber) != null)
        {
            return;
        }

        EmailCompletionBuilder emailCompletionBuilder = new EmailCompletionBuilder(connection, userNumber);

        try {
            LEXICON.put(userNumber,  emailCompletionBuilder.getLexiconSet());
            //System.out.println("lexicon: " + lexicon);
        } catch (SQLException ex) {
            JOptionPaneNewCustom.showException(null, ex);
        }
    }

}
