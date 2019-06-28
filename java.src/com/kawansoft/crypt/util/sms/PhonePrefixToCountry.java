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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.google.i18n.phonenumbers.NumberParseException;

public class PhonePrefixToCountry {

    private static boolean DISPLAY_MAP = false;

    /** Base map of phone prefix per country code, aka ("FR", "33") */
    private static Map<String, String> map = new TreeMap<>();

    /** The map of phone prefix per country aka ("33", ["FR"]) and ("1", ["CA", "US"]) */
    private static Map<String, Set<String>> mapCountryFromPhonePrefix = new TreeMap<>();

    
    /**
     * For test purposes
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

	//System.out.println("string".substring(0, 2));
	//System.out.println("string".substring(0, 3));
	
	DISPLAY_MAP = true;
	buildMapPhoneFromPrefixCountry();
	buildMapCountryFromPhonePrefix();

    }
    
    /**
     * Returns the iso2 country code for a phone prefix numeric in string format.
     * 
     * @param phoneNumber the full phone number, necessary in case multiple possible countries
     * @return the country code as "FR", "US", etc
     * 
     * @throws NumberParseException 
     * @throws IllegalArgumentException in case no country code for phone number
     */
    public static String getCountryCodeForPhoneNumber(String phoneNumber) throws NumberParseException, IllegalArgumentException {
	
	if (phoneNumber == null) {
	    throw new NullPointerException("phoneNumber is null!");
	}
	
	if (phoneNumber.startsWith("+")) {
	    phoneNumber = phoneNumber.substring(1);
	}
	
	if (! StringUtils.isNumeric(phoneNumber)) {
	    throw new IllegalArgumentException("phoneNumber is not numeric!");
	}
	
	if (map.isEmpty()) {
	    buildMapPhoneFromPrefixCountry();
	}
	if (mapCountryFromPhonePrefix.isEmpty()) {
	    buildMapCountryFromPhonePrefix();
	}
	
	Set<String> countries = CountryCodeSeacher.getCountries(mapCountryFromPhonePrefix, phoneNumber);
	if (countries == null) {
	    throw new IllegalArgumentException("Unknown phone prefix for phone number: "
		    + phoneNumber);
	}
	
	String countryCode = null;
	
	if (countries.size() == 1) {
	    for (Iterator<String> iterator = countries.iterator(); iterator.hasNext();) {
		countryCode = iterator.next();
	    }
	}
	else {
	    //System.out.println("phoneNumber: " + phoneNumber);
	    //System.out.println("countries  : " + countries);
	    countryCode = PhoneCountryLookup.getIsoCountryCode(phoneNumber, countries);
	}
        
       //System.out.println();
       //System.out.println("phoneNumber: " + phoneNumber);
       //System.out.println("countryCode: " + countryCode);

       return countryCode;
	
    }
    
    /**
     * Returns the phone prefix for iso2 country code  ("33" for "FR)
     * @param iso2CountryCode	the country code as "FR", "US", etc.
     * @return the phone prefix for isoCountryCode as a string with a raw number 33, 1, etc
     */
    public static String getPrefixFor(String iso2CountryCode) {
	
	if (iso2CountryCode == null) {
	    throw new NullPointerException("iso2CountryCode is null!");
	}
	
	if (map.isEmpty()) {
	    buildMapPhoneFromPrefixCountry();
	}
	
	String result = map.get(iso2CountryCode);
	if (result == null) {
	    throw new IllegalArgumentException("Unknown country code "
		    + iso2CountryCode);
	}
	return result;
    }
    

    /**
     * Builds mapCountryFromPhonePrefix from map (invert map)
     */
    private static void buildMapCountryFromPhonePrefix() {
	
	for (Map.Entry<String, String> entry : map.entrySet()) {
	    // System.out.println(entry.getKey() + "/" + entry.getValue());
	    String alphaCode = entry.getKey();
	    String numericCode = entry.getValue();
	    numericCode = numericCode.replace("-", "");
	    numericCode = numericCode.replace("+", "");

	    if (!mapCountryFromPhonePrefix.containsKey(numericCode)) {
		Set<String> alphaCodes = new TreeSet<>();
		alphaCodes.add(alphaCode);
		mapCountryFromPhonePrefix.put(numericCode, alphaCodes);
	    } else {
		Set<String> alphaCodes  = mapCountryFromPhonePrefix.get(numericCode);
		alphaCodes.add(alphaCode);
		mapCountryFromPhonePrefix.put(numericCode, alphaCodes);
	    }
	}
	// Add missing values
	
	Set<String> usRegions = new TreeSet<String>();
	
	// and 1-939 ? for PR
	usRegions.add("PR");
	mapCountryFromPhonePrefix.put("1939", usRegions);
	
	//1-829? and 1-949? For DO
	usRegions = new TreeSet<String>();
	usRegions.add("DO");
	mapCountryFromPhonePrefix.put("1829", usRegions);
	mapCountryFromPhonePrefix.put("1949", usRegions);
	
	
	if (DISPLAY_MAP) {
	    for (Map.Entry<String, Set<String>> entry : mapCountryFromPhonePrefix
		    .entrySet()) {
		String key = entry.getKey();
		Set<String> value = entry.getValue();
		System.out.println(key + " " + value);
	    }
	}
    }

