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
package net.safester.application.addrbooknew.gmail;

import java.awt.Desktop;
import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.safelogic.utilx.Debug;

import net.safester.application.install.NewVersionInstaller;

/**
 * Method to import Google accounts (Gmail) Contacts.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class GoogleContacts {

    public static boolean DEBUG = Debug.isSet(GoogleContacts.class);

    /** Will be fetch by validateCode() */
    private People peopleService = null;

    /**
     * Launch a browser with URL that asks for Contact access authorizations and
     * allows for user to get back the auth code
     * @throws java.io.IOException
     */
    public static void openBrowserToGetCode() throws IOException {

	// Go to the Google Developers Console, open your application's
	// credentials page, and copy the client ID and client secret.
	// Then paste them into the following code.
	String clientId = GooglePeopleParms.CLIENT_ID;

	// Or your redirect URL for web based applications.
	String redirectUrl = GooglePeopleParms.REDIRECT_URL;
	String scope = GooglePeopleParms.SCOPE;

	// Step 1: Authorize -->
	String authorizationUrl = new GoogleBrowserClientRequestUrl(clientId,
		redirectUrl, Arrays.asList(scope)).setResponseTypes(
		Arrays.asList(GooglePeopleParms.RESPONSE_TYPE_CODE)).build();

	// Point or redirect your user to the authorizationUrl.
	Desktop desktop = Desktop.getDesktop();

	URI uri = null;

	try {
	    uri = new URL(authorizationUrl).toURI();
	} catch (URISyntaxException e) {
	    throw new IllegalArgumentException(e);
	}

	desktop.browse(uri);
    }

    /**
     * Tests that the code is valid for Contacts access authorization.
     * The validation will populate the field People peopleService.
     * 
     * @param code
     *            the authentication code, provided by the end user from the Web
     * @param clientSecret the Google "Client Secret" for Auth. Fetch encrypted from server from server for security reasons
     * 
     * @throws IOException
     *             if any Internet Exception
     * @return the boolean
     * 
     */
    public boolean validateCode(String code, String clientSecret) throws IOException {

	// See
	// http://stackoverflow.com/questions/35604406/retrieving-information-about-a-contact-with-google-people-api-java

	if (code == null) {
	    throw new NullPointerException("code can not be null!");
	}
	
	// Go to the Google Developers Console, open your application's
	// credentials page, and copy the client ID and client secret.
	// Then paste them into the following code.
	String clientId = GooglePeopleParms.CLIENT_ID;
        
	// Or your redirect URL for web based applications.
	String redirectUrl = GooglePeopleParms.REDIRECT_URL;

	// End of Step 1 <--

	HttpTransport httpTransport = null;

	Proxy proxy = NewVersionInstaller.getProxy();

	if (proxy == null) {
	    httpTransport = new NetHttpTransport();
	} else {
	    httpTransport = new NetHttpTransport.Builder().setProxy(proxy)
		    .build();
	}

	JacksonFactory jsonFactory = new JacksonFactory();

	GoogleTokenResponse tokenResponse = null;

	try {
	    tokenResponse = new GoogleAuthorizationCodeTokenRequest(
		    httpTransport, jsonFactory, clientId, clientSecret, code,
		    redirectUrl).execute();
	} catch (Exception ex) {
	    if (ex instanceof TokenResponseException
		    && ex.getMessage().trim().contains("400 Bad Request")) {
		ex.printStackTrace();
		return false;
	    } else {
		throw ex;
	    }
	}

	debug(new Date() + " MIDDLE");
	
	if (tokenResponse == null) {
	    return false;
	}

	GoogleCredential credential = new GoogleCredential.Builder()
		.setTransport(httpTransport).setJsonFactory(jsonFactory)
		.setClientSecrets(clientId, clientSecret).build()
		.setFromTokenResponse(tokenResponse);

	peopleService = new People.Builder(httpTransport, jsonFactory,
		credential).setApplicationName(
		GooglePeopleParms.APPLICATION_NAME).build();

	return true;
    }

    /**
     * Contacts the Google server and returns all the Persons from the user Gmail account. 
     * isCodeValid() must be called before to validate code and put in memory the People instance peopleService.
     * 
     * @return all the user's Gmail Contacts
     * 
     * @throws IOException
     *             if any Internet Exception
     * 
     */
    public List<Person> getPersons() throws IOException {

	if (peopleService == null) {
	    throw new NullPointerException("People peopleService field is null. Call validateCode() before this getPersons().");
	}
	
	List<Person> finalPersons = new ArrayList<Person>();

	// Request all connections in loop by "pages" of 500 Persons
	ListConnectionsResponse response = peopleService
		.people()
		.connections()
		.list("people/me")
		.setPageSize(500)
		.setRequestMaskIncludeField(
			"person.names,person.emailAddresses,person.phoneNumbers,person.organizations")
		.execute();

	List<Person> persons = response.getConnections();
        
        if (persons == null) {
            return finalPersons;
        }
        
	finalPersons.addAll(persons);

	String token = response.getNextPageToken();
	debug("token              : " + token);
	debug("finalPersons.size(): " + finalPersons.size());

	while (token != null) {

	    response = peopleService
		    .people()
		    .connections()
		    .list("people/me")
		    .setPageToken(token)
		    .setPageSize(500)
		    .setRequestMaskIncludeField(
			    "person.names,person.emailAddresses,person.phoneNumbers,person.organizations")
		    .execute();

	    persons = response.getConnections();
	    finalPersons.addAll(persons);

	    token = response.getNextPageToken();
	    debug("token              : " + token);
	    debug("finalPersons.size(): " + finalPersons.size());
	}

	return finalPersons;

    }

    /**
     * debug tool
     */
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }


}
