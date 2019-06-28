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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.safelogic.utilx.syntax.EmailChecker;

/**
 * Class that allows to format email addresses in the format: "Name <email@domain.com>"
 * @author SafeLogic
 */
public class EmailUser
{

    /** The name */
    private String name = null;

    /** The email address in SMTP ohn@doe.com format */
    private String emailAddress = null;

    /**
     *
     * Build a new Email User with the user email and the
     * @param nameAndEmailAddress   the full string in format:
     * <br> "name <email@domain.net>"
     *
     * if the string is correct, the string will be spliited into two strings:
     * <ul>
     * <li>the Name</li>
     * <li>the email in the <email@domain.net> format</li>
     *
     */
    public EmailUser(String nameAndEmailAddress)
    {
        if (nameAndEmailAddress == null || nameAndEmailAddress.isEmpty())
        {
            return;
        }

        if (nameAndEmailAddress.contains(" "))
        {
            name = StringUtils.substringBeforeLast(nameAndEmailAddress, " ");
            emailAddress = StringUtils.substringAfterLast(nameAndEmailAddress, " ");
        }
        else
        {
            // No name
            emailAddress = nameAndEmailAddress;
        }

        //System.out.println("emailAddress: " + emailAddress);
        
        if (name != null)
        {
            name = name.trim();
        }

        // security check
        if (emailAddress == null)
        {
            return;
        }
       
        emailAddress = emailAddress.trim();

        String [] boundariesLeftStr = {"<", "(", "["};
        String [] boundariesRightStr = {">", ")", "]"};

        List<String> boundariesLeft = Arrays.asList(boundariesLeftStr);
        List<String> boundariesRight = Arrays.asList(boundariesRightStr);

        for (String boundaryLeft : boundariesLeft) {
            if (emailAddress.contains(boundaryLeft))
            {
                emailAddress = StringUtils.substringAfter(emailAddress, boundaryLeft);
            }
        }

        for (String boundaryRight : boundariesRight) {
            if (emailAddress.contains(boundaryRight))
            {
                emailAddress = StringUtils.substringBeforeLast(emailAddress, boundaryRight);
            }
        }

    }
    
    public static void main(String args[]) {
        EmailUser emailUser = new EmailUser("alex <abecquereau@safelogic.com>");

        System.out.println("EmailUser.getName()         :" + emailUser.getName() + ":");
        System.out.println("EmailUser.getEmailAddress() :" + emailUser.getEmailAddress()  + ":");
    }
    
    /**
     *
     * Build a new Email User with the user email and the 
     * @param name
     * @param emailAddress
     */
    public EmailUser(String name, String emailAddress)
    {
        this.name = name;
        this.emailAddress = emailAddress;
    }

      
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * Test if the email syntax is valid or not.
     * @return
     */
    public boolean isEmailSyntaxValid()
    {
        if (emailAddress == null)
        {
            return false;
        }
        
        EmailChecker emailChecker = new EmailChecker(emailAddress);
        return emailChecker.isSyntaxValid();
    }
    
    /**
     * @return the name + emailAddress in "name <emailAddress>" format
     */
    public String getNameAndEmailAddress()
    {
        if (name == null || name.isEmpty())
        {
            return "<" + emailAddress + ">"; // Only that
        }
        else
        {
            return name.trim() + " " + "<" + emailAddress + ">";
        }
        
    }

    @Override
    public String toString() {
        return getNameAndEmailAddress();
    }


   
}
