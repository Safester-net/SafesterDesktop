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
package com.safelogic.utilx.io.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * OpenAppend is a tool to dump data in a file in append mode. It is usefull for
 * logs. <br>
 * The file is kept opened ==> so, caller must explicitly close it at end
 */

public class OpenAppend {

    /** The CR LF chars */
    public static final String CR_LF = System.getProperty("line.separator");

    private RandomAccessFile raf_file = null;
    private long l_lgfile;

    /**
     *
     * Open for writing (append mode) into this file.
     *
     **/

    public OpenAppend(File f_file) throws IOException {

	raf_file = new RandomAccessFile(f_file, "rw");

	l_lgfile = raf_file.length();
	raf_file.seek(l_lgfile);

	// NDP - 05/05/98
	// raf_file.writeBytes("\n");

    }

    /**
     *
     * Write the string argument into this object.
     *
     **/

    public void append(String s_chaine) throws IOException {
	raf_file.writeBytes(s_chaine);
    }

    /**
     * Appends the given line to the file. The CR+LF chars are append to the
     * line.
     * 
     * @param sLine
     *            the line to append
     * @exception IOException
     *                if an i/o error occured
     */

    public void appendLine(String sLine) throws IOException {
	append(sLine + CR_LF);
    }

    /**
     * 
     * Returns the file length.
     * 
     **/

    public long getLength() {
	return l_lgfile;
    }

    /**
     * 
     * Closes the file.
     * 
     **/
    public void close() throws IOException {
	raf_file.close();
    }

}
// CSaAppend.java
