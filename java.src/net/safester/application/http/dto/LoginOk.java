package net.safester.application.http.dto;

import java.sql.Timestamp;

/**
 * DTP for Login succeed
 * 
 * @author abecquereau
 *
 */
public final class LoginOk {

    private final String status = "OK";

    private String token;
    private int product;
    private String name = null;
    private int userNumber = -1;
    private Timestamp endDate = null;


    public LoginOk(String token, int product, String name, int userNumber, Timestamp endDate) {
	super();
	this.token = token;
	this.product = product;
	this.name = name;
	this.userNumber = userNumber;
	this.endDate = endDate;
    }

    /**
     * @return the token
     */
    public String getToken() {
	return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
	this.token = token;
    }

    /**
     * @return the status
     */
    public String getStatus() {
	return status;
    }

    /**
     * @return the product
     */
    public int getProduct() {
	return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(int product) {
	this.product = product;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

   
    public int getUserNumber() {
        return userNumber;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
	return "LoginOk [status=" + status + ", token=" + token + ", product=" + product + ", name=" + name
		+ ", userNumber=" + userNumber + ", endDate=" + endDate + "]";
    }

}
