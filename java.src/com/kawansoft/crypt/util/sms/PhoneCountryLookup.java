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

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * Tools to verify and format cell phone numbers.
 *
 * @author Nicolas de Pomereu
 */
public class PhoneCountryLookup {

    /*
     * GP = Guadeloupe FR = France MQ = Martinique PF = Polynésie Française
     */
    public static final String NO_COUNTRY_CODE_FOR_REGION = "NO_COUNTRY_CODE_FOR_REGION";

    public static final String FR_FRANCE = "FR";
    public static final String FR_GUADELOUPE = "GP";
    public static final String FR_MARTINIQUE = "MQ";
    public static final String FR_POLYNESIA = "PF";

    private static Set<String> SET_COUNTRIES_ALPHA_ID = new HashSet<>();

    /**
     * Get the nice display format of a phone number
     *
     * @param phoneNumber
     * @param countryCode TODO
     * @return
     */
    public static String getInternationalFormat(String phoneNumber, String countryCode) {

        if (phoneNumber == null) {
            return null;
        }

        if (countryCode == null) {
            return phoneNumber;
        }
        
        if (phoneNumber.isEmpty()) {
            return phoneNumber;
        }

        phoneNumber = addLeadingPlusSign(phoneNumber);

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        PhoneNumber numberProto = null;

        try {
            numberProto = phoneUtil.parse(phoneNumber, countryCode);
            String i18nFormat = phoneUtil.format(numberProto,
                    PhoneNumberFormat.INTERNATIONAL);
            return i18nFormat;
        } catch (NumberParseException e) {
            e.printStackTrace();
            return phoneNumber;
        }
    }

    /**
     * Says from a phone number if usage of Alphanumeric ID possible as SMS
     * sender
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isAlphanumericIdPossible(String phoneNumber) {

        if (phoneNumber == null) {
            throw new NullPointerException("phoneNumber is null!");
        }

        phoneNumber = addLeadingPlusSign(phoneNumber);

        if (SET_COUNTRIES_ALPHA_ID.isEmpty()) {
            SET_COUNTRIES_ALPHA_ID.add("AT");
            SET_COUNTRIES_ALPHA_ID.add("AU");
            SET_COUNTRIES_ALPHA_ID.add("CH");
            SET_COUNTRIES_ALPHA_ID.add("CY");
            SET_COUNTRIES_ALPHA_ID.add("CZ");
            SET_COUNTRIES_ALPHA_ID.add("DE");
            SET_COUNTRIES_ALPHA_ID.add("DK");
            SET_COUNTRIES_ALPHA_ID.add("ES");
            // SET_COUNTRIES_ALPHA_ID.add("FI"); // NO! See
            // https://www.ovh.com/fr/g747.sms_-_aboutissement_selon_la_destination
            SET_COUNTRIES_ALPHA_ID.add("FR");
            SET_COUNTRIES_ALPHA_ID.add("GB");
            SET_COUNTRIES_ALPHA_ID.add("GP");
            SET_COUNTRIES_ALPHA_ID.add("GR");
            SET_COUNTRIES_ALPHA_ID.add("IL");
            SET_COUNTRIES_ALPHA_ID.add("IS");
            SET_COUNTRIES_ALPHA_ID.add("IT");
            SET_COUNTRIES_ALPHA_ID.add("JE");
            SET_COUNTRIES_ALPHA_ID.add("LU");
            SET_COUNTRIES_ALPHA_ID.add("ME");
            SET_COUNTRIES_ALPHA_ID.add("MQ");
            SET_COUNTRIES_ALPHA_ID.add("MU");
            SET_COUNTRIES_ALPHA_ID.add("NL");
            SET_COUNTRIES_ALPHA_ID.add("NO");
            SET_COUNTRIES_ALPHA_ID.add("PE");
            SET_COUNTRIES_ALPHA_ID.add("PF");
            // SET_COUNTRIES_ALPHA_ID.add("PT"); // NO! See
            // https://www.ovh.com/fr/g747.sms_-_aboutissement_selon_la_destination
            SET_COUNTRIES_ALPHA_ID.add("RE");
            SET_COUNTRIES_ALPHA_ID.add("RS");
            SET_COUNTRIES_ALPHA_ID.add("RU");
            SET_COUNTRIES_ALPHA_ID.add("SE");
            SET_COUNTRIES_ALPHA_ID.add("SG");
            SET_COUNTRIES_ALPHA_ID.add("SI");
            SET_COUNTRIES_ALPHA_ID.add("SK");
            SET_COUNTRIES_ALPHA_ID.add("UA");
        }

        String isoCountryCode = null;
        try {
            isoCountryCode = getIsoCountryCode(phoneNumber);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        if (SET_COUNTRIES_ALPHA_ID.contains(isoCountryCode)) {
            return true;
        } else {
            return false;
        }

    }

    
    public static String getIsoCountryCode(String phoneNumber) throws IllegalArgumentException, NumberParseException {
	return PhonePrefixToCountry.getCountryCodeForPhoneNumber(phoneNumber);
    }

    public static String addLeadingPlusSign(String phoneNumber) {

        if (phoneNumber == null) {
            throw new NullPointerException("phoneNumber is null!");
        }

        // phone must begin with '+'
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber;
        }
        return phoneNumber;
    }

    public static boolean isFrenchOfficialLanguage(String isoCountryCode) {

        if (isoCountryCode == null) {
            throw new NullPointerException("isoCountryCode is null!");
        }

        if (isoCountryCode.equals(FR_FRANCE)
                || isoCountryCode.equals(FR_GUADELOUPE)
                || isoCountryCode.equals(FR_MARTINIQUE)
                || isoCountryCode.equals(FR_POLYNESIA)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Returns the ISO country code (FR) from the phone nu
     *
     * @param phoneNumber
     * @return
     * @throws NumberParseException
     * @throws IllegalArgumentException
     */
    public static String getIsoCountryCode(String phoneNumber, Set<String> possibleRegions)
            throws NumberParseException {

        if (phoneNumber == null) {
            throw new NullPointerException("phoneNumber is null!");
        }

        phoneNumber = addLeadingPlusSign(phoneNumber);

        boolean isValid;
        PhoneNumber number;
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();

        String countryCode = null;

        for (String r : possibleRegions) {
            // check if it's a possible number
            isValid = util.isPossibleNumber(phoneNumber, r);

            if (isValid) {
                number = util.parse(phoneNumber, r);

                // check if it's a valid number for the given region
                isValid = util.isValidNumberForRegion(number, r);
                if (isValid) {
                    countryCode = r;

                    // Patch for VA (Vatican) ==> Return IT Italia
                    if (countryCode.equals("VA")) {
                        countryCode = "IT";
                    }

                    return countryCode;
                }
            }
        }

        // Throw Exception
        throw new IllegalArgumentException(NO_COUNTRY_CODE_FOR_REGION + ": " + possibleRegions);

    }
    
    
    /**
     * Returns the ISO country code (FR) from the phone nu
     *
     * @param phoneNumber
     * @return
     * @throws NumberParseException
     */
    /*
    public static String getIsoCountryCode(String phoneNumber)
            throws NumberParseException {

        if (phoneNumber == null) {
            throw new NullPointerException("phoneNumber is null!");
        }

        phoneNumber = addLeadingPlusSign(phoneNumber);

        boolean isValid;
        PhoneNumber number;
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();

        String countryCode = null;

        for (String r : util.getSupportedRegions()) {
            // check if it's a possible number
            isValid = util.isPossibleNumber(phoneNumber, r);

            if (isValid) {
                number = util.parse(phoneNumber, r);

                // check if it's a valid number for the given region
                isValid = util.isValidNumberForRegion(number, r);
                if (isValid) {
                    countryCode = r;

                    // Patch for VA (Vatican) ==> Return IT Italia
                    if (countryCode.equals("VA")) {
                        countryCode = "IT";
                    }

                    return countryCode;
                }
            }
        }

        // Throw Exception
        throw new IllegalArgumentException(NO_COUNTRY_CODE_FOR_REGION);

    }
    */

