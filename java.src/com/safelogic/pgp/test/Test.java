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
package com.safelogic.pgp.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.safelogic.pgp.api.PgpSymActionsOne;

/**
 * @author Nicolas de Pomereu
 * 
 */
public class Test {

    /**
     * 
     */
    public Test() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	PgpSymActionsOne pgpSymActionsOne = new PgpSymActionsOne();

	File inFile = new File("c:\\temp\\boxed-delete.avi");
	File outFile = new File("c:\\temp\\boxed-delete.avi.pgp");

	pgpSymActionsOne.encryptFileSymmetricPgp(inFile, outFile,
		"passphrase".toCharArray());

	InputStream in = pgpSymActionsOne.decryptFileSymmetricPgp(outFile,
		"passphrase".toCharArray());

	FileOutputStream out = new FileOutputStream(new File(
		"c:\\temp\\boxed-delete-2.avi"));

	try {

	    IOUtils.copy(in, out);
	    
	} finally {
	    IOUtils.closeQuietly(in);
	    IOUtils.closeQuietly(out);
	}
	
	System.out.println("done");

    }

}
