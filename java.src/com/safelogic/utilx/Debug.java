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
package com.safelogic.utilx;

// for debug list file
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.safelogic.utilx.collection.PropertyList;

/**
 * Debug class to allow easy debugging (from ConfiMail)
 */

public class Debug {
    /** the production version flag (set to false for debug) */
    public static final boolean PROD_VERSION = false;

    /** the debug list for Servlets */
    public static final String DEBUG_LIST = "debug_list.ini";

    /* the max size for the loaded debug list */
    // public static final int MAX_SIZE = 20 ;

    /** the line feed */
    private static final String CR_LF = System.getProperty("line.separator");

    // static members

    /**
     * This flag indicates that isSet() must always return true. This is only
     * active if PROD_VERSION = false. (set to true for complete debug)
     */
    public static boolean m_fullDebug = false;

    /** The debug class list */
    private static Hashtable m_hList = null;

    /**
     * Initializes the static content using the debug list at the given File <BR>
     * This has no effect if PROD_VERSION is set to true
     * 
     * @param fDebug
     *            the list file containing the class names to debug
     * @exception IOException
     *                if an I/O error occured
     * @exception MalformedURLException
     *                Bad URL
     */

    public Debug(File fDebug) throws IOException {
	loadList(fDebug);
    }

    /**
     * Initializes the static content using the debug list at the given URL
     * directory. <BR>
     * This has no effect if PROD_VERSION is set to true
     * 
     * @param sRootURL
     *            the base URL where the list file is located
     * @exception IOException
     *                if an I/O error occured
     * @exception MalformedURLException
     *                Bad URL
     */

    public Debug(String sRootURL) throws IOException, MalformedURLException {
	loadList(sRootURL + DEBUG_LIST);
    }

    /**
     * Initializes the static content using the given debug list. <BR>
     * This has no effect if PROD_VERSION is set to true
     * 
     * @param sList
     *            the list as a String array
     */

    public Debug(String[] sList) {
	loadList(sList);
    }

    /**
     * Initializes the static content by indicating to allow or not full debug. <BR>
     * This has no effect if PROD_VERSION is set to true
     */

    public Debug(boolean fullDebug) {
	m_fullDebug = fullDebug;
    }

    /**
     * Checks if the specified class is set in debug mode. This always returns
     * false if this is a production version.
     * 
     * @param oClass
     *            the object whose debug status is asked
     * 
     * @return true if this object is an instance of a class of the debug list,
     *         false otherwise
     */

    public static boolean isSet(Object oClass) {
	if (PROD_VERSION)
	    return false;

	if (m_fullDebug)
	    return true;

	try {
	    return isSet(oClass.getClass().getName());
	} catch (Exception eAll) {
	    return false;
	}
    }
    
    /**
     * Checks if the specified class is set in debug mode. This always returns
     * false if this is a production version.
     * 
     * @param oClass
     *            the object whose debug status is asked
     * 
     * @return true if this object is an instance of a class of the debug list,
     *         false otherwise
     */

    public static boolean isSet(Class<?> theClass) {
	if (PROD_VERSION)
	    return false;

	if (m_fullDebug)
	    return true;

	try {
	    return isSet(theClass.getName());
	} catch (Exception eAll) {
	    return false;
	}
    }    

    /**
     * Checks if the specified class is set in debug mode. This always returns
     * false if this is a production version.
     * 
     * @param sName
     *            the complete name (includes package) whose debug status is
     *            asked
     * 
     * @return true if this object is an instance of a class of the debug list,
     *         false otherwise
     */

    public static boolean isSet(String sName) {
	if (PROD_VERSION || sName == null)
	    return false;

	if (m_fullDebug)
	    return true;

	if (m_hList == null)
	    return false;

	return m_hList.containsKey(sName);
    }

    /**
     * Loads the list of debug class.
     * 
     * @param sList
     *            the list of files
     */

    private static void loadList(String[] sList) {
	if (sList == null) {
	    m_hList = null;
	    return;
	}

	m_hList = new Hashtable();
	for (int n = 0; n < sList.length; n++) {
	    if (sList[n] != null)
		m_hList.put(sList[n], new String());
	}
    }

    /**
     * Loads the list of debug class from the given file URL
     * 
     * @param objAddress
     *            the debug address <br>
     *            It may be: <br>
     *            - a File <br>
     *            - an URL <br>
     *            - an URL in String format (for reverse compatibility)
     * 
     * @exception IOException
     *                if an I/O error occured
     * @exception MalformedURLException
     *                Bad URL
     */
    /*
     * private static void loadList(String sDebugFileURL) throws IOException,
     * MalformedURLException { PropertyList plList = new PropertyList() ;
     * plList.load(new URL(sDebugFileURL)) ; Vector vDebugList =
     * plList.getList() ; m_hList = new Hashtable() ; for(int n = 0 ; n <
     * vDebugList.size() ; n++) { if(vDebugList.elementAt(n) != null)
     * m_hList.put(vDebugList.elementAt(n), new String()) ; }
     * 
     * }
     */

    private static void loadList(Object objAddress) throws IOException,
	    MalformedURLException {
	PropertyList plList = new PropertyList();

	if (objAddress instanceof String) {
	    plList.load(new URL((String) objAddress));
	} else if (objAddress instanceof URL) {
	    plList.load((URL) objAddress);
	} else if (objAddress instanceof File) {
	    plList.load((File) objAddress);
	} else {
	    throw new IllegalArgumentException(
		    "Debug Address Object must be type of String, URL or File: "
			    + objAddress);
	}

	Vector vDebugList = plList.getList();
	m_hList = new Hashtable();
	for (int n = 0; n < vDebugList.size(); n++) {
	    if (vDebugList.elementAt(n) != null)
		m_hList.put(vDebugList.elementAt(n), new String());
	}

    }

    /**
     * Overrides Object.toString() to give specific information :<br>
     * - Production version ;<br>
     * - Debug list.
     * 
     * @return a String representation of this object
     */

    public String toString() {
	return getDisplay();
    }

    /**
     * @return a String representation of this object static state
     */

    public static String getDisplay() {
	String sReturn = new String();

	sReturn += "PROD_VERSION=" + PROD_VERSION + CR_LF;

	if (m_hList == null) {
	    sReturn += "m_hList=null" + CR_LF;
	} else {
	    sReturn += "m_hList=" + CR_LF;
	    sReturn += "{" + CR_LF;
	    Enumeration eList = m_hList.keys();
	    int nCnt = m_hList.size();
	    while (eList.hasMoreElements()) {
		sReturn += " " + eList.nextElement()
			+ ((--nCnt == 0) ? "" : ",") + CR_LF;
	    }

	    sReturn += "}" + CR_LF;
	}

	return sReturn;
    }

    /**
     * Gets the specified exception stack trace as a String.
     * 
     * @param eThrown
     *            the exception
     * @return the exception stack trace
     */

    public static final String GetStackTrace(Exception eThrown) {
	if (eThrown == null)
	    return null;

	try {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintWriter pw = new PrintWriter(baos);
	    eThrown.printStackTrace(pw);
	    pw.close();
	    String sStack = new String(baos.toByteArray());
	    return sStack;
	} catch (Exception eProb) {
	    // hope you can see that in your java console
	    System.out.println("While reporting exception : "
		    + eProb.toString());
	    if (eThrown == null)
		return null;
	    return eThrown.toString();
	}
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
