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
package net.safester.application.addrbooknew;

import java.io.Serializable;


/**
 * The components of a row of adress book in a tables
 *
 * @author Nicolas de Pomereu
 */
public class RecipientEntry implements Serializable {


    public static final String CSV_SEPARATOR = ";";

    private static final long serialVersionUID = 7716970238358721463L;

    private String emailAddress = null;
    private String name = null;
    private String company = null;
    private String mobile = null;

    private String emailNotify = null;

    /**
     * Constructor. Parameters are supposed to be valid, as this class is used
     * for loadings & imports emailNotify is not used anymore, keep it if it
     * comes back
     *
     * @param emailAddress
     * @param name
     * @param company
     * @param mobile
     * @param emailNotify
     */
    public RecipientEntry(String emailAddress, String name, String company, String mobile, String emailNotify) {

        if (emailAddress == null) {
            throw new NullPointerException("emailAddress est null!");
        }

        this.emailAddress = removeSeparators(emailAddress);
        this.name = removeSeparators(name);
        this.company = company;
        this.mobile = removeSeparators(mobile);
        this.emailNotify = removeSeparators(emailNotify);

        this.emailAddress = this.emailAddress.trim();
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @return the emailNotify
     */
    public String getEmailNotify() {

        return emailNotify;
    }

    /**
     * Build a PdfRcipient from a / separated String with : emailAddress / Name
     * / Mobile
     *
     * @param recipientString
     * @return the RecipientEntry
     */
    public static RecipientEntry buildFromString(String recipientString)  {

        if (recipientString == null) {
            throw new NullPointerException("recipientString is null");
        }
        
        String[] elements = recipientString.split("/");

        String emailAddress = "";
        String name = "";
        String company = "";
        String mobile = "";
        
        if (elements != null && elements.length == 3) {
            emailAddress = elements[0];
            name = elements[1];
            mobile = elements[2];
        }
        
        if (elements != null && elements.length == 4) {
            emailAddress = elements[0];
            name = elements[1];
            mobile = elements[2];
            company = elements[3];
        }
                
        RecipientEntry pdfRecipient = new RecipientEntry(emailAddress, name, company, mobile, null);
        return pdfRecipient;

    }
    
        

    /**
     * Remove "," and ";" from string
     *
     * @param string
     * @return
     */
    private String removeSeparators(String string) {
                
        if (string == null || string.isEmpty()) {
            string = "";
            return string;
        }

        string = string.replace(CSV_SEPARATOR, " ");
        string = string.replace(";", " ");

        return string;
    }

    /**
     * Setter because company may be defined later
     * @param company the company to set
     */
    public void setCompany(String company) {
        this.company = company;
    }
    
    

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	RecipientEntry other = (RecipientEntry) obj;
	if (emailAddress == null) {
	    if (other.emailAddress != null)
		return false;
	} else if (!emailAddress.equals(other.emailAddress))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "RecipientEntry [emailAddress=" + emailAddress + ", name=" + name
		+ ", company=" + company + ", mobile=" + mobile
		+ ", emailNotify=" + emailNotify + "]";
    }


}
