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
package com.kawansoft.crypt.util.sms;

import com.kawansoft.crypt.sms.MobilePhoneValidator;
import com.kawansoft.crypt.sms.MobileUtil;


/**
 *
 * @author Nicolas de Pomereu
 */
public class PhoneCountryLookupTest {
    
    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {
	// displayPossibleRegions();
        
	String phoneNumberBahamas = "+12423223301";
	String phoneNumberParis = "+33623261275";
	String phoneNumberMontreal = "+15148400010";
        String phoneNumberLandline= "+33177695958";
	String phoneNumberTwilioKawanSoftUSA = "+14159675199";
        String phoneNumberItaliaBrunoLevy= "+393428113323";
	String hiltonBruxelles = "+3225484211";
        String hiltonSpain = "+34934957777";
        String hiltonLima = "+51-1-200-8000";
        String hiltonMontenegro = "+382-20-443443";
	String phoneNumber = hiltonMontenegro;

	phoneNumber = MobileUtil.removeSpecialCharacters(phoneNumber);
        MobilePhoneValidator mobilePhoneValidator = new MobilePhoneValidator(phoneNumber);
        boolean isValid = mobilePhoneValidator.isValid();
        String errroMesage = mobilePhoneValidator.getErrorMessage();
        
        if (isValid) {
            System.out.println("Valid!");
        }
        else {
            System.out.println("Not valid! errroMesage : " + errroMesage);
        }

	String isoCountryCode = null;
	isoCountryCode = PhoneCountryLookup.getIsoCountryCode(phoneNumber);
	System.out.println("ISO country code  : " + isoCountryCode);

        //displayPossibleRegions(phoneNumber);
                
	boolean isPossible = PhoneCountryLookup.isAlphanumericIdPossible(phoneNumber);
	System.out.println("isAlphanumericIdPossible: " + isPossible);

	String i18nFormat = PhoneCountryLookup.getInternationalFormat(phoneNumber, isoCountryCode);
	System.out.println("International Format: " + i18nFormat);

    }

}
