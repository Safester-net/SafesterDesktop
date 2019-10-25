/**
 * 
 */
package net.safester.application.compose.api.test;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.lang3.SystemUtils;
import org.awakefw.commons.api.client.AwakeProgressManager;
import org.awakefw.commons.api.client.DefaultAwakeProgressManager;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;
import org.bouncycastle.openpgp.PGPPublicKey;

import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.SafesterLookAndFeelManager;
import net.safester.application.compose.api.ApiMessageSender;
import net.safester.application.compose.api.PGPPublicKeysBuilder;
import net.safester.application.compose.api.PgpTextEncryptor;
import net.safester.application.compose.api.engines.ApiEncryptAttachmentsUsingThread;
import net.safester.application.http.dto.IncomingMessageDTO;
import net.safester.clientserver.ServerParms;

/**
 * @author Nicolas de Pomereu
 */
public class ApiEncryptAttachmentsTest {

    public static List<File> getEnuncryptedFiles() {
	String rootFilesDir = SystemUtils.USER_HOME + File.separator + "Safester" + File.separator + "clearFiles"
		+ File.separator;
	String file1 = "apache-tomcat-8.5.30.exe";
	String file2 = "unlocker208.zip";
	List<File> enuncryptedFiles = new ArrayList<>();
	enuncryptedFiles.add(new File(rootFilesDir + file1));
	enuncryptedFiles.add(new File(rootFilesDir + file2));
	return enuncryptedFiles;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	
	boolean addFiles = true;
	
	SafesterLookAndFeelManager.setLookAndFeel();
	
	List<File> enuncryptedFiles = new ArrayList<>();

	if (addFiles) {
	    enuncryptedFiles =  getEnuncryptedFiles();
	}
	
	ApiMessageTestDataCreatorRaw apiMessageTestDataCreatorRaw = new ApiMessageTestDataCreatorRaw();
	String token = apiMessageTestDataCreatorRaw.getToken();
	IncomingMessageDTO incomingMessageDTO = apiMessageTestDataCreatorRaw.incomingMssageDTOBuild(enuncryptedFiles);

	System.out.println(incomingMessageDTO);
	System.out.println(new Date() + " /sendMessage API start...");

	System.out.println(new Date() + " using files:" + enuncryptedFiles);

	KawanHttpClient kawanHttpClient = new KawanHttpClient();
	AwakeProgressManager awakeProgressManager = new DefaultAwakeProgressManager();

	ApiMessageSender apiMessageSender = new ApiMessageSender(kawanHttpClient,
		incomingMessageDTO.getSenderEmailAddr(), token, incomingMessageDTO, enuncryptedFiles,
		awakeProgressManager);
	
        AwakeFileSession awakeFileSession = new AwakeFileSession(ServerParms.getAwakeSqlServerUrl(), incomingMessageDTO.getSenderEmailAddr(), token, null, null); // Now a simple Wrapper.
        Connection connection = new AwakeConnection(awakeFileSession);
        
        Set<String> keysId = new HashSet<>();
        keysId.add(incomingMessageDTO.getSenderEmailAddr());
        PGPPublicKeysBuilder pGPPublicKeysBuilder = new PGPPublicKeysBuilder(keysId, connection);
        List<PGPPublicKey> pGPPublicKeyList = pGPPublicKeysBuilder.buildPGPPublicKeys();
        
        PgpTextEncryptor pgpTextEncryptor = new PgpTextEncryptor(pGPPublicKeyList);
        String bodyEncrypted =  pgpTextEncryptor.encrypt(incomingMessageDTO.getBody());
        incomingMessageDTO.setBody(bodyEncrypted);
        
	JFrame jFrame = new JFrame();
	
	ApiEncryptAttachmentsUsingThread apiEncryptAttachmentsUsingThread = new ApiEncryptAttachmentsUsingThread(
		apiMessageSender, pGPPublicKeyList, jFrame);
	apiEncryptAttachmentsUsingThread.encryptAndsendMessage();
	
	// Because we start threads...
	while (true) {
	    Thread.sleep(50);
	}

    }


}
