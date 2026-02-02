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
package com.safelogic.pgp.api.toolkit.test;

import java.util.Scanner;


public class ScannerDemo {
    public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    
    //
    // Read string input for username
    //
    System.out.print("Username: ");
    String username = scanner.nextLine();
    
    //
    // Read string input for password
    //
    System.out.print("Password: ");
    String password = scanner.nextLine();
    
    //
    // Read an integer input for another challenge
    //
    System.out.print("What is 2 + 2: ");
    int result = scanner.nextInt();
    
    if (username.equals("admin") && password.equals("secret") && result == 4) {
        System.out.println("Welcome to Java Application");
    } else {
        System.out.println("Invalid username or password, access denied!");     
    }
    }
}
