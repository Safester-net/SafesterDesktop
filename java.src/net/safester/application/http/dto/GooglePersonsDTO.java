package net.safester.application.http.dto;

import java.util.List;

import com.google.api.services.people.v1.model.Person;

public class GooglePersonsDTO {

    private String status = "OK";
    private List<Person> finalPersons = null;
    
  
    public GooglePersonsDTO(List<Person> finalPersons) {
	super();
	this.finalPersons = finalPersons;
    }

    public List<Person> getFinalPersons() {
        return finalPersons;
    }

    @Override
    public String toString() {
	return "GooglePersonsDTO [status=" + status + ", finalPersons=" + finalPersons + "]";
    }

}
