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
package net.safester.application.engines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import org.bouncycastle.openpgp.PGPPublicKey;

import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.api.PgpFileStatusOne;
import com.safelogic.pgp.api.engines.CryptoEngine;
import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.pgp.api.util.parms.CmPgpCodes;
import com.safelogic.pgp.api.util.parms.PgpExtensions;
import com.safelogic.pgp.apispecs.PgpFileStatus;
import com.safelogic.utilx.Hex;

import net.safester.application.util.JOptionPaneNewCustom;

/**
 * A DecryptEngine is an decryption Thread, because of Progression Monitor
 * needs. All operations are done in the run().
 *
 */
public class AttachmentDecryptEngine extends Thread implements CryptoEngine {

    /**
     * The debug flag
     */
    //  protected boolean DEBUG = Debug.isSet(this);
    protected boolean DEBUG = true;

    /* National language messages */
    private MessagesManager messages = new MessagesManager();

//    /** The in string of the crypto operation */
//    private String m_inString = null;
    /**
     * The out String of the crypto operation
     */
    private String m_outString = null;

    /**
     * Files list in input of crypto operation
     */
    private List<File> m_filesIn = new Vector<File>();

    /**
     * Files list in output of crypto operation
     */
    private List<File> m_filesOut = new Vector<File>();

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
     * The list of messages, one per file
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
    private JFrame m_jframe = null;

    /**
     * Action!
     */
    private PgpActionsOne pgpActions = null;

    //Passphrase
    private char[] passphrase = null;

    //Key bloc
    private String keyBloc;

    List<String> failedIntegrityFileList;

    /**
     * Constructor
     *
     * @param inString The input String (maybe null). Only for Crypto in this
     * version
     * @param filesIn The Input File list for the crypto Operation
     * @param waiterEngine the Waiter Engine use to display "Please Wait" in
     * progress bar
     * @param jframe the calling JFrame
     */
    public AttachmentDecryptEngine(List<File> filesIn,
            WaiterEngine waiterEngine,
            JFrame jframe,
            String keyBloc,
            char[] passphrase) {
        if (m_filesIn == null) {
            throw new IllegalArgumentException("filesIn cannot be null");
        }
        if (keyBloc == null) {
            throw new IllegalArgumentException("keyBloc cannot be null");
        }
        if (passphrase == null) {
            throw new IllegalArgumentException("passphrase cannot be null");
        }
        m_current = 0;
        m_filesIn = filesIn;
        m_waiterEngine = waiterEngine;
        m_jframe = jframe;
        this.keyBloc = keyBloc;
        this.passphrase = passphrase;
        // Security: if user pass null instead of empty list...

    }

