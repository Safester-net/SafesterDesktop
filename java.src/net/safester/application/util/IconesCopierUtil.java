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
package net.safester.application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * @author Nicolas de Pomereu
 *
 */
public class IconesCopierUtil {

    /**
     * 
     */
    public IconesCopierUtil() {
	// TODO Auto-generated constructor stub
    }

    public static String SIZE_16 = "16x16";
    public static String SIZE_24 = "24x24";
    public static String SIZE_32 = "32x32";
    public static String SIZE_48 = "48x48";
    
    /**
     * @param args
     */
    public static void main(String[] args)  throws Exception {

	/*
	icones_in : C:\Users\Nicolas de Pomereu\Desktop\icones.txt
	in_dir : C:\Users\Nicolas de Pomereu\Documents\Softwares\iconex_g2\g_collection\g_collection_png\gradient
	out_dir: I:\Safester\java.src\net\safester\application\images\files_2
	*/
	
	System.out.println(new Date() + " Begin...");
	File iconFiles = new File("I:\\Safester\\icones.txt");
	File inDir = new File("C:\\Users\\Nicolas de Pomereu\\Documents\\software\\g_iconex\\gradient");
	File outDir = new File("I:\\Safester\\java.src\\net\\safester\\application\\images\\files_2");
	
	List<String> iconNames = getIconNames(iconFiles);
	for (String name : iconNames) {
	    System.out.println(name);
	}
	
	copy(iconNames, inDir, outDir);
	System.out.println(new Date() + " End...");
	
    }

    private static void copy(List<String> iconNames, File inDir, File outDir) throws IOException {
	copy(iconNames, inDir, outDir, SIZE_16);
	copy(iconNames, inDir, outDir, SIZE_24);
	copy(iconNames, inDir, outDir, SIZE_32);
	copy(iconNames, inDir, outDir, SIZE_48);
    }

    private static void copy(List<String> iconNames, File inDir, File outDir,
	    String subDir) throws IOException {

	File inDirFinal = new File(inDir.toString() + File.separator + subDir);
	File outDirFinal = new File(outDir.toString() + File.separator + subDir);
	
	outDirFinal.mkdirs();
	
	for (String name : iconNames) {
	    File fileIn = new File(inDirFinal.toString() + File.separator + name);
	    File fileOut = new File(outDirFinal.toString() + File.separator + name);
	    FileUtils.copyFile(fileIn, fileOut);
	    
	}
    }

    private static List<String> getIconNames(File file) throws IOException {

	try (InputStream in = new FileInputStream(file);) {
	    LineNumberReader lineNumbeRreader = new LineNumberReader(
		    new InputStreamReader(in));
	    String line = null;

	    List<String> lines = new ArrayList<>();
	    while ((line = lineNumbeRreader.readLine()) != null) {
		
		if (line.length() > 1) lines.add(line.trim()+".png");
	    }
	    return lines;
	}

    }

}
