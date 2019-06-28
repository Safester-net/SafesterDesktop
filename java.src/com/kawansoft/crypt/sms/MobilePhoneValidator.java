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
package com.kawansoft.crypt.sms;

import org.apache.commons.lang3.StringUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.kawansoft.crypt.util.sms.PhoneCountryLookup;
import net.safester.application.messages.MessagesManager;

/**
 * Class to validate phone numbers wir detailed error message
 * @author Nicolas de Pomereu
 *
 */
public class MobilePhoneValidator {

    private String mobilePhone = null;
    private String errorMessage = null;
    
    /**
     * Constructor
     * @param mobilePhonethe mobile phone to validate
     */
    public MobilePhoneValidator(String mobilePhone) {
	super();
	this.mobilePhone = mobilePhone;
    }
    
    /**
     * Says if a phone number is a valid cell phone / mobile number
     * 
     * @return true if phone number is valid
     */
    public boolean isValid() {
        
	if (mobilePhone == null || mobilePhone.isEmpty()) {
	    errorMessage = MessagesManager.get("invalid_number_is_null_or_empty");
	    return false;
	}

	mobilePhone = PhoneCountryLookup.addLeadingPlusSign(mobilePhone);

	// is numeric?
	String numericPart = mobilePhone.substring(1);
	if (!StringUtils.isNumeric(numericPart)) {
	    errorMessage = MessagesManager.get("invalid_number_is_not_numeric") + " " +  numericPart;
	    return false;
	}

	String isoCountryCode = null;
	try {
	    isoCountryCode = PhoneCountryLookup.getIsoCountryCode(mobilePhone);
	} catch (Exception ex) {
	    errorMessage = MessagesManager.get("invalid_number_or_country_not_found") + " " + mobilePhone;
	    return false;
	}

	PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
	PhoneNumber numberProto = null;

	try {
	    numberProto = phoneUtil.parse(mobilePhone, isoCountryCode);
	    boolean isValid = phoneUtil.isValidNumber(numberProto);

	    if (!isValid) {
		errorMessage = MessagesManager.get("invalid_number_does_not_match_a_valid_pattern") + " " + mobilePhone;
		return false;
	    }

	    // Must not be a land line phone
	    PhoneNumberType phoneNumberType = phoneUtil
		    .getNumberType(numberProto);
	    if (phoneNumberType != PhoneNumberType.MOBILE
		    && phoneNumberType != PhoneNumberType.FIXED_LINE_OR_MOBILE
		    && phoneNumberType != PhoneNumberType.PERSONAL_NUMBER 
		    && phoneNumberType != PhoneNumberType.UNKNOWN) {
		errorMessage = MessagesManager.get("invalid_number_is_not_a_cell_phone_number") + " " + mobilePhone + ".";
		return false;
	    }

	    errorMessage = null;
	    return true;
	} catch (NumberParseException e) {
	    errorMessage = MessagesManager.get("invalid_number_can_not_be_parsed") + " " + e.getMessage();
	    return false;
	}
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    
    
    

}
