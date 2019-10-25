/**
 * 
 */
package net.safester.application.compose.api.drafts;

import java.util.List;

import net.safester.application.http.dto.IncomingMessageDTO;

/**
 * Allows to build or retrieve a store of IncomingMessageDTO list;
 * @author Nicolas de Pomereu
 *
 */
public class MessageDraftsStore {

    /** The list of message in the drafts store */
    private List<IncomingMessageDTO> incomingMessageDTOList = null;

    /** 
     * Constructor.
     * @param incomingMessageDTOList 
     */
    public MessageDraftsStore(List<IncomingMessageDTO> incomingMessageDTOList) {
	this.incomingMessageDTOList = incomingMessageDTOList;
    }

    public List<IncomingMessageDTO> getIncomingMessageDTOList() {
        return incomingMessageDTOList;
    }
}
