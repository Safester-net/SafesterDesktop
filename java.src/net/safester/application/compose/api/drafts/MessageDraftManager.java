/**
 * 
 */
package net.safester.application.compose.api.drafts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import net.safester.application.compose.api.GsonUtil;
import net.safester.application.http.dto.IncomingMessageDTO;

/**
 * Saves the draft message.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class MessageDraftManager {

    private int userNumber = 1;

    /**
     * Constructor.
     *
     * @param userNumber the key id owner user number
     */
    public MessageDraftManager(int userNumber) {
	this.userNumber = userNumber;
 
    }

    /**
     * Saves draft by adding current IncomingMessageDTO in Json format to
     * user.home/.safester/SafesterDrafts.txt
     * 
     * @param incomingMessageDTO    the message to save
     * @throws IOException
     */
    public void save(IncomingMessageDTO incomingMessageDTO) throws IOException {

	File safesterDraftsTxt = getSafesterDraftsTxt(this.userNumber);

	List<IncomingMessageDTO> incomingMessageDTOList = null;
	if (!safesterDraftsTxt.exists()) {
	    incomingMessageDTOList = new ArrayList<>();
	} else {
	    String jsonString = FileUtils.readFileToString(safesterDraftsTxt);
	    MessageDraftsStore messageDraftsStore = GsonUtil.fromJson(jsonString, MessageDraftsStore.class);
	    incomingMessageDTOList = messageDraftsStore.getIncomingMessageDTOList();
	}
       
        if (incomingMessageDTO.getMessageId() < 0 ) {
            // If there is no message IF+D, this is a non existing composeed Draft
            int max = 0;
            for (IncomingMessageDTO incomingMessageDTO1 : incomingMessageDTOList) {
                max = Math.max(max, incomingMessageDTO1.getMessageId());
            }
            incomingMessageDTO.setMessageId(max + 1); // Always add 1 to be cautious
            incomingMessageDTOList.add(incomingMessageDTO);
        }
        else {
            Set<IncomingMessageDTO> incomingMessageDTOSet =  new HashSet<>(incomingMessageDTOList);
            incomingMessageDTOSet.remove(incomingMessageDTO); // Always remove to be suer he add will be tekan into account
            incomingMessageDTOSet.add(incomingMessageDTO);
            incomingMessageDTOList = new ArrayList<>(incomingMessageDTOSet);
        }
       
	MessageDraftsStore messageDraftsStore = new MessageDraftsStore(incomingMessageDTOList);
	FileUtils.write(safesterDraftsTxt, GsonUtil.getJSonString(messageDraftsStore));
    }

    /**
     * Creates the user.home/.safester/SafesterDrafts.txt it does not exists.
     * @param userNumber    the owner
     * @return the drafts file
     */
    public static  File getSafesterDraftsTxt(int userNumber) {
	File safesterDraftsTxt = new File(getSafesterDir().toString() + File.separator + "SafesterDrafts_" + userNumber + ".txt");
	return safesterDraftsTxt;
    }

    /**
     * Returns user.home/.Safester dir
     * @return 
     */
    private static File getSafesterDir() {
	String userHome = System.getProperty("user.home");
	File safesterDir = new File(userHome + File.separator + ".safester");

	if (!safesterDir.exists()) {
	    safesterDir.mkdir();
	}

	return safesterDir;
    }

    public void delete(List<Integer> messageIdList) throws IOException {
	File safesterDraftsTxt = getSafesterDraftsTxt(this.userNumber);

	if (!safesterDraftsTxt.exists()) {
	    return;
	} else {
	    String jsonString = FileUtils.readFileToString(safesterDraftsTxt);
	    MessageDraftsStore messageDraftsStore = GsonUtil.fromJson(jsonString, MessageDraftsStore.class);
	    List<IncomingMessageDTO>  incomingMessageDTOList = messageDraftsStore.getIncomingMessageDTOList();
            
            Set<IncomingMessageDTO> incomingMessageDTOSet =  new HashSet<>(incomingMessageDTOList);
                        
            for (int messageId : messageIdList) {
                for (IncomingMessageDTO incomingMessageDTO : incomingMessageDTOList) {
                    if (incomingMessageDTO.getMessageId() == messageId) {
                        incomingMessageDTOSet.remove(incomingMessageDTO);
                    }
                }
            }
            
            if (incomingMessageDTOSet.isEmpty()) {
                safesterDraftsTxt.delete();
            }
            
            incomingMessageDTOList = new ArrayList<>(incomingMessageDTOSet);
            
            messageDraftsStore = new MessageDraftsStore(incomingMessageDTOList);
            FileUtils.write(safesterDraftsTxt, GsonUtil.getJSonString(messageDraftsStore));
        
	}
    }

}
