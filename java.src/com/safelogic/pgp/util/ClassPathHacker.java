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
package com.safelogic.pgp.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


public class ClassPathHacker {
    
    private static final Class[] parameters = new Class[]{URL.class};
     
    public static void addFile(String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }//end method
     
    public static void addFile(File f) throws IOException {
        addURL(f.toURL());
    }//end method
     
     
    public static void addURL(URL u) throws IOException {
            
        URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;
     
        try {
            Method method = sysclass.getDeclaredMethod("addURL",parameters);
            method.setAccessible(true);
            method.invoke(sysloader,new Object[]{ u });
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }//end try catch
            
    }//end method
     
    }//end class


