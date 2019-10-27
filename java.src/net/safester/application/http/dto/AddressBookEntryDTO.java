package net.safester.application.http.dto;

public final class AddressBookEntryDTO {

    private String emailAddress = null;
    private String name = null;
    private String company = null;
    private String cellPhone = null;

    public AddressBookEntryDTO() {

    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
	return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    public String getCompany() {
	return company;
    }

    public void setCompany(String company) {
	this.company = company;
    }

    public String getCellPhone() {
	return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
	this.cellPhone = cellPhone;
    }

    @Override
    public String toString() {
	return "AddressBookEntryDTO [emailAddress=" + emailAddress + ", name=" + name + ", company=" + company
		+ ", cellPhone=" + cellPhone + "]";
    }
    
}
