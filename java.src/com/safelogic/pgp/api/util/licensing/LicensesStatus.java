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
package com.safelogic.pgp.api.util.licensing;

public class LicensesStatus
{
    /** The license value codes THESE ARE SECRET VALUES */     
    public static final int LICENSE_IS_VALID_PRO_TRIAL     =  1;
    public static final int LICENSE_IS_VALID_PRO_PERPETUAL =  2;

    // 1) System errors
    public static final int LICENSE_KEY_FILE_NOT_FOUND     = -1;
    public static final int LICENSE_SIGN_SYSTEM_ERROR      = -2;
    
    // 2) Invalid License errors
    public static final int LICENSE_INVALID_FILE_NOT_FOUND = -101;
    public static final int LICENSE_INVALID_SIGNATURE      = -102; 
    public static final int LICENSE_INVALID_EXPIRED        = -103; 
    public static final int LICENSE_INVALID_NO_MORE_TRIAL  = -104;
    
    // 3) Trial license almost expired return codes
    public static final int LICENSE_5_DAYS_REMAINING       = 5;
    public static final int LICENSE_2_DAYS_REMAINING       = 2;
    public static final int LICENSE_1_DAYS_REMAINING       = 1;
}

