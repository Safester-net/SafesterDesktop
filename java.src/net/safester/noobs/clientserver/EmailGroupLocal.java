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

import java.util.List;

import net.safester.noobs.clientserver.specs.Local;

/**
 *
 * @author Alexandre Becquereau
 */
public class EmailGroupLocal implements Local {

    private int id;
    private int user_number;
    private String name;

    List<GroupMemberLocal> members;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserNumber() {
        return user_number;
    }

    public void setUserNumber(int userNumber) {
        this.user_number = userNumber;
    }

    public List<GroupMemberLocal> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMemberLocal> members) {
        this.members = members;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "EmailGroupLocal [id=" + id + ", user_number=" + user_number
		+ ", name=" + name + ", members=" + members + "]";
    }


}
