/*
 * This file is part of Safester.                                    
 * Copyright (C) 2019, KawanSoft SAS
 * (https://www.Safester.net). All rights reserved.                                
 *                                                                               
 * Safester is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * Safester is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package net.safester.application.compose.api.engines;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;

import org.bouncycastle.openpgp.PGPPublicKey;

import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.api.engines.CryptoEngine;
import com.safelogic.pgp.api.util.parms.CmPgpCodes;

import net.safester.application.engines.WaiterEngine;
import net.safester.application.parms.Parms;
import net.safester.application.util.CacheFileHandler;

public class ApiAttachmentEncryptEngine extends Thread implements CryptoEngine {

    /**
     * Files list in input of crypto operation
     */
    private List<File> m_filesIn = new Vector<File>();

    /**
     * Files list in output of crypto operation
     */
    private List<File> m_filesOut = new Vector<File>();

    /**
     * List of recipients for crypto operation
     */
    private List<PGPPublicKey> m_recipients = null;

    /**
     * Current value of progression out of MAXIMUM_PROGRESS
     */
    private int m_current;

    /**
     * Note to pass to the progression bar
     */
    private String m_note = null;

    /**
     * The return code
     */
    private int m_returnCode = CmPgpCodes.RC_ERROR;

    /**
     * The list of messages, one per file (not used in encryption)
     */
    private List<String> OperationMessages = new Vector<String>();

    /**
     * The display message that will be displayed at of process by the action
     * listener
     */
    private String m_endMessage = null;

    /**
     * The Exception thrown if something *realy* bad happened
     */
    private Exception m_exception = null;

    /**
     * The waiter engine
     */
    private WaiterEngine m_waiterEngine = null;

    /**
     * The calling JFrame
     */
    //private JFrame m_jframe = null;

    public ApiAttachmentEncryptEngine(
            List<File> filesIn,
            List<PGPPublicKey> recipients,
            WaiterEngine waiterEngine
            ) {

	if (filesIn == null) {
	    throw new NullPointerException("filesIn cannot be null!");
	}
	if (recipients == null) {
	    throw new NullPointerException("recipients cannot be null!");
	}
	if (waiterEngine == null) {
	    throw new NullPointerException("waiterEngine cannot be null!");
	}
	
        //jOptionPaneDebugShowMessage("filesIn : "  + filesIn.get(0));
        //jOptionPaneDebugShowMessage("inString: " + inString);
        //jOptionPaneDebugShowMessage("action  : " + action);
        m_current = 0;

        // Security: if user pass null instead of empty list...
        if (m_filesIn == null) {
            m_filesIn = new Vector<File>(); // new empty list
        }

        m_filesIn = filesIn;
        m_recipients = recipients;
        m_waiterEngine = waiterEngine;
    }

    @Override
    public int getCurrent() {
        return m_current;
    }

    @Override
    public void setCurrent(int i) {
        this.m_current = i;
    }

    @Override
    public String getNote() {
        return m_note;
    }

    @Override
    public void setNote(String string) {
        m_note = string;
    }

    @Override
    public List<File> getFilesIn() {
        return m_filesIn;
    }

    @Override
    public List<File> getFilesOut() {
        return m_filesOut;
    }

    @Override
    public int getReturnCode() {
        return m_returnCode;
    }

    @Override
    public List<String> getOperationMessages() {
        return OperationMessages;
    }

    @Override
    public String getEndMessage() {
        return m_endMessage;
    }

    @Override
    public Exception getException() {
        return m_exception;
    }

    @Override
    public void run() {
        long fileslength = computeFilesLength();

        PgpActionsOne pgpActions = new PgpActionsOne(this);
        pgpActions.setIntegrityCheck(true);
        pgpActions.setFilesLength(fileslength);

        File fileIn = null;
        File fileOut = null;
        m_waiterEngine.setWaiterStop(true);
        CacheFileHandler cacheFileHandler = new CacheFileHandler();

        try {
            for (int i = 0; i < m_filesIn.size(); i++) {
                fileIn = m_filesIn.get(i);
                fileOut = getFileOut(fileIn);
                cacheFileHandler.addCachedFile(fileOut.toString());
                pgpActions.encryptFilePgp(fileIn, fileOut, m_recipients);
                //pgpActions.en
                m_filesOut.add(fileOut);
            }

            m_returnCode = CmPgpCodes.RC_OK;
            setCurrent(MAXIMUM_PROGRESS + 1);
        } catch (FileNotFoundException e) {

            // Hack NDP 10/03/18 - 13:02: Stop encryption/decryptions and re-throw FileNotFoundException if file is locked (FileNotFoundException)
            
            /*
            // Special handle for FileNotFoundException.
            // It means there is a security/system access problem
            // Display a message that asks if we must continue:
            FileSecurityChecker fileSecurityChecker = new FileSecurityChecker(m_jframe);
            try {
                fileSecurityChecker.displayWarningAndAskForContinue(fileIn, fileOut, e);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
             */
            // m_end_message contains file name
            if (fileIn == null) {
                m_endMessage = "Unknown";
            } else {
                m_endMessage = fileIn.toString();
            }
            m_exception = e;
            setCurrent(MAXIMUM_PROGRESS + 1);

        } catch (InterruptedException e) {
            // This is a normal/regular interruption asked by user.
            // 30/05/11 11:53 ABE : Fix: setCurrent(MAXIMUM_PROGRESS + 1) in Exceptions
            setCurrent(MAXIMUM_PROGRESS + 1);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

            m_exception = e;

            // 30/05/11 11:53 ABE : Fix: setCurrent(MAXIMUM_PROGRESS + 1) in Exceptions
            setCurrent(MAXIMUM_PROGRESS + 1);
        }
    }

    private File getFileOut(File fileIn) {
        String fileInName = fileIn.getName();
        return new File(fileIn.getParent()
                + System.getProperty("file.separator")
                + fileInName//fileIn.getName()
                + Parms.ENCRYPTED_FILE_EXT);
    }

    /**
     *
     * @return the total length of all files
     */
    private long computeFilesLength() {
        long filesLength = 0;
        for (int i = 0; i < m_filesIn.size(); i++) {
            File fileIn = m_filesIn.get(i);
            filesLength += fileIn.length();
        }
        return filesLength;
    }

    @Override
    public String getOutString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
