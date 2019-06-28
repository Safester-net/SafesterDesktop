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

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

/**
 * Class to search for code country from phone prefix using a tree.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class CountryCodeSeacher {

    private static Map<String, Set<String>> mapCountryFromPhonePrefix = null;
    
    public static Set<String> getCountries(
	    Map<String, Set<String>> mapCountryFromPhonePrefix,
	    String phoneNumber) {
	
	if (phoneNumber == null) {
	    throw new NullPointerException("phoneNumber is null!");
	}

	if (mapCountryFromPhonePrefix == null || mapCountryFromPhonePrefix.isEmpty()) {
	    throw new NullPointerException("mapCountryFromPhonePrefix is null or empty!");
	}
	
	if (! StringUtils.isNumeric(phoneNumber)) {
	    throw new IllegalArgumentException("phoneNumber is not numeric!");
	}

	CountryCodeSeacher.mapCountryFromPhonePrefix = mapCountryFromPhonePrefix;
	
	Set<String> countries = new TreeSet<>();
	
	if (phoneNumber.startsWith("9")) {
	    countries = searchCountriesPrefixStarts_9(phoneNumber);
	} else if (phoneNumber.startsWith("8")) {
	    countries = searchCountriesPrefixStarts_8(phoneNumber);
	} else if (phoneNumber.startsWith("7")) {
	    countries = searchCountriesPrefixStarts_7(phoneNumber);
	} else if (phoneNumber.startsWith("6")) {
	    countries = searchCountriesPrefixStarts_6(phoneNumber);
	} else if (phoneNumber.startsWith("5")) {
	    countries = searchCountriesPrefixStarts_5(phoneNumber);
	} else if (phoneNumber.startsWith("4")) {
	    countries = searchCountriesPrefixStarts_4(phoneNumber);
	} else if (phoneNumber.startsWith("3")) {
	    countries = searchCountriesPrefixStarts_3(phoneNumber);
	} else if (phoneNumber.startsWith("2")) {
	    countries = searchCountriesPrefixStarts_2(phoneNumber);
	} else if (phoneNumber.startsWith("1")) {
	    countries = searchCountriesPrefixStarts_1(phoneNumber);
	}
	
	return countries;
    }

    private static String getFirst3(String phoneNumber) {
	
	if (phoneNumber.length() < 3) {
	    throw new IllegalArgumentException("phoneNumber is too short: " + phoneNumber);
	}
	
	String first3 = phoneNumber.substring(0, 3);
	return first3;
    }
    
    private static String getFirst4(String phoneNumber) {
	
	if (phoneNumber.length() < 4) {
	    throw new IllegalArgumentException("phoneNumber is too short: " + phoneNumber);
	}
	
	String first3 = phoneNumber.substring(0, 4);
	return first3;
    }

    private static String getFirst2(String phoneNumber) {
	
	if (phoneNumber.length() < 2) {
	    throw new IllegalArgumentException("phoneNumber is too short: " + phoneNumber);
	}
	
	String first2 = phoneNumber.substring(0, 2);
	return first2;
    }
    
    private static String getFirst1(String phoneNumber) {
	
	if (phoneNumber.length() < 1) {
	    throw new IllegalArgumentException("phoneNumber is too short: " + phoneNumber);
	}
	
	String first1 = phoneNumber.substring(0, 1);
	return first1;
    }
    
    /**
     * Extract countries from map countries for prefix that starts with 9
     * @param phoneNumber
     * @return
     */
    private static Set<String> searchCountriesPrefixStarts_9(String phoneNumber) {
	
	if (!phoneNumber.startsWith("9")) {
	    throw new IllegalArgumentException("phone number does not start with 9: " + phoneNumber);
	}
	
	String first2 = getFirst2(phoneNumber);
	String first3 = getFirst3(phoneNumber);
	
	Set<String> countries = null;
	if (first2.equals("99") ||  first2.equals("97") ||  first2.equals("96")) {
	    countries =  mapCountryFromPhonePrefix.get(first3);
	}
	else {
	    countries =  mapCountryFromPhonePrefix.get(first2);
	}

	if (countries == null) {
	    throw new IllegalArgumentException("no valid country code for prefix 9 for phone number: " + phoneNumber);
	}
	return countries;

    }

    /**
     * Extract countries from map countries for prefix that starts with 8
     * @param phoneNumber
     * @return
     */
    private static Set<String> searchCountriesPrefixStarts_8(String phoneNumber) {
	
	if (!phoneNumber.startsWith("8")) {
	    throw new IllegalArgumentException("phone number does not start with 8: " + phoneNumber);
	}
	
	String first2 = getFirst2(phoneNumber);
	String first3 = getFirst3(phoneNumber);
	
	Set<String> countries = null;
	if (first2.equals("88") ||   first2.equals("85")) {
	    countries =  mapCountryFromPhonePrefix.get(first3);
	}
	else {
	    countries =  mapCountryFromPhonePrefix.get(first2);
	}

	if (countries == null) {
	    throw new IllegalArgumentException("no valid country code for prefix 8 for phone number: " + phoneNumber);
	}
	return countries;

    }

    /**
     * Extract countries from map countries for prefix that starts with 7
     * @param phoneNumber
     * @return
     */
    private static Set<String> searchCountriesPrefixStarts_7(String phoneNumber) {
	
	if (!phoneNumber.startsWith("7")) {
	    throw new IllegalArgumentException("phone number does not start with 7: " + phoneNumber);
	}
	
	String first1 = getFirst1(phoneNumber);
	Set<String> countries  =  mapCountryFromPhonePrefix.get(first1);
	
	if (countries == null) {
	    throw new IllegalArgumentException("no valid country code for prefix 7 for phone number: " + phoneNumber);
	}
	return countries;

    }
    
    /**
     * Extract countries from map countries for prefix that starts with 6
     * @param phoneNumber
     * @return
     */
    private static Set<String> searchCountriesPrefixStarts_6(String phoneNumber) {
	
	if (!phoneNumber.startsWith("6")) {
	    throw new IllegalArgumentException("phone number does not start with 6: " + phoneNumber);
	}
	
	
	String first2 = getFirst2(phoneNumber);
	String first3 = getFirst3(phoneNumber);
	
	Set<String> countries = null;
	if (first2.equals("69") ||   first2.equals("68") ||  first2.equals("67")) {
	    countries =  mapCountryFromPhonePrefix.get(first3);
	}
	else {
	    countries =  mapCountryFromPhonePrefix.get(first2);
	}

	if (countries == null) {
	    throw new IllegalArgumentException("no valid country code for prefix 6 for phone number: " + phoneNumber);
	}
	return countries;

    }
    
    /**
     * Extract countries from map countries for prefix that starts with 5
     * @param phoneNumber
     * @return
     */
    private static Set<String> searchCountriesPrefixStarts_5(String phoneNumber) {
	
	if (!phoneNumber.startsWith("5")) {
	    throw new IllegalArgumentException("phone number does not start with 5: " + phoneNumber);
	}
	
	
	String first2 = getFirst2(phoneNumber);
	String first3 = getFirst3(phoneNumber);
	
	Set<String> countries = null;
	if (first2.equals("59") ||   first2.equals("50")) {
	    countries =  mapCountryFromPhonePrefix.get(first3);
	}
	else {
	    countries =  mapCountryFromPhonePrefix.get(first2);
	}

	if (countries == null) {
	    throw new IllegalArgumentException("no valid country code for prefix 5 for phone number: " + phoneNumber);
	}
	return countries;

    }
    
    /**
     * Extract countries from map countries for prefix that starts with 4
     * @param phoneNumber
     * @return
     */
    private static Set<String> searchCountriesPrefixStarts_4(String phoneNumber) {
	
	if (!phoneNumber.startsWith("4")) {
	    throw new IllegalArgumentException("phone number does not start with 8: " + phoneNumber);
	}
	
	String first2 = getFirst2(phoneNumber);
	String first3 = getFirst3(phoneNumber);
	
	Set<String> countries = null;
	if (first2.equals("42")) {
	    countries =  mapCountryFromPhonePrefix.get(first3);
	}
	else {
	    countries =  mapCountryFromPhonePrefix.get(first2);
	}

	if (countries == null) {
	    throw new IllegalArgumentException("no valid country code for prefix 4 for phone number: " + phoneNumber);
	}
	return countries;

    }

    /**
     * Extract countries from map countries for prefix that starts with 3
     * @param phoneNumber
     * @return
     */
    private static Set<String> searchCountriesPrefixStarts_3(String phoneNumber) {
	
	if (!phoneNumber.startsWith("3")) {
	    throw new IllegalArgumentException("phone number does not start with 3: " + phoneNumber);
	}
	
	
	String first2 = getFirst2(phoneNumber);
	String first3 = getFirst3(phoneNumber);
	
	Set<String> countries = null;
	if (first2.equals("38") ||   first2.equals("37") || first2.equals("35") ) {
	    countries =  mapCountryFromPhonePrefix.get(first3);
	}
	else {
	    countries =  mapCountryFromPhonePrefix.get(first2);
	}

	if (countries == null) {
	    throw new IllegalArgumentException("no valid country code for prefix 3 for phone number: " + phoneNumber);
	}
	return countries;

    }
    
    /**
     * Extract countries from map countries for prefix that starts with 2
     * @param phoneNumber
     * @return
     */
    private static Set<String> searchCountriesPrefixStarts_2(String phoneNumber) {
	
	if (!phoneNumber.startsWith("2")) {
	    throw new IllegalArgumentException("phone number does not start with 2: " + phoneNumber);
	}
	
	String first3 = getFirst3(phoneNumber);
	String first2 = getFirst2(phoneNumber);

	Set<String> countries = null;
	if (first2.equals("29") || first2.equals("28") || first2.equals("26")
		|| first2.equals("25") || first2.equals("24")
		|| first2.equals("23") || first2.equals("22")
		|| first2.equals("21")) {
	    countries = mapCountryFromPhonePrefix.get(first3);
	} else {
	    countries = mapCountryFromPhonePrefix.get(first2);
	}

	if (countries == null) {
	    throw new IllegalArgumentException("no valid country code for prefix 2 phone number: " + phoneNumber);
	}
	return countries;

    }
    
    /**
     * Extract countries from map countries for prefix that starts with 1
     * @param phoneNumber
     * @return
     */
    private static Set<String> searchCountriesPrefixStarts_1(String phoneNumber) {
	
	if (!phoneNumber.startsWith("1")) {
	    throw new IllegalArgumentException("phone number does not start with 1: " + phoneNumber);
	}
	
	String first1 = getFirst1(phoneNumber);
	String first4 = getFirst4(phoneNumber);
	
	Set<String> countries = null;
	if (first4.equals("1242") || first4.equals("1246")
		|| first4.equals("1264") || first4.equals("1268")
		|| first4.equals("1284") || first4.equals("1340")
		|| first4.equals("1345") || first4.equals("1441")
		|| first4.equals("1473") || first4.equals("1649")
		|| first4.equals("1664") || first4.equals("1670")
		|| first4.equals("1671") || first4.equals("1684")
		|| first4.equals("1758") || first4.equals("1767")
		|| first4.equals("1784") || first4.equals("1787")
		|| first4.equals("1809") || first4.equals("1868")
		|| first4.equals("1869") || first4.equals("1876")
		|| first4.equals("1829") || first4.equals("1949") // and 1-829? and 1-949? For DO
		|| first4.equals("1939") // and 1-939 ? For PR
		) { 
	    countries = mapCountryFromPhonePrefix.get(first4);
	}
	else {
	    countries =  mapCountryFromPhonePrefix.get(first1); // US & CA
	}

	if (countries == null) {
	    throw new IllegalArgumentException("no valid country code for prefix 1 for phone number: " + phoneNumber);
	}
	return countries;

    }
    
    

    
}
