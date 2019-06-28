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
package net.safester.application.parms;

import net.safester.clientserver.ServerParms;

/**
 * Store parameters
 *
 * @author Alexandre Becquereau
 */
public class StoreParms {

    // Products
    public static final short PRODUCT_FREE = 0;
    public static final short PRODUCT_SILVER = 1;
    public static final short PRODUCT_GOLD = 2;
    public static final short PRODUCT_PLATINUM = 3;

    public static final String PRODUCT_NAME_FREE = "FREE";
    public static final String PRODUCT_NAME_SILVER = "SILVER";
    public static final String PRODUCT_NAME_GOLD = "GOLD";
    public static final String PRODUCT_NAME_PLATINUM = "PLATINUM";

    private static final long BODY_LIMIT = 1 * Parms.MO;
    private static final long BODY_LIMIT_FREE = 10 * Parms.KO;

    private static final long ATTACH_LIMIT_FREE = 50 * Parms.MO;
    private static final long ATTACH_LIMIT_SILVER = 200 * Parms.MO;
    private static final long ATTACH_LIMIT_GOLD = 1 * Parms.GO;
    private static final long ATTACH_LIMIT_PLATINUM = 25 * Parms.GO;

    // Keep a non 0 value to avoid java.lang.ArithmeticException: / by zero

    private static final long STORAGE_LIMIT_FREE = 500 * Parms.MO;
    private static final long STORAGE_LIMIT_SILVER = 2 * Parms.GO;
    private static final long STORAGE_LIMIT_GOLD = 5 * Parms.GO;
    private static final long STORAGE_LIMIT_PLATINUM = 25 * Parms.GO;

    private static final int RECIPIENTS_LIMIT_FREE = 8;
    private static final int RECIPIENTS_LIMIT_SILVER = 100;
    private static final int RECIPIENTS_LIMIT_GOLD = 800;
    private static final int RECIPIENTS_LIMIT_PLATINUM = 10000;

    public static final String INVALID_CODE = "invalid_code";
    public static final String EXPIRED_CODE = "expired_code";
    public static final String SYSTEM_ERROR = "system_error";

    public static long getBodyLimitForSubscription(short subscription) {
	if (subscription == PRODUCT_FREE) {
	    return BODY_LIMIT_FREE;
	} else {
	    return BODY_LIMIT;
	}
    }

    public static long getAttachLimitForSubscription(short subscription) {
	switch (subscription) {
	case PRODUCT_FREE:
	    return ATTACH_LIMIT_FREE;
	case PRODUCT_SILVER:
	    return ATTACH_LIMIT_SILVER;
	case PRODUCT_GOLD:
	    return ATTACH_LIMIT_GOLD;
	case PRODUCT_PLATINUM:
	    return ATTACH_LIMIT_PLATINUM;
	default:
	    return ATTACH_LIMIT_FREE;
	}
    }

    public static long getStorageForSubscription(short subscription) {
	switch (subscription) {
	case PRODUCT_FREE:
	    return STORAGE_LIMIT_FREE;
	case PRODUCT_SILVER:
	    return STORAGE_LIMIT_SILVER;
	case PRODUCT_GOLD:
	    return STORAGE_LIMIT_GOLD;
	case PRODUCT_PLATINUM:
	    return STORAGE_LIMIT_PLATINUM;
	default:
	    return STORAGE_LIMIT_FREE;
	}
    }

    public static int getRecipientsLimitForSubscription(short subscription) {
	switch (subscription) {
	case PRODUCT_FREE:
	    return RECIPIENTS_LIMIT_FREE;
	case PRODUCT_SILVER:
	    return RECIPIENTS_LIMIT_SILVER;
	case PRODUCT_GOLD:
	    return RECIPIENTS_LIMIT_GOLD;
	case PRODUCT_PLATINUM:
	    return RECIPIENTS_LIMIT_PLATINUM;
	default:
	    return RECIPIENTS_LIMIT_FREE;
	}
    }

    public static String getProductNameForSubscription(short subscription) {
	switch (subscription) {
	case PRODUCT_FREE:
	    return PRODUCT_NAME_FREE;
	case PRODUCT_SILVER:
	    return PRODUCT_NAME_SILVER;
	case PRODUCT_GOLD:
	    return PRODUCT_NAME_GOLD;
	case PRODUCT_PLATINUM:
	    return PRODUCT_NAME_PLATINUM;
	default:
	    return PRODUCT_NAME_FREE;
	}
    }

    public static String getUrlSilver() {
	String url = ServerParms.getHOST();
	url += "/shop_v3_silver.html";
	return url;
    }

    public static String getUrlGold() {
	String url = ServerParms.getHOST();
	url += "/shop_v3_gold.html";
	return url;
    }

    public static String getUrlPlatinum() {
	String url = ServerParms.getHOST();
	url += "/shop_v3_platinum.html";
	return url;
    }

    public static String getUrlUpgrade() {
	String url = ServerParms.getHOST();
	url += "/shop_v3_upgrade.html";
	return url;
    }

}
