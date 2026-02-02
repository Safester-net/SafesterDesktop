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

// IniFileManager.java	: ConfiMail INI File manager

//
// Copyright (c) SafeLogic 2000
//
// Last Updates: 
// 03/02/02 16:45 NDP - creation
// 19/06/02 16:25 NDP - get the ini file from the classpath
// 17/10/02 15:55 NDP - refactor getIniFilename
// 10/02/03 19:25 NDP - if a full filename is in the classpath ==> detects it!
// 21/07/03 12:40 NDP - comments 
// 23/07/03 18:20 NDP - File is also searched in user.home
// 03/12/03 11:20 NDP - store the directory in memory & add getter/setter

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

import com.safelogic.utilx.dir.Directory;

/**
 * Get the full file name, with path, from the raw file name. <br>
 * Search within the filename
 */

public final class IniFileManager {

    /** The Directory containing the file (if it exists) */
    private String m_sDirectory = null;

    /**
     * Constructor
     */

    public IniFileManager() {

    }

    /**
     * 
     * Retrieves a special generic .Ini filename. <br>
     * File is search in the harcoded ini directory <br>
     * 
     * @return A String containing the .ini filename.
     * 
     */

    public String getIniFilename(String sIniFile) throws IOException {

	String sPathSep = new String(System.getProperty("path.separator"));
	String sFilesep = new String(System.getProperty("file.separator"));
	String sUserHome = new String(System.getProperty("user.home"));
	String sClasspath = new String(System.getProperty("java.class.path"));

	StringTokenizer strtk = new StringTokenizer(sClasspath, sPathSep);

	boolean bDebug = false;

	// 23/07/03 18:20 NDP - File is also searched in user.home
	String sFileUserHome = Directory.AddSep(sUserHome) + sIniFile;
	// sFileUserHome = "\"" + sFileUserHome + "\"";

	if (new File(sFileUserHome).exists()) {
	    return sFileUserHome;
	}

	String sTkClasspath = null;

	while (strtk.hasMoreTokens()) {
	    sTkClasspath = strtk.nextToken(sPathSep);

	    if (sTkClasspath.toLowerCase().endsWith(sIniFile.toLowerCase())) {
		int nPos = sTkClasspath.toLowerCase().lastIndexOf(
			sIniFile.toLowerCase());

		String sFile = sTkClasspath.substring(0, nPos) + sIniFile;
		File fClassPath = new File(sFile);

		if (bDebug)
		    System.out.println(fClassPath.toString());

		if (fClassPath.exists()) {
		    String sDirectory = Directory
			    .AddSep(fClassPath.getParent());
		    this.setDirectory(sDirectory);
		    return fClassPath.toString();
		}
	    }

	    File fIni = new File(sTkClasspath + sFilesep + sIniFile);

	    if (bDebug)
		System.out.println(fIni.toString());

	    if (fIni.exists()) {
		String sDirectory = Directory.AddSep(sTkClasspath);
		this.setDirectory(sDirectory);
		return fIni.toString();
	    }
	}

	throw new FileNotFoundException(sIniFile);

    }

    public String getDirectory() {
	return m_sDirectory;
    }

    public void setDirectory(String directory) {
	m_sDirectory = directory;
    }

    // Rule 8: Make your classes noncloneable
    public final Object clone() throws java.lang.CloneNotSupportedException {
	throw new java.lang.CloneNotSupportedException();
    }

    // Rule 9: Make your classes nonserializeable
    private final void writeObject(ObjectOutputStream out)
	    throws java.io.IOException {
	throw new java.io.IOException("Object cannot be serialized");
    }

    // Rule 10: Make your classes nondeserializeable
    private final void readObject(ObjectInputStream in)
	    throws java.io.IOException {
	throw new java.io.IOException("Class cannot be deserialized");
    }
}

// end CmIniFilesManager.java
