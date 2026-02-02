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
package com.safelogic.pgp.api.toolkit.xmlrpc.util;

//LoggingOutputStream
//Copyright (c) SafeLogic, 2000 - 2009
//
//Last Updates: 
//3 nov. 2009 17:38:23 Nicolas de Pomereu
//
//Code found here: http://blogs.sun.com/nickstephen/entry/java_redirecting_system_out_and 
//

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * An OutputStream that writes contents to a Logger upon each call to flush()
 */
public class LoggingOutputStream extends ByteArrayOutputStream {
    
    private String lineSeparator;
    
    private Logger logger;
    private Level level;
    
    /**
     * Constructor
     * @param logger Logger to write to
     * @param level Level at which to write the log message
     */
    public LoggingOutputStream(Logger logger, Level level) {
        super();
        this.logger = logger;
        this.level = level;
        lineSeparator = System.getProperty("line.separator");
    }
    
    /**
     * upon flush() write the existing contents of the OutputStream to the logger as 
     * a log record.
     * @throws java.io.IOException in case of error
     */
    public void flush() throws IOException {

        String record;
        synchronized(this) {
            super.flush();
            record = this.toString();
            super.reset();
        }
        
        if (record.length() == 0 || record.equals(lineSeparator)) {
            // avoid empty records
            return;
        }

        logger.logp(level, "", "", record);
    }
}
