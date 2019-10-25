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
package org.awakefw.sql.api.client;

import java.sql.Connection;

import org.awakefw.commons.jdbc.abstracts.AbstractConnection;
import org.awakefw.file.api.client.AwakeFileSession;

/**
 * Only used as a wrapper of AwakeFileSession.
 * @author Nicolas de Pomereu
 *
 */
public final class AwakeConnection extends AbstractConnection implements Connection {

    private AwakeFileSession AwakeFileSession;

    /**
     * Constructor.
     * @param awakeFileSession
     */
    public AwakeConnection(AwakeFileSession awakeFileSession) {
	if (awakeFileSession == null) {
	    throw new NullPointerException("awakeFileSession is null!");
	}
	
	this.AwakeFileSession = awakeFileSession;
    }

    /**
     * Returns the wrapped AwakeFileSession.
     * @return the wrapped AwakeFileSession
     */
    public AwakeFileSession getAwakeFileSession() {
        return AwakeFileSession;
    }

    /**
     * Returns the username of this Awake File underlying session
     * @return the username of this Awake File underlying session
     */
    public String getUsername() {
	return this.AwakeFileSession.getUsername();
    }
    
    /**
     * Returns the Authentication Token. This method is used by other Awake products
     * (Awake SQL, ...)
     * 
     * @return the Authentication Token
     */
    public String getAuthenticationToken() {
	return this.AwakeFileSession.getAuthenticationToken();
    }

    /**
     * Allows to get a copy of the current <code>AwakeConnection</code>: use it to
     * do some simultaneous operations in a different thread (in order to avoid
     * conflicts).
     */
    @Override
    public AwakeConnection clone() {
	AwakeFileSession awakeFileSession = AwakeFileSession.clone();
	return new AwakeConnection(awakeFileSession);
    }

  
}
