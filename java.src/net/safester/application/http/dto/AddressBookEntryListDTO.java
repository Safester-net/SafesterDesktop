package net.safester.application.http.dto;

import java.util.List;

public final class AddressBookEntryListDTO {
	private String status = "OK";
	private List<AddressBookEntryDTO> addressBookEntries;
	
	public AddressBookEntryListDTO() {
		
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the addressBookEntries
	 */
	public List<AddressBookEntryDTO> getAddressBookEntries() {
		return addressBookEntries;
	}

	/**
	 * @param addressBookEntries the addressBookEntries to set
	 */
	public void setAddressBookEntries(List<AddressBookEntryDTO> addressBookEntries) {
		this.addressBookEntries = addressBookEntries;
	}

	@Override
	public String toString() {
	    return "AddressBookEntryListDTO [status=" + status + ", addressBookEntries=" + addressBookEntries + "]";
	}
	
}
