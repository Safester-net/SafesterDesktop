package net.safester.clientserver.test;

import java.io.IOException;

import org.apache.commons.net.whois.WhoisClient;

public class WhoisTest {


	public static void main(String[] args) throws IOException {
	    org.apache.commons.net.whois.WhoisClient whois = new org.apache.commons.net.whois.WhoisClient();
	    whois.connect("whois.gandi.net"); //WhoisClient.DEFAULT_HOST);
	    //whois.connect(WhoisClient.DEFAULT_HOST);
	    
	    String domainWhois = whois.query("kawansoft.com");
	    System.out.println(domainWhois);
	    whois.disconnect();
	}

	
}