    /**
     * Builds base map
     */
    private static void buildMapPhoneFromPrefixCountry() {

	map.put("AC", "+247");
	map.put("AD", "+376");
	map.put("AE", "+971");
	map.put("AF", "+93");
	map.put("AG", "+1-268");
	map.put("AI", "+1-264");
	map.put("AL", "+355");
	map.put("AM", "+374");
	map.put("AN", "+599");
	map.put("AO", "+244");
	map.put("AR", "+54");
	map.put("AS", "+1-684");
	map.put("AT", "+43");
	map.put("AU", "+61");
	map.put("AW", "+297");
	//map.put("AX", "+358-18"); This is �land, in Finland Don't f... care!!
	map.put("AZ", "+374-97");
	map.put("AZ", "+994");
	map.put("BA", "+387");
	map.put("BB", "+1-246");
	map.put("BD", "+880");
	map.put("BE", "+32");
	map.put("BF", "+226");
	map.put("BG", "+359");
	map.put("BH", "+973");
	map.put("BI", "+257");
	map.put("BJ", "+229");
	map.put("BM", "+1-441");
	map.put("BN", "+673");
	map.put("BO", "+591");
	map.put("BR", "+55");
	map.put("BS", "+1-242");
	map.put("BT", "+975");
	map.put("BW", "+267");
	map.put("BY", "+375");
	map.put("BZ", "+501");
	map.put("CA", "+1");
	map.put("CC", "+61");
	map.put("CD", "+243");
	map.put("CF", "+236");
	map.put("CG", "+242");
	map.put("CH", "+41");
	map.put("CI", "+225");
	map.put("CK", "+682");
	map.put("CL", "+56");
	map.put("CM", "+237");
	map.put("CN", "+86");
	map.put("CO", "+57");
	map.put("CR", "+506");
	map.put("CS", "+381");
	map.put("CU", "+53");
	map.put("CV", "+238");
	map.put("CX", "+61");
	map.put("CY", "+90-392");
	map.put("CY", "+357");
	map.put("CZ", "+420");
	map.put("DE", "+49");
	map.put("DJ", "+253");
	map.put("DK", "+45");
	map.put("DM", "+1-767");
	map.put("DO", "+1-809"); // and 1-829? and 1-949? for DO
	map.put("DZ", "+213");
	map.put("EC", "+593");
	map.put("EE", "+372");
	map.put("EG", "+20");
	map.put("EH", "+212");
	map.put("ER", "+291");
	map.put("ES", "+34");
	map.put("ET", "+251");
	map.put("FI", "+358");
	map.put("FJ", "+679");
	map.put("FK", "+500");
	map.put("FM", "+691");
	map.put("FO", "+298");
	map.put("FR", "+33");
	map.put("GA", "+241");
	map.put("GB", "+44");
	map.put("GD", "+1-473");
	map.put("GE", "+995");
	map.put("GF", "+594");
	map.put("GG", "+44");
	map.put("GH", "+233");
	map.put("GI", "+350");
	map.put("GL", "+299");
	map.put("GM", "+220");
	map.put("GN", "+224");
	map.put("GP", "+590");
	map.put("GQ", "+240");
	map.put("GR", "+30");
	map.put("GT", "+502");
	map.put("GU", "+1-671");
	map.put("GW", "+245");
	map.put("GY", "+592");
	map.put("HK", "+852");
	map.put("HN", "+504");
	map.put("HR", "+385");
	map.put("HT", "+509");
	map.put("HU", "+36");
	map.put("ID", "+62");
	map.put("IE", "+353");
	map.put("IL", "+972");
	map.put("IM", "+44");
	map.put("IN", "+91");
	map.put("IO", "+246");
	map.put("IQ", "+964");
	map.put("IR", "+98");
	map.put("IS", "+354");
	map.put("IT", "+39");
	map.put("JE", "+44");
	map.put("JM", "+1-876");
	map.put("JO", "+962");
	map.put("JP", "+81");
	map.put("KE", "+254");
	map.put("KG", "+996");
	map.put("KH", "+855");
	map.put("KI", "+686");
	map.put("KM", "+269");
	map.put("KN", "+1-869");
	map.put("KP", "+850");
	map.put("KR", "+82");
	map.put("KW", "+965");
	map.put("KY", "+1-345");
	map.put("KZ", "+7");
	map.put("LA", "+856");
	map.put("LB", "+961");
	map.put("LC", "+1-758");
	map.put("LI", "+423");
	map.put("LK", "+94");
	map.put("LR", "+231");
	map.put("LS", "+266");
	map.put("LT", "+370");
	map.put("LU", "+352");
	map.put("LV", "+371");
	map.put("LY", "+218");
	map.put("MA", "+212");
	map.put("MC", "+377");
	map.put("MD", "+373-533");
	map.put("MD", "+373");
	map.put("ME", "+382");
	map.put("MG", "+261");
	map.put("MH", "+692");
	map.put("MK", "+389");
	map.put("ML", "+223");
	map.put("MM", "+95");
	map.put("MN", "+976");
	map.put("MO", "+853");
	map.put("MP", "+1-670");
	map.put("MQ", "+596");
	map.put("MR", "+222");
	map.put("MS", "+1-664");
	map.put("MT", "+356");
	map.put("MU", "+230");
	map.put("MV", "+960");
	map.put("MW", "+265");
	map.put("MX", "+52");
	map.put("MY", "+60");
	map.put("MZ", "+258");
	map.put("NA", "+264");
	map.put("NC", "+687");
	map.put("NE", "+227");
	map.put("NF", "+672");
	map.put("NG", "+234");
	map.put("NI", "+505");
	map.put("NL", "+31");
	map.put("NO", "+47");
	map.put("NP", "+977");
	map.put("NR", "+674");
	map.put("NU", "+683");
	map.put("NZ", "+64");
	map.put("OM", "+968");
	map.put("PA", "+507");
	map.put("PE", "+51");
	map.put("PF", "+689");
	map.put("PG", "+675");
	map.put("PH", "+63");
	map.put("PK", "+92");
	map.put("PL", "+48");
	map.put("PM", "+508");
	map.put("PR", "+1-787"); // and 1-939 ? for PR
	map.put("PS", "+970");
	map.put("PT", "+351");
	map.put("PW", "+680");
	map.put("PY", "+595");
	map.put("QA", "+974");
	map.put("RE", "+262");
	map.put("RO", "+40");
	map.put("RS", "+381");
	map.put("RU", "+7");
	map.put("RW", "+250");
	map.put("SA", "+966");
	map.put("SB", "+677");
	map.put("SC", "+248");
	map.put("SD", "+249");
	map.put("SE", "+46");
	map.put("SG", "+65");
	map.put("SH", "+290");
	map.put("SI", "+386");
	map.put("SJ", "+47");
	map.put("SK", "+421");
	map.put("SL", "+232");
	map.put("SM", "+378");
	map.put("SN", "+221");
	map.put("SO", "+252");
	map.put("SO", "+252");
	map.put("SR", "+597");
	map.put("ST", "+239");
	map.put("SV", "+503");
	map.put("SY", "+963");
	map.put("SZ", "+268");
	map.put("TA", "+290");
	map.put("TC", "+1-649");
	map.put("TD", "+235");
	map.put("TG", "+228");
	map.put("TH", "+66");
	map.put("TJ", "+992");
	map.put("TK", "+690");
	map.put("TL", "+670");
	map.put("TM", "+993");
	map.put("TN", "+216");
	map.put("TO", "+676");
	map.put("TR", "+90");
	map.put("TT", "+1-868");
	map.put("TV", "+688");
	map.put("TW", "+886");
	map.put("TZ", "+255");
	map.put("UA", "+380");
	map.put("UG", "+256");
	map.put("US", "+1");
	map.put("UY", "+598");
	map.put("UZ", "+998");
	map.put("VA", "+379");
	map.put("VC", "+1-784");
	map.put("VE", "+58");
	map.put("VG", "+1-284");
	map.put("VI", "+1-340");
	map.put("VN", "+84");
	map.put("VU", "+678");
	map.put("WF", "+681");
	map.put("WS", "+685");
	map.put("YE", "+967");
	map.put("YT", "+262");
	map.put("ZA", "+27");
	map.put("ZM", "+260");
	map.put("ZW", "+263");
    }
    
