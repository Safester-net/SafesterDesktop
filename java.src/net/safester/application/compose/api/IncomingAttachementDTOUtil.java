package net.safester.application.compose.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.awakefw.file.api.util.HtmlConverter;

import net.safester.application.http.dto.IncomingAttachementDTO;

public class IncomingAttachementDTOUtil {

    public IncomingAttachementDTOUtil() {
	// TODO Auto-generated constructor stub
    }

    /**
     * Transform the unencrypted files to attach as list of IncomingAttachementDTO instances.
     * @param enuncryptedFiles	the files to attach
     * @return the IncomingAttachementDTO objects in a List
     * @throws FileNotFoundException 
     */
    public static List<IncomingAttachementDTO> getAttachmentsAddingPgpExt(List<File> filesToAttach) throws FileNotFoundException {
        
        List<IncomingAttachementDTO> incomingAttachments = new ArrayList<>(); 
        int i = 1;
        
        for (File file : filesToAttach) {
            
            if (! file.exists() ) {
        	throw new FileNotFoundException("File to encrypt does not exist: "  + file);
            }
            
            IncomingAttachementDTO IncomingAttachementDTO = new IncomingAttachementDTO();
            String filename = HtmlConverter.toHtml(file.getName());
            IncomingAttachementDTO.setAttachPosition(i++);
            IncomingAttachementDTO.setFilename(filename + ".pgp");
            IncomingAttachementDTO.setSize(file.length());
            incomingAttachments.add(IncomingAttachementDTO);
        }
        
        return incomingAttachments;
    }

}
