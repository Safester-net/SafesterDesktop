package net.safester.application.compose.api.engines;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import org.awakefw.commons.api.client.AwakeProgressManager;

import net.safester.application.compose.api.ApiMessageSender;
import net.safester.application.engines.WaiterEngine;
import net.safester.application.messages.MessagesManager;

public class ApiMessageSenderUsingThread {

    private MessagesManager messages = new MessagesManager();

    private ApiMessageSender apiMessageSender = null;
    JFrame jframe = null;

    /** All engine object (engine itself, monitor, listener etc.) */
    private WaiterEngine waiterEngine;
    private Timer apiMessageSenderEngineMonitor;
    private ProgressMonitor progressDialog;
    private AwakeProgressManager awakeProgressManager;

    private ApiMessageSenderEngine apiMessageSenderEngine;

    public ApiMessageSenderUsingThread(ApiMessageSender apiMessageSender, JFrame jframe) {
	
	if (apiMessageSender == null) {
	    throw new NullPointerException("apiMessageSender cannot be null!");
	}
	
	this.apiMessageSender = apiMessageSender;
	this.jframe = jframe;
    }

    public void sendMessage() {

	// And now, start the listener & the engine
	// Launch progress dialog
	progressDialog = new ProgressMonitor(jframe, null, null, 0, 100);
	progressDialog.setMillisToPopup(100); // Hyperfast popup

	awakeProgressManager = this.apiMessageSender.getAwakeProgressManager();

	// Start the engine & the listener
	apiMessageSenderEngine = new ApiMessageSenderEngine(apiMessageSender);

	waiterEngine = new WaiterEngine(this.messages.getMessage("PLEASE_WAIT"));
	waiterEngine.start();

	apiMessageSenderEngineMonitor = new Timer(500, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent event) {

		new ApiMessageSenderEngineListener(
			apiMessageSenderEngine, 
			awakeProgressManager, 
			waiterEngine,
			apiMessageSenderEngineMonitor, 
			progressDialog, 
			jframe);

	    }
	});

	apiMessageSenderEngine.start();
	apiMessageSenderEngineMonitor.start();

    }

}