    public static void displayMapCountryFromPhonePrefixPrGroup() {
	for (Map.Entry<String, Set<String>> entry2 : mapCountryFromPhonePrefix.entrySet()) {

	    String key = entry2.getKey();
	    Set<String> value = entry2.getValue();

	    if (key.startsWith("1")) {
		System.out.println(key.substring(0, 1) + "  group " + key + " "
			+ value);
	    }
	    if (key.startsWith("2")) {
		System.out.println(key.substring(0, 1) + "  group " + key + " "
			+ value);
	    }
	    if (key.startsWith("3")) {
		System.out.println(key.substring(0, 1) + "  group " + key + " "
			+ value);
	    }
	    if (key.startsWith("4")) {
		System.out.println(key.substring(0, 1) + "  group " + key + " "
			+ value);
	    }
	    if (key.startsWith("5")) {
		System.out.println(key.substring(0, 1) + "  group " + key + " "
			+ value);
	    }
	    if (key.startsWith("6")) {
		System.out.println(key.substring(0, 1) + "  group " + key + " "
			+ value);
	    }

	    if (key.startsWith("7")) {
		System.out.println(key.substring(0, 1) + "  group " + key + " "
			+ value);
	    }
	    if (key.startsWith("8")) {
		System.out.println(key.substring(0, 1) + "  group " + key + " "
			+ value);
	    }
	    if (key.startsWith("9")) {
		System.out.println(key.substring(0, 1) + "  group " + key + " "
			+ value);
	    }

	}
    }
}
