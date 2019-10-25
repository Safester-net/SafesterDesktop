/**
 * 
 */
package net.safester.application.compose.api;

import java.util.ArrayList;
import java.util.List;

import net.safester.application.http.dto.IncomingRecipientDTO;

/**
 * Helper to build easily and securely the list of incoming Recipients.
 * @author Nicolas de Pomereu
 *
 */
public class ApiRecipientsBuilder {

    public static final int RECIPIENT_TO = 1;
    public static final int RECIPIENT_CC = 2;
    public static final int RECIPIENT_BCC = 3;
    
    /**
     * Builds the IncomingRecipientDTO list from recipients core values.
     * @param emailAddresses	list of email addresses
     * @param names		the names corresponding to the email addresses
     * @param recipientTypes	the recipient types corresponding to the email addresses
     * @return the cleanly IncomingRecipientDTO list.
     */
    public static List<IncomingRecipientDTO> getRecipients(List<String> emailAddresses, List<String> names,
            List<Integer> recipientTypes) {
    
	if (emailAddresses == null || emailAddresses.isEmpty()) throw new NullPointerException("emailAddresses is null or empty!");
	if (names == null || names.isEmpty()) throw new NullPointerException("names is null or empty!");
	if (recipientTypes == null || recipientTypes.isEmpty()) throw new NullPointerException("recipientTypes is null or empty!");
	
	int size = emailAddresses.size();
	
	if (names.size() != size) {
	    throw new IndexOutOfBoundsException("names list size is different from reference emailAddresses size.");
	}
	if (recipientTypes.size() != size) {
	    throw new IndexOutOfBoundsException("recipientTypes list size is different from reference emailAddresses size.");
	}
	
	List<IncomingRecipientDTO> listRecipients = new ArrayList<>();
    
        for (int i = 0; i < emailAddresses.size(); i++) {
            IncomingRecipientDTO incomingRecipientDTO = new IncomingRecipientDTO();
            incomingRecipientDTO.setRecipientEmailAddr(emailAddresses.get(i));
            incomingRecipientDTO.setRecipientName(names.get(i));
            //incomingRecipientDTO.setRecipientPosition(i+1);
            
            int recipientType = recipientTypes.get(i);
            if (recipientType < RECIPIENT_TO || recipientType > RECIPIENT_BCC) {
        	throw new IllegalArgumentException("RecipientType is invalid: " + recipientType);
            }
            incomingRecipientDTO.setRecipientType(recipientType);
            listRecipients.add(incomingRecipientDTO);
        }
    
        return listRecipients;
    }

}
