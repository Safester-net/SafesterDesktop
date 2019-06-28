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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * Java Security Template for SafeShare.
 * 
 * Java Development Security Guidelines:
 *  <ul>
 *  <li> http://java.sun.com/security/seccodeguide.html
 *  <li> http://www.appperfect.com/support/java-coding-rules/security.html
 *  <li> http://faisalferoz.wordpress.com/2010/10/14/how-to-write-secure-java-code/
 * </ul>
 *
 * Web Security Guidelines:
 * <ul>
 * <li> http://www.dzone.com/links/r/seven_security_misconfigurations_in_java_webxml_f.html
 * <li> http://techchase.in/2009/08/basic-security-check-for-webapplications/
 * </ul>
 * 
 * @author Nicolas de Pomereu
 */

//
// All Client class that declare/use passphrase or login:
// - SafeShareItLogin.java
// - SafeShareItChangePassphrase.java
// - SafeSharePassphraseQuestion*.java
// - ConnectionParms.

//
// All Server class with Master Key access, Login access, etc.
// 
//

// Use final keyword! 
public final class JavaSecurityTemplate {
    
    // Use final keyword! 
    private final char [] login = null;    
    private final char [] password = null;
    
        
    //Rule 8: Make your classes noncloneable
    public final Object clone() throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
    }

    //Rule 9: Make your classes nonserializeable
    private final void writeObject(ObjectOutputStream out)
        throws java.io.IOException {
        throw new java.io.IOException("Object cannot be serialized.");
    }

    //Rule 10: Make your classes nondeserializeable
    private final void readObject(ObjectInputStream in)
        throws java.io.IOException {
        throw new java.io.IOException("Class cannot be deserialized.");
    }
    
}