    public static void displayCountryCodes() {

        String[] locales = Locale.getISOCountries();

	for (String countryCode : locales) {

	    Locale obj = new Locale("en", countryCode);

            int numCountryCode = PhoneNumberUtil.getInstance().getCountryCodeForRegion(obj.getCountry());
                       
	    System.out.println(obj.getCountry() + " = "
		    + obj.getDisplayCountry() + " " + numCountryCode);

 
            //System.out.println(numCountryCode + ", " + obj.getCountry());
            
	}
        
        PhoneNumberUtil util
                = PhoneNumberUtil.getInstance();

        for (String r : util.getSupportedRegions()) {
            int numCountryCode = PhoneNumberUtil.getInstance().getCountryCodeForRegion(r);
            System.out.println(numCountryCode + ", " + r);
        }

    }

// KEEP AS MODEL
    public static void displayPossibleRegions(String phoneNumber) {

        phoneNumber = addLeadingPlusSign(phoneNumber);

        boolean isValid;
        PhoneNumber number;
        PhoneNumberUtil util
                = PhoneNumberUtil.getInstance();

        for (String r : util.getSupportedRegions()) {

            try { // check if it's a possible number
                isValid = util.isPossibleNumber(phoneNumber, r);
                if (isValid) {
                    number = util.parse(phoneNumber, r);

                    // check if it's a valid number for the given region isValid =
                    isValid = util.isValidNumberForRegion(number, r);
                    if (isValid) {
                        System.out.println(r + ": " + number.getCountryCode() + ", "
                                + number.getNationalNumber());
                    }
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    public static void displayCountryCodes() {

	String[] locales = Locale.getISOCountries();

	for (String countryCode : locales) {

	    Locale obj = new Locale("en", countryCode);

	    System.out.println(obj.getCountry() + " = "
		    + obj.getDisplayCountry());

	}

    }
     */
}
