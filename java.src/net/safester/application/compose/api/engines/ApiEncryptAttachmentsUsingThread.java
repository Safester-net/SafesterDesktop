package net.safester.application.compose.api.engines;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import org.bouncycastle.openpgp.PGPPublicKey;

import net.safester.application.compose.api.ApiMessageSender;
import net.safester.application.engines.WaiterEngine;
import net.safester.application.messages.MessagesManager;

public class ApiEncryptAttachmentsUsingThread {

    private MessagesManager messages = new MessagesManager();

    private ApiMessageSender apiMessageSender = null;
    JFrame jframe = null;

    /** All engine object (engine itself, monitor, listener etc.) */
    private ApiAttachmentEncryptEngine apiAttachmentEncryptEngine;
    private WaiterEngine waiterEngine;
    private Timer apiMessageSenderEngineMonitor;
    private ProgressMonitor progressDialog;

    private List<PGPPublicKey> pgpPublicKeys  = new ArrayList<>();
    
    public ApiEncryptAttachmentsUsingThread(ApiMessageSender apiMessageSender, List<PGPPublicKey> pgpPublicKeys, JFrame jframe) {
	
	if (apiMessageSender == null) {
	    throw new NullPointerException("apiMessageSender cannot be null!");
	}
	if (pgpPublicKeys == null) {
	    throw new NullPointerException("pgpPublicKeys cannot be null!");
	}

	this.apiMessageSender = apiMessageSender;
	this.pgpPublicKeys = pgpPublicKeys;
	this.jframe = jframe;
    }

    public void encryptAndsendMessage() throws InterruptedException {

	// And now, start the listener & the engine
	// Launch progress dialog
	progressDialog = new ProgressMonitor(jframe, null, null, 0, 100);
	progressDialog.setMillisToPopup(100); // Hyperfast popup

	waiterEngine = new WaiterEngine(this.messages.getMessage("PLEASE_WAIT"));
	waiterEngine.start();

	// Start the engine & the listener
	apiAttachmentEncryptEngine = new ApiAttachmentEncryptEngine(apiMessageSender.getUnencryptedFiles(), pgpPublicKeys, waiterEngine);

	apiMessageSenderEngineMonitor = new Timer(500, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent event) {

		new ApiAttachmentEncryptEngineListener(apiAttachmentEncryptEngine, waiterEngine,
			apiMessageSenderEngineMonitor, progressDialog, apiMessageSender, jframe);

	    }
	});

	apiAttachmentEncryptEngine.start();
	apiMessageSenderEngineMonitor.start();

    }

}
