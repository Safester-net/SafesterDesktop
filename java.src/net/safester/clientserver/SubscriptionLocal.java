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
package net.safester.clientserver;

import java.sql.Timestamp;

public class SubscriptionLocal {
   
    private int userNumber = -1;
    private int typeSubscription = -1;
    private Timestamp enddate = null;
    
    public int getUserNumber() {
        return userNumber;
    }
    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }
    public int getTypeSubscription() {
        return typeSubscription;
    }
    public void setTypeSubscription(int typeSubscription) {
        this.typeSubscription = typeSubscription;
    }
    public Timestamp getEnddate() {
        return enddate;
    }
    public void setEnddate(Timestamp enddate) {
        this.enddate = enddate;
    }

    @Override
    public String toString() {
        return "SubscriptionLocal{" + "userNumber=" + userNumber + ", typeSubscription=" + typeSubscription + ", enddate=" + enddate + '}';
    }
  
    
}
