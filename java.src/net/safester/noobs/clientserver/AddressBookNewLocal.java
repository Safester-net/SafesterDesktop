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
package net.safester.noobs.clientserver;

import net.safester.noobs.clientserver.specs.Local;

/**
 * @author Nicolas de Pomereu
 * Defines an instance of AddressBookNew
 * <br>
 */ 

public class AddressBookNewLocal implements Local, Comparable<AddressBookNewLocal>
{		
    
	/** Table columns */
    
    private int user_number;
    private int address_book_id;
    private String name;
    private String email;  
    private String company; 
    private String cell_phone; 

    /**
     * Constructor (Void)
     */
    public AddressBookNewLocal()
    {
        // Void Constructor
    }    
    
    /** Return field value */
    public int getUserNumber()
    {
        return this.user_number;
    }
    /** Return field value */
    public int getAddressBookId()
    {
        return this.address_book_id;
    }
    /** Return field value */
    public String getName()
    {
        return this.name;
    }
    /** Return field value */
    public String getEmail()
    {
        return this.email;
    }
        
    
    /** Set field value */
    public void setUserNumber(int user_number)
    {
        this.user_number = user_number;
    } 
    /** Set field value */
    public void setAddressBookId(int address_book_id)
    {
        this.address_book_id = address_book_id;
    } 
    /** Set field value */
    public void setName(String name)
    {
        this.name = name;
    } 
    /** Set field value */
    public void setEmail(String email)
    {
        this.email = email;
    }

    
    
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCellPhone() {
        return cell_phone;
    }

    public void setCellPhone(String cell_phone) {
        this.cell_phone = cell_phone;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "AddressBookLocal [address_book_id=" + address_book_id
                + ", email=" + email + ", name=" + name + ", user_number="
                + user_number + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
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
	AddressBookNewLocal other = (AddressBookNewLocal) obj;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	return true;
    } 
    
    @Override
    public int compareTo(AddressBookNewLocal o) {
        
        if (name == null && o == null) {
            return 0;
        }
        
        if (name == null) {
            return - 1;
        }
        
        if (o == null) {
            return 1;
        }
        
        return name.toUpperCase().compareTo(o.getName().toUpperCase());
    }
    
    
       
} // EOF
 