    /**
     * Run the Crypto Engine when this thread is started
     */
    public void run() {
        try {
            debug(new Date() + " DecryptEngine Begin 1");

            pgpActions = new PgpActionsOne(this);

            m_waiterEngine.setWaiterStop(true);

            // Compute the total length for all files & store in PgpAction
            long fileslength = computeFilesLength();
            pgpActions.setFilesLength(fileslength);
            debug(new Date() + " DecryptEngine fileslength: " + fileslength);
            String OpMessage = null;
            int returnCode = 0;

            File fileIn = null;
            File fileOut = null;
            failedIntegrityFileList = new ArrayList<String>();
            // Do crypto operations on all files
            for (int i = 0; i < m_filesIn.size(); i++) {

                try {
                    fileIn = m_filesIn.get(i);
                    fileOut = getFileOut(fileIn);

                    if (fileOut == null) {
                        debug(new Date() + " DecryptEngine fileOut null");
                        OpMessage = this.messages
                                .getMessage("ERROR_NOT_A_PGP_FILE");
                        OperationMessages.add(OpMessage);
                        m_filesOut.add(fileOut);
                        continue;
                    }

                    String fileName = fileIn.toString();
                    debug(new Date() + " DecryptEngine fileName:" + fileName);
                    // 1) Get fileIn PGP status
                    PgpFileStatus pgpFileStatus = new PgpFileStatusOne();
                    int pgpStatus = pgpFileStatus.getPgpStatus(fileIn);

                    if (pgpStatus != PgpFileStatus.STATUS_CRYPTED_ASYM) {
                        debug(new Date() + " DecryptEngine pgpStatus!= PgpFileStatus.STATUS_CRYPTED_ASYM");
                        OpMessage = this.messages
                                .getMessage("ERROR_NOT_A_PGP_FILE");
                        OperationMessages.add(OpMessage);
                    } else {
                        // Decrypt the fileIn

//                            System.out.println("fileIn : " + fileIn);
//                            System.out.println("fileOut: " + fileOut);
//                            System.out.println("keyBloc: " + keyBloc);
                        //fileIn = new File("C:\\temp\\safeshareit\\me.jpg.pgp");
                        //fileOut = new File("C:\\temp\\safeshareit\\me.jpg");
                        //keyBloc = FileUtils.readFileToString(new File("c:\\temp\\safeshareit\\nico@safelogic.com_PRIVATE.asc"));
                        //Thread.sleep(2000);
                        //Reset integrityCheck before each file
                        pgpActions.setIntegrityCheck(false);
                        returnCode = pgpActions.decryptPgpFromAscKey(fileIn, fileOut, keyBloc, passphrase);
                        //System.out.println("Integrity check : " + pgpActions.isIntegrityCheck());

                        fileIn.delete();

                        if (!pgpActions.isIntegrityCheck()) {
                            failedIntegrityFileList.add(fileOut.getName());
                        }
                        //  System.out.println("returnCode: " + returnCode);

                        OpMessage = getOperationMessage(returnCode);
                        OperationMessages.add(OpMessage);

                    }

                    if (fileOut != null) {
                        m_filesOut.add(fileOut);
                    }
                } catch (FileNotFoundException e) {
                    
                    // Hack NDP 10/03/18 - 13:02: Stop encryption/decryption and re-throw FileNotFoundException if file is locked (FileNotFoundException)

                    /*
                    // e.printStackTrace();
                    // Special handle for FileNotFoundException.
                    // It means there is a security/system access problem                
                    // Display a message that asks if we must continue:
                    FileSecurityChecker fileSecurityChecker = new FileSecurityChecker(m_jframe);
                    fileSecurityChecker.displayWarningAndAskForContinue(fileIn, fileOut, e);  
                    continue;
                     */
                    // m_end_message contains file name
                    if (fileIn == null) {
                        m_endMessage = "Unknown";
                    } else {
                        m_endMessage = fileIn.toString();
                    }
                    m_exception = e;
                    setCurrent(MAXIMUM_PROGRESS + 1);
                    return;
                }
            }

            m_returnCode = CmPgpCodes.PASSPHRASE_OK;

            //30/05/11 18:15 ABE : AttachmentDecryptEngine: setCurrent(MAXIMUM_PROGRESS + 1) everywhere
            setCurrent(MAXIMUM_PROGRESS + 1); // Says current = maximum; so task is over

            debug(new Date() + " DecryptEngine End");

        } catch (InterruptedException e) {
            e.printStackTrace();

            // This is a normal/regular interruption asked by user.
            debug("EncryptEngine Normal InterruptedException thrown by user Cancel!");
            //30/05/11 18:15 ABE : AttachmentDecryptEngine: setCurrent(MAXIMUM_PROGRESS + 1) everywhere
            setCurrent(MAXIMUM_PROGRESS + 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // m_endMessage = messages.getMessage("ERROR_DECRYPT_IMPOSSIBLE");

            JOptionPaneNewCustom.showException(m_jframe, e);

            //30/05/11 18:15 ABE : AttachmentDecryptEngine: setCurrent(MAXIMUM_PROGRESS + 1) everywhere
            setCurrent(MAXIMUM_PROGRESS + 1);
        } catch (Exception e) {
            e.printStackTrace();
            debug("EncryptEngine Exception thrown: " + e);

            m_exception = e;

            /*
             * if (m_fileOut != null) { m_fileOut.delete(); }
             */
            m_endMessage = messages.getMessage("OPERATION_OVER");
            //30/05/11 18:15 ABE : AttachmentDecryptEngine: setCurrent(MAXIMUM_PROGRESS + 1) everywhere
            setCurrent(MAXIMUM_PROGRESS + 1);
        }

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

    /**
     *
     * @param fIn the File In
     * @return the File Out whose name depends on the crypto operation type
     */
    private File getFileOut(File fIn) {
        File fileOut = null;

        String strFileIn = fIn.toString();

        if (PgpExtensions.isPgpEncryptedOrSignedExtension(strFileIn)) {
            String strFileOut = strFileIn.substring(0, strFileIn.lastIndexOf('.'));
            fileOut = new File(strFileOut);
        }

        return fileOut;

    }

    /**
     * Analayse the return code and return the corresponding Operation Message
     *
     * @param returnCode The Return Code of the last crypto operation
     */
    private String getOperationMessage(int returnCode) {

        debug("returnCode: " + returnCode);

        String opMessage = null;

        if (returnCode == CmPgpCodes.KEY_NOT_FOUND) {
            opMessage = messages.getMessage("ERROR_NO_SECRET_KEY");
        } else if (returnCode == CmPgpCodes.SIGN_BAD) {
            opMessage = messages.getMessage("SIGNATURE_BAD");
        } else if (returnCode == CmPgpCodes.SIGN_OK) {
            String signingKey = "";

            // Get The signing key infos
            PGPPublicKey publicKey = pgpActions.getLastSignPublicKey().getKey();

            if (publicKey != null) {
                // User Id is always the first of all User Ids for a Public Key
                Iterator<?> it = publicKey.getUserIDs();
                String userId = (String) it.next();

                String fingerprint = Hex.toString(publicKey.getFingerprint());
                String keyId = "0x" + fingerprint.substring(32);

                signingKey = messages.getMessage("MESSAGE_SIGNED_WITH_KEY") + " " + keyId + " - " + userId;
            }

            opMessage = messages.getMessage("SIGNATURE_OK");
            opMessage += " ";
            opMessage += signingKey;

            //opMessage += "(" + messages.getMessage("DECRYPT_FILE_OK") + ")";
        } else if (returnCode == CmPgpCodes.ENCRYPT_ASYM) {
            opMessage = messages.getMessage("DECRYPT_FILE_OK");
        }

        return opMessage;

    }

    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#getCurrent()
     */
    public int getCurrent() {
        return m_current;
    }

    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#setCurrent(int)
     */
    public void setCurrent(int current) {
        m_current = current;
    }

    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#getNote()
     */
    public String getNote() {
        return m_note;
    }

    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#setNote(java.lang.String)
     */
    public void setNote(String note) {
        m_note = note;
    }

    /**
     * @return the out String
     */
    public String getOutString() {
        return m_outString;
    }

    /**
     * @return the filesIn
     */
    public List<File> getFilesIn() {
        return m_filesIn;
    }

    /**
     * @return the filesOut
     */
    public List<File> getFilesOut() {
        return m_filesOut;
    }

    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#getReturnCode()
     */
    public int getReturnCode() {
        return m_returnCode;
    }

    /**
     * @return the message to display
     */
    public String getEndMessage() {
        return m_endMessage;
    }

    /**
     * @return the operation Messages : one Message per File
     */
    public List<String> getOperationMessages() {
        return OperationMessages;
    }

    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#getException()
     */
    public Exception getException() {
        return m_exception;
    }

    public List<String> getFailedIntegrityFileList() {
        return failedIntegrityFileList;
    }

//    private String removeUselessBlankLines(String in)
//    {
//        StringReader stringReader = new StringReader(in);
//        BufferedReader reader = new BufferedReader(stringReader);
//   //     StringWriter writer = new StringWriter();
//
//        String line = null;
//        String newBody = "";
//
//        try
//        {
//            while(( line = reader.readLine()) != null)
//            {                
//                if (! line.equals("") && ! line.equals(" "))
//                {                                        
//                    // Skip stupid GnuPg Lines
//                    if (line.contains(":") && ! line.contains("Version"))                        
//                    {
//                        continue;
//                    }
//                    
//                    // We must keep a CR/LF following the "Version" PGP  Tag
//                    if (line.contains("Version"))
//                    {
//                        newBody += line.trim() + Util.CR_LF + Util.CR_LF;
//                    }
//                    else
//                    {
//                        newBody += line.trim() + Util.CR_LF;
//                    }
//                    
//                }
//            }
//        }
//        catch (IOException ioe)
//        {
//            throw new IllegalArgumentException(ioe);
//        }
//        
//        //newBody = newBody.replaceAll(new String(converter.hexStringToBytes( "A0")), "");
//        //newBody = newBody.substring(0, newBody.length() -1);
//        
//        return newBody;
//    }
    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

}
