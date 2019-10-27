/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.safester.application.http.test;

import com.google.api.services.people.v1.model.Person;
import com.kawansoft.httpclient.KawanHttpClient;
import java.util.Date;
import java.util.List;
import net.safester.application.http.ApiMessages;
import net.safester.application.http.KawanHttpClientBuilder;

/**
 *
 * @author Nicolas de Pomereu
 */
public class ApiMessagesTest {
    
    /**
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println(new Date() + " Begin...");

        String username = "ndepomereu@gmail.com";
        String token = "ddb1bce5a973c0ba859f";

        KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.build(null);
        ApiMessages apiMessages = new ApiMessages(kawanHttpClient, username, token);

        String googeCode = "4/sgHGYwAXZOPj6b2Nk7xoWitJ0hmfva9y-TBY1_LffDcs8N_ELMVTYxg";
        List<Person> persons = apiMessages.googleGetPersons(googeCode);

        System.out.println(new Date() + " persons.size(): " + persons.size());
        
        for (Person person : persons) {
            System.out.println(person);
        }

    }
}
