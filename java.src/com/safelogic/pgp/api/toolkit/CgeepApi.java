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
package com.safelogic.pgp.api.toolkit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownServiceException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.bouncycastle.openpgp.PGPDataValidationException;

import com.safelogic.pgp.api.HttpTransferOne;
import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.KeyImageHandlerOne;
import com.safelogic.pgp.api.KeySignatureHandlerOne;
import com.safelogic.pgp.api.KeyTransferHkpServerOne;
import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.api.PgpKeyUtil;
import com.safelogic.pgp.api.PgpSymActionsOne;
import com.safelogic.pgp.api.PubkeyDescriptorOne;
import com.safelogic.pgp.api.engines.CryptoEngine;
import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.pgp.api.util.parms.CmPgpCodes;
import com.safelogic.pgp.api.util.parms.Parms;
import com.safelogic.pgp.apispecs.HttpTransfer;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.KeyImageHandler;
import com.safelogic.pgp.apispecs.KeySignatureHandler;
import com.safelogic.pgp.apispecs.KeyTransferHkpServer;
import com.safelogic.pgp.apispecs.PgpActions;
import com.safelogic.pgp.apispecs.PgpSymActions;
import com.safelogic.pgp.apispecs.PubkeyDescriptor;
import com.safelogic.pgp.apispecs.StringTriplet;
import com.safelogic.pgp.util.FileUtil;
import com.safelogic.pgp.util.UrlUtil;
import com.safelogic.utilx.Debug;
import com.safelogic.utilx.Hex;

public class CgeepApi implements ICgeepApi {
            
    /** Time to wait after a thread start */
    private static final int THREAD_START_WAIT = 500;

    /** Maximum value of progression bar (defaults to 100)*/
    public static final int MAXIMUM_PROGRESS = 100;
    
    /** The debug flag */ 
    protected boolean DEBUG = Debug.isSet(this);
    
    private final static String SEP     = System.getProperty("file.separator");
	private final static String CR_LF   = System.getProperty("line.separator");
	
    // null Byte
    public static final byte[] B_NULL = (new String ("null")).getBytes() ;
    
	private final static String seedFileName = "randseed.bin";

	private String PUB_LINE = "pub   {0}/{1} {2}" + CR_LF;
	private String UID_LINE = "uid                  {0}" + CR_LF;
	
	private static final String DEFAULT_ENCODING = "ISO-8859-1";
	
	private static final String DEFAULT_SYMMETRIC_ALGO = "AES-256";
	
	private static final String[] SYMMETRIC_ALGO = {
		"CAST-128", 
		"Blowfish-128", 
		"AES-128", 
		"AES-192", 
		"AES-256", 
		"3DES-168"
	};
	
	private static final String [] ASYMMETRIC_ALGO = {
		"DSA/Elgamal",
		"RSA"
	};
	
	// 
	// Configuration values
	// 
	   /** Default keyring directory */
    private String keyringDirectory = System.getProperty("user.home") + SEP;
    
    /** 
     * If true, all files with be encrypted and/or signed with armored format
     * true is the best solution for email sending/receptions 
     */
    private boolean armorModeForFiles = false;
    
	private String charset = DEFAULT_ENCODING;
	
	private String symmetricAlgorithm = DEFAULT_SYMMETRIC_ALGO;
	
	/** Recipients for public key encryption */
	private List<String> m_recipients = new Vector<String>();
	
    /** The container for the  Command parameters passed from C++ */
    private List<String> argvCommandLineParameters = new Vector<String>();
                
    // END Configuration values
    
	/**
	 * CONSTRUCTOR - No code
	 */
	public CgeepApi ()
	{
		//Set keyring directory in JVM. Can not throw an Exception
		UrlUtil.setKeyDirectory(keyringDirectory);	
		
        // If it's impossible to copy the non restricted policy files
        // This means 
        // 1) User is not Admin (windows security restriction on Vista)
        // 2) AND so user can not use strong encryption.
        //
		
        if (! UrlUtil.copyNonRestricedPolicyFilesToJavaHomeLibSecurity())
        {
            // French or English Messages   
            MessagesManager messages = new  MessagesManager();                
            String message = messages.getMessage("STRONG_ENCRYPTION_ADMIN_ONLY") 
                            + FileUtil.CR_LF
                            + UrlUtil.getJavaHomeLibSecurityDir();

           System.out.println(message);
        }		
                        
	}
		
    //////////////////////////////////////////////////////////////////////////
    // Error management functions                                           //
    //////////////////////////////////////////////////////////////////////////	

    //@Override
    public boolean isOperationOk() {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        return taskContainer.isOperationOk();
    }
	
    //@Override
    public String getErrorCode() 
    {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        return taskContainer.getErrorCode();
    }

    //@Override
    public String getException() {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        return taskContainer.getException();
    }

    //@Override
    public String getStackTrace() {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        return taskContainer.getStackTrace();
    }        
    
    //@Override
    public boolean isOperationOkForTask(int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        return taskContainer.isOperationOk();
    }

    //@Override
    public String getErrorCodeForTask(int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        return taskContainer.getErrorCode();
    }

    //@Override
    public String getExceptionForTask(int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        return taskContainer.getException();
    }
    
    //@Override
    public String getStackTraceForTask(int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        return taskContainer.getStackTrace();
    }
    
    //////////////////////////////////////////////////////////////////////////
    // Version                                                              //
    //////////////////////////////////////////////////////////////////////////
    
    // 08/10/09 17:55 NDP: getVersion() is now callable from c++ (to test dll)
    //@Override    
    public String getVersion()
    {
        String version = ApiVersion.getVersionWithDate();
        
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            // Just to check if license is OK!
            taskContainer.setOperationOk();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);   
            
            // Return the Version + the error code
            version += " " + e.getMessage();
        }
        
        return version;
    }    
        
    //////////////////////////////////////////////////////////////////////////
    // Command Line                                                         //
    //////////////////////////////////////////////////////////////////////////
        
    /**
     * Add a parameter to the command line to execute
     * @param parameter  the String parameter to add to the command before execution
     */
    //@Override
    public void addCommandLineParameter(String parameter)
    {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
                       
            argvCommandLineParameters.add(parameter);
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }
    }
        
    /**
     * Execute the command (for c++ dll) with the parameters passed by addParameter
     */
    //@Override
    public void executeCommandLine() 
    {  
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
                
        try
        {
            taskContainer.setOperationOk();
            
            String [] args 
                = argvCommandLineParameters.toArray(new String[argvCommandLineParameters.size()]);
            
            for (int i = 0; i < args.length; i++)
            {
                debug("arg["+ i + "]" + args[i]);
            }
                                   
            CgeepCmdLine cgeepCmdLine = new CgeepCmdLine();            
            cgeepCmdLine.executeCommandFromJava(this, args);
            
            //System.out.println("Done.");
        }     
        catch (Exception e)
        {   
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            
            if (! taskContainer.isLicenseValid())
            {
                System.out.println(taskContainer.getErrorCode());                
            }
            else
            {
                e.printStackTrace();
            }
           
        }
    }    
    
    
    //////////////////////////////////////////////////////////////////////////
    // Key management functions                                             //
    //////////////////////////////////////////////////////////////////////////
    
    /**
     * @return the directory that contains the keyring to use
     */
    //@Override
    public String getKeyRingDirectory()
    {
        return this.keyringDirectory; 
    }
    
    //@Override
    public void setKeyRingDirectory(String directory) {
        
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {            
            //System.out.println("setKeyRingDirectory(String directory)");
            
            taskContainer.setOperationOk();

            if(directory == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            File dir = new File(directory);

            //If directory does not exists throw error
            if(!dir.exists())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_DIRECTORY_NOT_FOUND, null);
                return;
            }

            if(!dir.isDirectory())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_NOT_A_DIRECTORY, null);
                return;
            }
            //If directory is not readable and writebale throw error
            if(!dir.canRead())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_DIRECTORY_CANT_READ, null);
                return;
            }

            if(!dir.canWrite())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_DIRECTORY_CANT_WRITE, null);
                return;
            }
            if(!directory.endsWith(SEP))
            {
                directory += SEP;
            }
            this.keyringDirectory = directory;
            UrlUtil.setKeyDirectory(directory);
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }
    }
    

    //@Override
    public void generateKeyPair(String userId, char[] passphrase,
            String algoAsym, int keyLengthAsym, String algoSym,
            String expirationDate) 
    {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
                        
            //Check parameters
            if(userId == null || passphrase == null || algoAsym == null || algoSym == null || expirationDate == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            
            if((keyLengthAsym == 0)||(keyLengthAsym % 128 != 0))
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_INVALID_ASYMMETRIC_KEY_LENGTH, null);
                return;
            }

            //Check algo sym exists in system
            if(!Arrays.asList(SYMMETRIC_ALGO).contains(algoSym))
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_INVALID_SYMMETRIC_ALGO, null);
                return;
            }
            
            //Check alog Asym exists in system
            if(!Arrays.asList(ASYMMETRIC_ALGO).contains(algoAsym))
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_INVALID_ASYMMETRIC_ALGO, null);
                return;
            }

            Date expDate = null;
            if(!expirationDate.equalsIgnoreCase("NEVER"))
            {
                SimpleDateFormat df = new SimpleDateFormat("yy/mm/dd");    
                try
                {
                    //Try to extract date from string
                    expDate = df.parse(expirationDate);
                }
                catch(Exception e)
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_EXPIRATION_DATE_INVALID_FORMAT, e);
                    return;
                }
            }
            //Get seed from seed file (file will be created if not present)
            byte[] seed = getSeed();
            if(seed == null)
            {
                //Error manager already contains error code
                return;
            }

            //Extract sym algo length from algo name
            String strLength = algoSym.substring(algoSym.indexOf('-')+1);
            
            algoSym = algoSym.substring(0, algoSym.indexOf('-'));
            int symAlgoLength = 0;
            try
            {
                symAlgoLength = Integer.parseInt(strLength);
            }
            catch(Exception e )
            {
                //Should never happens
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
                return;
            }

            //All parameters seems to be ok, create keypair
            KeyHandler kh = new KeyHandlerOne();

            //Test keyring existence and accessibility
            if(keyringExist())
            {
                if(isOperationOk())     
                {
                    //Mandatory check of errorMan status to ensure there was no 
                    //problem accessing keyring (errorMan can contains an error code set in keyringExist()
                    
                    //Add new key pair to existing keyring
                    kh.generateAndAddKeyPair(userId, passphrase, algoAsym, keyLengthAsym, algoSym, symAlgoLength, seed, expDate);
                }
                return;
            }
            
            //Create new keyring
            kh.generateAndStoreKeyPair(userId, passphrase, algoAsym, keyLengthAsym, algoSym, symAlgoLength, seed, expDate);
            
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }
    }

    //@Override
    public void changePassphrase(String userId, char[] passphraseOld,
            char[] passphraseNew) 
    {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            
            if(userId == null || passphraseOld == null || passphraseNew == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            KeyHandler kh = new KeyHandlerOne();
            boolean isPassphraseValid = kh.isPassphraseValid(userId, passphraseOld);
            if(!isPassphraseValid)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_BAD_PASSPHRASE, null);
                return;
            }

            kh.changePassphrase(userId, passphraseOld, passphraseNew);
            
        }
        catch(Exception e )
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return;
        }
    }   
    
    /**
     * Test keyring existence and accessibility
     * @return  true if both pubring and secring existsand is accessible false otherwise
     *              If keyring is not accessible an error code is set to errorMan
     */
    boolean keyringExist() 
    {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            File pubring = new File(keyringDirectory + "pubring.pkr");
            File secring = new File(keyringDirectory + "secring.skr");
            if(!pubring.exists() || !secring.exists())
            {
                return false;
            }
            if(!pubring.canRead() || !pubring.canWrite() || !secring.canRead() || !secring.canWrite())
            {
                //Keyring must be read/write mode
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_READ_WRITE_MODE, null);
            }
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_ACCESSING_KEYRING, e);
            return false;
        }
        return true;
    }    
    

    //@Override
    public String listKeys(String substring) 
    {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            //Check keyring existance/accessibility
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return null;
            }
            KeyHandler kh = new KeyHandlerOne();
            //List all keys in public keyring
            List<String> pubKeyIds = kh.getPublicKeyRingUserIds();
            if(substring != null)
            {
                //Remove all keys that does not match searched substring
                pubKeyIds = filterList(pubKeyIds, substring);
                
//                if(!isOperationOk())
//                {
//                    //errorMan already contains error code
//                    return null; 
//                }
            }
            //Format output
            String output = buildListResult(pubKeyIds);
            
            return output;
        }
        catch (Exception e )
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return null;
        }
        
    }

    /**
     * Format list of key to match search result :
     * <tt>
     * pub   1024D/08941581 2008-12-15
     * uid                  Lattner Daniel <lattner@officehouse.cz>
     * sub   2048g/B139C386 2008-12-15
     *   
     * pub   1024/4D4FC24F 2006-05-09
     * uid                  Nicolas de Pomereu <ndepomereu@safelogic.com>
     * uid                  [jpeg image of size 3907]
     * sub   1024/8C1EA9FA 2006-05-09
     * </tt>
     * @param pubKeyIds
     * @return
     */
    private String buildListResult(List<String> pubKeyIds) 
    {
        String output = "";
        
        for(String keyId : pubKeyIds)
        {
            output += getKeyInfos(keyId);
        }
        return output;   
    }

    /**
     * Get all infos of a key and format it for a correct print
     * @param keyId         The user Id of searched key
     * @return              An output formatted string containing infos
     */
    private String getKeyInfos(String keyId)
    {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            String formattedString = "";
            KeyHandler kh = new KeyHandlerOne();
            PublicKey pubKey = kh.getPgpPublicKey(keyId);
            PubkeyDescriptor pkd = new PubkeyDescriptorOne(pubKey);
            
            String fingerprint = pkd.getFingerprint();
            String pgpKeyId = fingerprint.substring(fingerprint.length() -8);
            String keyLength = Integer.toString(pkd.getLength());
            
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String keyDate = df.format(pkd.getCreationDate());
            
            MessageFormat messageFormat = new MessageFormat(PUB_LINE);
            Object [] keyDetails = {keyLength, pgpKeyId, keyDate};
            
            formattedString = messageFormat.format(keyDetails);
            

            String keyBloc = kh.exportAscPgpPublicKey(keyId);
            //Get all userId of key
            List<String> userIdInKey = CgeepApiTools.getUserIdsFromBloc(keyBloc);
            for(String userId : userIdInKey)
            {
                messageFormat = new MessageFormat(UID_LINE);
                Object[] id = {userId};
                
                formattedString += messageFormat.format(id);
            }
            
            return formattedString;
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return null;
        }
    }

    /**
     * Create a new List of keyIds with all keyIds of orignal list matching substring
     * @param pubKeyIds             Original list of keyIds
     * @param substring             searched String 
     * @return                      The new list
     */
    private List<String> filterList(List<String> pubKeyIds, String substring) 
        throws Exception
    {
        List<String> newList = new Vector<String>();
        KeyHandler kh = new KeyHandlerOne();
        for(String keyId : pubKeyIds)
        {
            if(keyId.toLowerCase().contains(substring.toLowerCase()))
            {
                //Key id contains searched string
                newList.add(keyId);
            }
            else
            {
                //Extract Key bloc
                String keyBloc = kh.exportAscPgpPublicKey(keyId);
                //Get all userId of key
                List<String> userIdInKey = CgeepApiTools.getUserIdsFromBloc(keyBloc);
                for(String userId : userIdInKey)
                {
                    //If user id contains searched string add to new List 
                    if(userId.toLowerCase().contains(substring.toLowerCase()))
                    {
                        newList.add(keyId);
                        break;
                    }
                }
            }
        }
        return newList;

    }    
    
    //@Override
    public void exportPublicKey(String userId, String file) 
    {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try{
            taskContainer.setOperationOk();
            
            //Check parms
            if(userId == null || file == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            KeyHandler kh = new KeyHandlerOne();
            
            //Retreive key bloc
            String keyBloc = "";
            try
            {
                keyBloc = kh.exportAscPgpPublicKey(userId);     
            }
            catch(KeyException ke)
            {
                //No key matching key id founded
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_PUB_KEY_NOT_FOUND, ke);
                return;
            }
            
            File outFile = new File(file);
            
            if(outFile.exists() && !outFile.canWrite())
            {
                //File can't be written
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_FILE_NOT_WRITABLE, null);
                return;
            }
            
            //Write key bloc into file
            OutputStream out = new FileOutputStream(outFile);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            bos.write(keyBloc.getBytes() );
            bos.flush();
            bos.close();
            out.close();
        
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }

    }    
    
    //@Override
    public void exportPublicAndPrivateKey(String userId, String file) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try{
            taskContainer.setOperationOk();
            
            //Check parms
            if(userId == null || file == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            
            KeyHandler kh = new KeyHandlerOne();
            String privKeyBloc = "";
            try
            {
                privKeyBloc = kh.exportAscPgpPrivateKey(userId);
            }
            catch(KeyException ke)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_PRIV_KEY_NOT_FOUND, ke);
                return;
            }
            
            String pubKeyBloc = "";
            try
            {
                pubKeyBloc = kh.exportAscPgpPublicKey(userId);
            }
            catch(KeyException ke)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_PUB_KEY_NOT_FOUND, ke);
                return;
            }
            
            String keyPair = privKeyBloc + CR_LF + pubKeyBloc;
            File outFile = new File(file);
            
            if(outFile.exists() && !outFile.canWrite())
            {
                //File can't be written
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_FILE_NOT_WRITABLE, null);
                return;
            }
            
            //Write key bloc into file
            OutputStream out = new FileOutputStream(outFile);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            bos.write(keyPair.getBytes() );
            bos.flush();
            bos.close();
            out.close();
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }

    }    
    
    //@Override
    public void importKey(String file) 
    {   TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
    
        try{
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            if(file == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            File keyFile = new File(file);
            if(!keyFile.exists())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return;
            }
            if(!keyFile.canRead())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                return;
            }
            
            KeyHandler kh = new KeyHandlerOne();
            try
            {
                kh.importPgpPublicAndPrivateKeyFromAsc(keyFile);
            }
            catch(Exception e)
            {
                if(e.toString().contains("Collection already contains a key"))
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_ALREADY_IN_KEYRING, null);
                    return;
                }
                throw e;
            }
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }
    }    
    
    //@Override
    public void signKey(String userId, String userIdPrivate, char[] passphrase) {
        
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            if(userId == null || userIdPrivate == null || passphrase == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            
            KeyHandler kh = new KeyHandlerOne();
            boolean isPassphraseValid = kh.isPassphraseValid(userIdPrivate, passphrase);
            if(!isPassphraseValid)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_BAD_PASSPHRASE, null);
                return;
            }
            
            KeySignatureHandler ksh = new KeySignatureHandlerOne();
            
            boolean isSigned = ksh.signPgpPublicKey(userIdPrivate, passphrase, userId, null, null, true);
            if(!isSigned)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_ALREADY_SIGNED, null);
            }
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }
        
    }    
    
    //@Override
    public void removeKeySignature(String userId, String userIdPrivate,
            char[] passphrase) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            if(userId == null || userIdPrivate == null || passphrase == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            
            KeyHandler kh = new KeyHandlerOne();
            boolean isPassphraseValid = kh.isPassphraseValid(userIdPrivate, passphrase);
            if(!isPassphraseValid)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_BAD_PASSPHRASE, null);
                return;
            }
            
            KeySignatureHandler ksh = new KeySignatureHandlerOne();
            
            ksh.removeSignatureFromPgpPublicKey(userIdPrivate, passphrase, userId);
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }        
    }
    
    
    //@Override
    public void addPhoto(String userId, String photo, char[] passphrase) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            if(userId == null  || passphrase == null || photo == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            File photoFile = new File(photo);
            if(!photoFile.exists())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return;
            }
            if(!photoFile.canRead())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                return;
            }
            
            KeyImageHandler kih = new KeyImageHandlerOne();                                   
            byte[] image = PgpKeyUtil.getJpegImageAsBytesFromFile(photoFile);
                        
            kih.addImageToPgpKey(userId, passphrase, image);
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }
        
    }

    //@Override
    public void deletePhoto(String userId) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            if(userId == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }       
            
            KeyImageHandler kih = new KeyImageHandlerOne();
            kih.removeImageFromPgpKey(userId);
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }

    }    
    
    //@Override
    public void deleteKey(String userId) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
        	if(userId == null)
        	{
        		taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
        		return;
        	}
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            
            KeyHandler kh = new KeyHandlerOne();
            int result = kh.deleteKeyPair(userId);
            if(result == CmPgpCodes.KEY_NOT_FOUND)
            {
                result = kh.deletePubKeyFromKeyRing(userId);
                if(result == CmPgpCodes.KEY_NOT_FOUND)
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_NOT_FOUND, null);
                    return;
                }
                if(result == CmPgpCodes.KEY_NOT_REMOVED)
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_NOT_REMOVED, null);
                }
            }
            
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }

    }    
    

    //@Override
    public void revokeKey(String userId, char[] passphrase) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            //Check keyring existance/accessibility
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            
            //Check parameters
            if(userId == null || passphrase == null)
            {
                
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            
            KeyHandler kh = new KeyHandlerOne();
            boolean isPassphraseValid = kh.isPassphraseValid(userId, passphrase);
            if(isPassphraseValid)
            {
                KeySignatureHandler ksh = new KeySignatureHandlerOne();
                ksh.revokeKey(userId, passphrase);
                return;
            }
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_BAD_PASSPHRASE, null);
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }
    }    
    
    
    //@Override
    // TODO: Better version that handles www.cGeep.com:8080
    /*
    public String searchKeysOnHkpServer(String hkpServerUrl, String substring) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(hkpServerUrl == null || substring == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return null;
            }
            
            // Prepare the HTTP output
            HttpTransfer httpTransfer = new HttpTransferOne();
                            
            try
            {            
                //http://pgp.mit.edu:11371/pks/lookup?search=npomereu&op=index&options=mr
                    
                String urlSearch = hkpServerUrl + "/pks/lookup?";
                
                NameValuePair[] data = 
                    {
                        new NameValuePair("search", substring),
                        new NameValuePair("op",     "index"),
                        new NameValuePair("options", "mr"),
                    };
                                        
                for (int i = 0; i < data.length; i++)
                {
                    if (i != 0)
                    {
                        urlSearch += "&";
                    }
                    
                    urlSearch += data[i].getName() + "=" + data[i].getValue();
                }
                                
                // Send!
                taskContainer.setOperationOk();
                httpTransfer.send(urlSearch, "GET", data); 

            }
            catch (ConnectException e1)
            {
                taskContainer.setErrorCode(Parms.ERR_HTTP_CONNECT_EXCEPTION, e1);
                return null; 
            }
            catch (SocketException e1)
            {
                taskContainer.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
                return null;             
            }
            catch (UnknownServiceException e1)
            {
                // Can happen on some key servers
                //errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
                //return null;     
                return null;
            }
            catch (ProtocolException e1)
            {
                taskContainer.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
                return null;             
            }
            catch (IOException e1)
            {
                taskContainer.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
                return null;             
            }        
                    
            // Ok, get the keys list from the stream
            String keyList = httpTransfer.recv();
            
            
            return keyList;
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return null;
        }
       
    }    
    
    //@Override
    public String receiveKey(String hkpServerUrl, String userId) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            if(hkpServerUrl == null || userId == null)
            {
            	taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
            	return null;
            }
            String keyBloc;
            KeyTransferHkpServer ktHkpServer = new KeyTransferHkpServerOne(hkpServerUrl);
            
            boolean exists = ktHkpServer.existsRemotePubKey(userId);
            if(!ktHkpServer.isOperationOk())
            {
                taskContainer.setErrorCode(ktHkpServer.getErrorCode(), ktHkpServer.getException());
                return null;
            }
            if(!exists)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_NOT_FOUND, null);
                return null;
            }
            
            List<StringTriplet> keys = ktHkpServer.getRemoteSearchedKey(userId, true);
            if(!ktHkpServer.isOperationOk())
            {
                taskContainer.setErrorCode(ktHkpServer.getErrorCode(), ktHkpServer.getException());
                return null;
            }
            StringTriplet key = keys.get(0);
            keyBloc = key.getElement3();
            
            return keyBloc;
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return null;
        }
    }
    
    //@Override
    public void sendKey(String hkpServerUrl, String userId) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            //Get key bloc
            KeyHandler keyHandler = new KeyHandlerOne();
            String keyAsc;
            try
            {
                keyAsc = keyHandler.exportAscPgpPublicKey(userId);
            }
            catch(KeyException ke)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_NOT_FOUND, null);
                return;
            }
            
            //Send key bloc to server
            KeyTransferHkpServer keyTransferHkpServer = new KeyTransferHkpServerOne(hkpServerUrl);

            keyTransferHkpServer.putRemoteAscPgpPublicKeyAsAsc(keyAsc);

            if (! keyTransferHkpServer.isOperationOk())
            {
                taskContainer.setErrorCode(keyTransferHkpServer.getErrorCode(), keyTransferHkpServer.getException());
            }
        }
        catch(Exception e )
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return;
        }       
    }    
    
    
    //////////////////////////////////////////////////////////////////////////
    // Wipe functions                                                       //
    //////////////////////////////////////////////////////////////////////////

    //@Override
    public void wipe(final String file, final int rounds, final int taskNumber) {

        if (taskNumber == 0)
        {
            wipeWrap(file, rounds, taskNumber);
        }
        else
        {
            Thread thread = new Thread() 
            {
                public void run() 
                {
                    wipeWrap(file, rounds, taskNumber);          
                }
            };
            
            thread.start();        
        }       
    }


    /**
     * @param file
     * @param rounds
     */
    private void wipeWrap(String file, int rounds, int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        
        try
        {
            taskContainer.setOperationOk();
            if(file == null || rounds <= 0)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            File fileToWipe = new File(file);
            if(!fileToWipe.exists())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return;
            }
            if(!fileToWipe.canWrite())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_WRITTEN, null);
                return;
            }
            
            String parent = fileToWipe.getParent();
            String name = fileToWipe.getName();
            
                                    
            File newFile = CgeepApiTools.getNewName(parent, name);
                                           
            boolean isRenamed = fileToWipe.renameTo(newFile);
            
            //files.set(i, newFile);
            
            byte[] bPattern = new byte[8];
            byte[] bMsg = new byte[4096];
            
            RandomAccessFile rafToWipe = new RandomAccessFile(newFile, "rw");
            
            rafToWipe.read(bMsg);

            bPattern = CgeepApiTools.FillArray(bPattern.length, 0);
            CgeepApiTools.wipeRound(rafToWipe, bPattern, false);
            
            bPattern = CgeepApiTools.FillArray(bPattern.length, 1);
            CgeepApiTools.wipeRound(rafToWipe, bPattern, false);
            
            for (int nCnt = 0; nCnt < rounds; nCnt++)
            {
                CgeepApiTools.wipeRound(rafToWipe, bPattern, true);
            }
            
            rafToWipe.close();
            
            newFile.delete();
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e); 
        }
    }    
    
    //////////////////////////////////////////////////////////////////////////
    // Symmetric Encryption/Decryption management functions                 //
    //////////////////////////////////////////////////////////////////////////

    /**
     * Return the Charset in use
     * 
     * @return the charset in use
     */
    
    //@Override
    public String getCharset()
    {
        return charset;
    }
            
    //@Override
    public void setCharset(String charset) {

        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            SortedMap<String, Charset> charsetsMap = Charset.availableCharsets();
            Set<String> charsets = charsetsMap.keySet();
                        
            if(charset ==null)
            {
                return;
            }
            
            if (charsets.contains(charsets))
            {
                this.charset = charset;
            }
            else
            {
                throw new UnsupportedCharsetException(charset);
            }
            
        }
        catch(UnsupportedCharsetException e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNSUPPORTED_CHARSET_EXCEPTION, e);
        }         
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }    
    }
    
    
    /**
     * @return the symmetricAlgorithm
     */
    //@Override
    public String getSymmetricAlgorithm()
    {
        return symmetricAlgorithm;
    }

    
    //@Override
    public void setSymmetricAlgorithm(String algorithm) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            if(algorithm != null)
            {
                if(!Arrays.asList(SYMMETRIC_ALGO).contains(algorithm))
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNSUPPORTED_SYMMETRIC_ALGO, null);
                    return;
                }
                this.symmetricAlgorithm = algorithm;
            }
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }
    }    
    
    
    //@Override
    public String encryptSymmetricString(String string, char [] passphrase) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
                   
        try
        {
            taskContainer.setOperationOk();
            
            String encryptedString = null;
            
            if(string == null || passphrase == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return null;
            }

            PgpSymActions pgpSymActions = new PgpSymActionsOne();
            byte[] encrypted = pgpSymActions.encryptSymmetricPgp(string.getBytes(), 
                                                                 passphrase);
            
            //Convert convert = new Convert();
            //encryptedString = convert.bytesToHexString(encrypted);
            
            encryptedString = Hex.toString(encrypted);
            
            return encryptedString;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return null;
        }
    }    
    

    //@Override
    public String decryptSymmetricString(String string, char[] passphrase) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            String decryptedString = null;
            
            if(string == null || passphrase == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return null;
            }
            
            //Convert convert = new Convert();
            //byte [] data = convert.hexStringToBytes(string);
            
            byte [] data =  Hex.fromString(string);

            PgpSymActions pgpSymActions = new PgpSymActionsOne();
            byte [] clearBytes = pgpSymActions.decryptSymmetricPgp(data, passphrase);
            decryptedString = new String (clearBytes);
            
            return decryptedString;
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return null;
        }
    }    
    
    /**
     * @param armorMode if true, the encrypted file will be PGP armored
     */
    //public void setArmorModeForFiles(boolean armorMode)
    public void setArmorModeForFiles(boolean armorMode)
    {
        //JOptionPane.showMessageDialog(null, "armorMode: " + armorMode);
        this.armorModeForFiles = armorMode;
    }
    
    
    //////////////////////////////////////////////////////////////////////////
    // Symmetric Encryption/Decryption for file functions                   //
    //////////////////////////////////////////////////////////////////////////    
    
    //@Override
    public void encryptSymmetric(final String file, final String encryptedFile,
                                 final char[] passphrase, final int taskNumber) 
    {
        if (taskNumber == 0)
        {
            encryptSymmetricWrap(file, encryptedFile, passphrase, taskNumber);
        }
        else
        {
            Thread thread = new Thread() 
            {
                public void run() 
                {
                    encryptSymmetricWrap(file, encryptedFile, passphrase, taskNumber);                 
                }
            };
            
            thread.start();       
            
            try
            {
                Thread.sleep(THREAD_START_WAIT); // Let the time for the thread to start...
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param file
     * @param encryptedFile
     * @param passphrase
     * @param taskNumber
     */
    private void encryptSymmetricWrap(String file, String encryptedFile,
            char[] passphrase, int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        
        try
        {           
            //errorMan.setOperationOk();
            taskContainer.setOperationOk();
            
            if(file == null || encryptedFile ==null || passphrase == null)
            {
                //errorMan.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            File clearFile = new File(file);
            File fileEncrypted = new File(encryptedFile);
            
            if(!clearFile.exists())
            {
                //errorMan.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return;
            }
            
            if(!clearFile.canRead())
            {
                //errorMan.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                return;
            }
            
            // Not, not anymore ==> Percent Progress is managed in CgeepApiEngine
            //CgeepApi.tasks.put(taskNumber, 0); 
            
            List<File> fileIn = new Vector<File>();
            fileIn.add(clearFile);
            
            CryptoEngine engine = new CgeepApiEngine(taskNumber, null, fileIn);
            taskContainer.setEngine(engine);
            
            PgpSymActions pgpSymActions = new PgpSymActionsOne(engine);
            pgpSymActions.setArmorMode(armorModeForFiles);
            pgpSymActions.setFilesLength(clearFile.length());
            
            pgpSymActions.encryptFileSymmetricPgp(clearFile, fileEncrypted, passphrase);          
            pgpSymActions.setMaximumProgress();
        }
        catch(Exception e)
        {
            //errorMan.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);         
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);          
        }
    }


    //@Override
    public void decryptSymmetric(final String file, final String decryptedFile,
            final char[] passphrase, final int taskNumber) 
    {
                
        if (taskNumber == 0)
        {
            decryptSymmetricWrap(file, decryptedFile, passphrase, taskNumber);
        }
        else
        {
            Thread thread = new Thread() 
            {
                public void run() 
                {
                    decryptSymmetricWrap(file, decryptedFile, passphrase, taskNumber);             
                }
            };
            
            thread.start();    
            
            try
            {
                Thread.sleep(THREAD_START_WAIT); // Let the time for the thread to start...
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }            
        }        
    }

    /**
     * @param file
     * @param decryptedFile
     * @param passphrase
     * @param taskNumber
     */
    private void decryptSymmetricWrap(String file, String decryptedFile,
            char[] passphrase, int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        
        try
        {            
            taskContainer.setOperationOk();
                        
            if(file == null || decryptedFile ==null || passphrase == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            File fileEncrypted = new File(file);
            File  clearFile = new File(decryptedFile);
            
            if(!fileEncrypted.exists())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return;
            }
            
            if(!fileEncrypted.canRead())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                return;            }
            
            //CgeepApi.tasks.put(taskNumber, 0);

            List<File> fileIn = new Vector<File>();
            fileIn.add(fileEncrypted);
            
            CryptoEngine engine = new CgeepApiEngine(taskNumber, null, fileIn);
            taskContainer.setEngine(engine);
            
            try
            {
                PgpSymActions pgpSymActions = new PgpSymActionsOne(engine);
                pgpSymActions.setFilesLength(fileEncrypted.length());
                
                pgpSymActions.decryptFileSymmetricPgp(fileEncrypted, clearFile, passphrase);
                pgpSymActions.setMaximumProgress();
            }
            catch( PGPDataValidationException e)
            {
                if(e.getMessage().contains("Invalid passphrase"))
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_BAD_PASSPHRASE, null);   
                    return;
                }
                throw e;
            }
        }
        catch(Exception e)
        {            
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);            
        }
    }    
        
    //@Override
    public int getPercentProcessedForTask(int taskNumber) 
    {                    
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        
        // If operation has failed ==> Force end of it!
        if (!taskContainer.isOperationOk())
        {
            taskContainer.setPercentProgress(100);
        }
        
        int result= taskContainer.getPercentProgress();
        
        return result;
        
    }

    //@Override
    public boolean cancelTask(int taskNumber)
    {
        boolean isInterrupted = false;
        
        try
        {
            TaskContainer taskContainer = new TaskContainer(taskNumber);
            isInterrupted = taskContainer.cancelTask(taskNumber);        
            return isInterrupted;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return isInterrupted;
        }
    }       
    
    

    //////////////////////////////////////////////////////////////////////////
    // Asymmetric Encryption/Decryption string functions                    //
    //////////////////////////////////////////////////////////////////////////

    //@Override
    public void resetRecipientsKeys() {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            m_recipients = new Vector<String>();
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }

    }

    
	//@Override
	public void addRecipientKey(String userId) {
		
	    TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
	    
	    try{
			taskContainer.setOperationOk();
			
			if(userId == null)
			{
				taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
			}
			if(m_recipients == null)
			{
				m_recipients = new Vector<String>();
			}
			m_recipients.add(userId);
		}
		catch(Exception e)
		{
			taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
		}

	}
	
	
    //@Override
    public String encryptString(String string) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {           
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return null;
            }
            
            if(string == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return null;
            }
            
            if(m_recipients == null || m_recipients.isEmpty())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_NO_RECIPIENTS, null);
                return null;
            }
            PgpActions pgpActions = new PgpActionsOne();
            
            String encryptedString = pgpActions.encryptPgp(string, m_recipients);
            
            return encryptedString;
            
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return null;
        }
    }	

    //@Override
    public String getUserIdForStringDecryption(String encryptedString) {
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return null;
            }
            
            if(encryptedString == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return null;
            }
            
            List<Long> pgpIds = PgpKeyUtil.extractPgpPublicKeys(encryptedString);
            if(pgpIds.isEmpty())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNABLE_TO_FIND_IDS, null);
                return null;
            }

            String userId = getPrivateUserIdInKeyring(pgpIds);
            return userId;
            
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return null;
        }
        
    }
    
	//@Override
	public String decryptString(String string, String userId, char[] passphrase) 
	{
	    TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
	    
		try
		{
			taskContainer.setOperationOk();
			
			if(!keyringExist())
			{
				if(isOperationOk())
				{
					//Keyring not exists
					taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
				}
				//an error occured accessing keyring and error code is already in errorMan
				return null;
			}
			String decryptedString = null;
			if(string == null || userId == null || passphrase == null)
			{
				taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
				return null;
			}
			KeyHandler kh = new KeyHandlerOne();
			boolean isPassphraseValid = kh.isPassphraseValid(userId, passphrase);
			
			if(!isPassphraseValid)
			{
				taskContainer.setErrorCode(CgeepApiErrorCode.ERR_BAD_PASSPHRASE, null);
				return null;
			}
			PgpActions pgpAction = new PgpActionsOne();
			
			decryptedString = pgpAction.decryptPgp(string, userId, passphrase);
			
			return decryptedString;
		}
		catch(Exception e)
		{
			taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
			return null;
		}
	}

    //////////////////////////////////////////////////////////////////////////
    // Asymmetric Encryption/Decryption file functions                      //
    //////////////////////////////////////////////////////////////////////////   
    	
    //@Override
    public void encrypt(final String file, final String encryptedFile, final int taskNumber) 
    {                   
        if (taskNumber == 0)
        {
            encryptWrap(file, encryptedFile, taskNumber);
        }
        else
        {
            Thread thread = new Thread() 
            {
                public void run() 
                {
                    encryptWrap(file, encryptedFile, taskNumber);            
                }
            };
            
            thread.start();        
            
            try
            {
                Thread.sleep(THREAD_START_WAIT); // Let the time for the thread to start...
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }            
        }       
    }


    /**
     * @param file
     * @param encryptedFile
     * @param taskNumber
     */
    private void encryptWrap(String file, String encryptedFile, int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        
        try{
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            
            if(file == null || encryptedFile == null || taskNumber < 0)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            File fileToEncrypt = new File(file);
            File encryped = new File(encryptedFile);
            if(!fileToEncrypt.exists())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return;
            }
            if(!fileToEncrypt.canRead())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                return;
            }
            if(encryped.exists() && !encryped.canWrite())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_WRITTEN, null);
                return;
            }
            
            if(m_recipients == null || m_recipients.isEmpty())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_NO_RECIPIENTS, null);
                return;
            }
            
            //CgeepApi.tasks.put(taskNumber, 0);
            
            List<File> filesIn = new Vector<File>();
            CgeepApiEngine engine = new CgeepApiEngine(taskNumber, null, filesIn);
            taskContainer.setEngine(engine);
             
            PgpActions pgpActions = new PgpActionsOne(engine);
            pgpActions.setArmorMode(armorModeForFiles);
            
            try
            {
                pgpActions.setFilesLength(fileToEncrypt.length());
                pgpActions.encryptPgp(fileToEncrypt, encryped, m_recipients);
                pgpActions.setMaximumProgress();
            }
            catch(Exception e)
            {
                if(e.toString().contains("Can't find encryption key"))
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_NOT_FOUND, null);
                    return;
                }
                throw e;
            }            
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
        }
    }	
	
    //@Override
    public String getUserIdForDecryption(String encryptedFile) {
        
        TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return null;
            }
            
            if(encryptedFile == null)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return null;
            }
            File fileEncrypted = new File(encryptedFile);
            if(!fileEncrypted.exists())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return null;
            }
            if(!fileEncrypted.canRead())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                return null;
            }
            
            List<Long> pgpIds = PgpKeyUtil.extractPgpPublicKeys(fileEncrypted);
            
            if(pgpIds.isEmpty())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNABLE_TO_FIND_IDS, null);
                return null;
            }
                                
            String userId = getPrivateUserIdInKeyring(pgpIds);
            return userId;
            
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return null;
        }
    }	
	
    //@Override
    public void decrypt(final String file, final String decryptedFile, final String userId,
            final char[] passphrase, final int taskNumber) 
    {   
        if (taskNumber == 0)
        {
            decryptWrap(file, decryptedFile, userId, passphrase, taskNumber);
        }
        else
        {

            Thread thread = new Thread() 
            {
                public void run() 
                {
                    decryptWrap(file, decryptedFile, userId, passphrase, taskNumber);      
                }
            };
            
            thread.start();        
            
            try
            {
                Thread.sleep(THREAD_START_WAIT); // Let the time for the thread to start...
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }            
        }       
    }
    
    
    /**
     * 
     * @param file
     * @param decryptedFile
     * @param userId
     * @param passphrase
     * @param taskNumber
     */
    
    private void decryptWrap(String file, String decryptedFile,
            String userId, char[] passphrase, int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            
            if(file == null || decryptedFile == null || userId == null || passphrase == null || taskNumber < 0)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            
            File encryptedFile = new File(file);
            File clearFile = new File(decryptedFile);
            if(!encryptedFile.exists())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return;
            }
            if(!encryptedFile.canRead())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                return;
            }
            if(clearFile.exists())
            {
                if(!clearFile.canWrite())
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_WRITTEN, null);
                    return;
                }
            }

            KeyHandler kh = new KeyHandlerOne();
            try
            {
                boolean isPassphraseValid = kh.isPassphraseValid(userId, passphrase);
                if(!isPassphraseValid)
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_BAD_PASSPHRASE, null);
                    return;
                }
            }
            catch(Exception e)
            {
                if(e.toString().contains("Can't find signing key"))
                {
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_NOT_FOUND, null);
                    return;
                }
                throw e;
            }
            
            List<File> filesIn = new Vector<File>();
            filesIn.add(encryptedFile);
            
            CryptoEngine engine = new CgeepApiEngine(taskNumber, null, filesIn);
            taskContainer.setEngine(engine);
            
            PgpActions pgpActions = new PgpActionsOne(engine);
            pgpActions.setFilesLength(encryptedFile.length());
            
            int result = pgpActions.decryptPgp(encryptedFile, clearFile, userId, passphrase);
            
            if(result == CmPgpCodes.SIGN_OK)
            {
                taskContainer.setSignatureStatus(SIGN_OK);
            }
            if(result ==  CmPgpCodes.SIGN_BAD)
            {
                taskContainer.setSignatureStatus(SIGN_BAD);
            }
            if(result == CmPgpCodes.ERR_UNKNOWN)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, null);
                pgpActions.setMaximumProgress();
                return;
            }
            if(result == CmPgpCodes.KEY_NOT_FOUND)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_NOT_FOUND, null);
                pgpActions.setMaximumProgress();
                return;
            }
            
            taskContainer.setSignatureStatus(SIGN_NOT_SIGNED);
            pgpActions.setMaximumProgress();
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return;
        }
    }
	

	/**
	 * Get a seed from a file (if file does not exist creates it)
	 * @return  byte[] containing seed
	 */
	private byte[]getSeed()
	{
	    TaskContainer taskContainer = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
	    
		try
		{
		    taskContainer.setOperationOk();
		    
			File seedFile = new File(keyringDirectory + seedFileName);
			if(!seedFile.exists())
			{
				CgeepApiTools.generateSeed(seedFile);
			}
			InputStream in = new FileInputStream(seedFile);
			int seedLength = in.available();
			byte[] seed = new byte[seedLength];
			in.read(seed);
			in.close();
			return seed;
		}
		catch(Exception e)
		{
			taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNABLE_TO_GET_SEED, e);
			return null;
		}

	}

    
    //////////////////////////////////////////////////////////////////////////
    // File signature & verification functions                                             //
    //////////////////////////////////////////////////////////////////////////  

    //@Override
    public String getSignatureStatus()
    {
        TaskContainer taskContainer 
            = new TaskContainer(TaskContainer.DEFAULT_SYNC_TASK);
        return taskContainer.getSignatureStatus();
    }   
    
    //@Override
    public String getSignatureStatusForTask(int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        return taskContainer.getSignatureStatus();
    }

    //@Override
    public void signDetached(final String file, final String detachedFileSignature,
            final String userId, final char[] passphrase, final int taskNumber) 
    {
                
        if (taskNumber == 0)
        {
            signDetachedWrap(file, detachedFileSignature, userId, passphrase, taskNumber);
        }
        else
        {

            Thread thread = new Thread() 
            {
                public void run() 
                {
                    signDetachedWrap(file, detachedFileSignature, userId, passphrase, taskNumber); 
                }
            };
            
            thread.start();      
            
            try
            {
                Thread.sleep(THREAD_START_WAIT); // Let the time for the thread to start...
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }            
        }       
    }


    private void signDetachedWrap(String file, String detachedFileSignature,
            String userId, char[] passphrase, int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            
            if(file == null || detachedFileSignature == null || userId == null || passphrase == null || taskNumber<0)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            
            File clearFile = new File(file);
            File signature = new File(detachedFileSignature);
            
            if(!clearFile.exists() )
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return;
            }
            if(!clearFile.canRead())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                return;
            }
            if(signature.exists() && !signature.canWrite())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_WRITTEN, null);
                return;
            }
            KeyHandler kh = new KeyHandlerOne();
            boolean isPassphraseValid = kh.isPassphraseValid(userId, passphrase);
            if(!isPassphraseValid)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_BAD_PASSPHRASE, null);
                return;
            }
                        
            List<File> filesIn = new Vector<File>();
            filesIn.add(clearFile);
            
            //CgeepApi.tasks.put(taskNumber, 0);
            
            CryptoEngine engine = new CgeepApiEngine(taskNumber, null, filesIn);
            taskContainer.setEngine(engine);
            
            PgpActions pgpActions = new PgpActionsOne(engine);
            pgpActions.setArmorMode(armorModeForFiles);            
            pgpActions.setFilesLength(clearFile.length());
            
            pgpActions.signDetachedPgp(clearFile, signature, userId, passphrase);
            pgpActions.setMaximumProgress();            
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return;
        }
    }

    //@Override
    public void verifyDetached(final String file, 
                               final String detachedFileSignature, 
                               final int taskNumber) 
    {
        
        if (taskNumber == 0)
        {
            verifyDetachedWrap(file, detachedFileSignature, taskNumber);
        }
        else
        {

            Thread thread = new Thread() 
            {
                public void run() 
                {
                    verifyDetachedWrap(file, detachedFileSignature, taskNumber);    
                }
            };
            
            thread.start();        
            
            try
            {
                Thread.sleep(THREAD_START_WAIT); // Let the time for the thread to start...
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }            
        }       
    }


    /**
     * @param file
     * @param detachedFileSignature
     * @param taskNumber
     */
    private void verifyDetachedWrap(String file, String detachedFileSignature,
            int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        
        try
        {
            taskContainer.setOperationOk();
            
            if(!keyringExist())
            {
                if(isOperationOk())
                {
                    //Keyring not exists
                    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
                }
                //an error occured accessing keyring and error code is already in errorMan
                return;
            }
            
            if(file == null || detachedFileSignature == null || taskNumber<0)
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
                return;
            }
            
            File clearFile = new File(file);
            File signature = new File(detachedFileSignature);
            
            if(!clearFile.exists() || !signature.exists())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
                return;
            }
            
            if(!clearFile.canRead() || !signature.canRead())
            {
                taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
                return;
            }
            
            List<File> filesIn = new Vector<File>();
            filesIn.add(clearFile);
            
            CryptoEngine engine = new CgeepApiEngine(taskNumber, null, filesIn);
            taskContainer.setEngine(engine);
            
            PgpActions pgpActions = new PgpActionsOne(engine);
            pgpActions.setFilesLength(clearFile.length());
            int result = pgpActions.verifyDetachedPgp(clearFile, signature);
                        
            if(result == CmPgpCodes.KEY_NOT_FOUND)
            {
                //taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEY_NOT_FOUND, null);
                taskContainer.setSignatureStatus(SIGN_KEY_NOT_FOUND);
                pgpActions.setMaximumProgress();
                return;
            }
            if(result == CmPgpCodes.SIGN_OK)
            {
                taskContainer.setSignatureStatus(SIGN_OK);
                pgpActions.setMaximumProgress();
                return;
            }
            if(result == CmPgpCodes.SIGN_BAD)
            {
                taskContainer.setSignatureStatus(SIGN_BAD);
                pgpActions.setMaximumProgress();
                return;
            }
            
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, null);
            pgpActions.setMaximumProgress();
            return;
        }
        catch(Exception e)
        {
            taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
            return;
        }
    }
    
    //////////////////////////////////////////////////////////////////////////
    // File signature + encryption function                                 //
    //////////////////////////////////////////////////////////////////////////     
    
	//@Override
	public void signAndEncrypt(final String file, final String encryptedFile,
	        final String userId, final char[] passphrase, final int taskNumber) {
				
        if (taskNumber == 0)
        {
            signAndEncryptWrap(file, encryptedFile, userId, passphrase, taskNumber);
        }
        else
        {

            Thread thread = new Thread() 
            {
                public void run() 
                {
                    signAndEncryptWrap(file, encryptedFile, userId, passphrase, taskNumber);   
                }
            };
            
            thread.start();       
            
            try
            {
                Thread.sleep(THREAD_START_WAIT); // Let the time for the thread to start...
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }            
        } 		
	}


    private void signAndEncryptWrap(String file, String encryptedFile,
            String userId, char[] passphrase, int taskNumber)
    {
        TaskContainer taskContainer = new TaskContainer(taskNumber);
        
        try
		{
            taskContainer.setOperationOk();
			
			if(!keyringExist())
			{
				if(isOperationOk())
				{
					//Keyring not exists
				    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_KEYRING_NOT_EXIST, null);
				}
				//an error occured accessing keyring and error code is already in errorMan
				return;
			}
			
			if(file == null || encryptedFile == null || userId == null || passphrase == null || taskNumber<0)
			{
			    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_MISSING_PARAMETER, null);
				return;
			}
			File clearFile = new File(file);
			File fileEncrypted = new File(encryptedFile);
			if(!clearFile.exists() )
			{
			    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_NOT_FOUND, null);
				return;
			}
			if(!clearFile.canRead())
			{
			    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_READ, null);
				return;
			}
			if(fileEncrypted.exists() && !fileEncrypted.canWrite())
			{
			    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_FILE_CANT_BE_WRITTEN, null);
				return;
			}
			KeyHandler kh = new KeyHandlerOne();
			boolean isPassphraseValid = kh.isPassphraseValid(userId, passphrase);
			if(!isPassphraseValid)
			{
			    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_BAD_PASSPHRASE, null);
				return;
			}
			
			if(m_recipients == null || m_recipients.isEmpty())
			{
			    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_NO_RECIPIENTS, null);
				return;
			}
			
			List<File> filesIn = new Vector<File>();
			filesIn.add(clearFile);
			
			//CgeepApi.tasks.put(taskNumber, 0);
			CryptoEngine engine = new CgeepApiEngine(taskNumber, null, filesIn);
			taskContainer.setEngine(engine);
			
			PgpActions pgpActions = new PgpActionsOne(engine);
            pgpActions.setArmorMode(armorModeForFiles);            
			pgpActions.setFilesLength(clearFile.length());
			
			pgpActions.signAndEncryptPgp(clearFile, fileEncrypted, m_recipients, userId, passphrase);
            pgpActions.setMaximumProgress();
		}
		catch(Exception e)
		{
		    taskContainer.setErrorCode(CgeepApiErrorCode.ERR_UNKNOWN_SYSTEM_ERROR, e);
			return;
		}
    }


	/**
     * Extract Private user id that is present in keyring from a list of pgp id 
     * @param pgpIds                    The list of pgp ids
     * @return                          The first private user id found in both list & keyring
     * @throws IOException
     * @throws FileNotFoundException
     * @throws KeyException
     * @throws NoSuchAlgorithmException
     */
    private String getPrivateUserIdInKeyring(List<Long> pgpIds)
            throws IOException, FileNotFoundException, KeyException,
            NoSuchAlgorithmException {
        KeyHandler keyHandler = new KeyHandlerOne();
            
        List<String> userIds = new Vector<String>();
        for(long pgpId : pgpIds)
        {
            //Get user id associated with pgp id 
            String user_Id = keyHandler.getSubkeyUserId(pgpId);
            userIds.add(user_Id);
        }
                 
        //Get all private keys in keyring
        List<String> ownerKeys = keyHandler.listPrivKeyIds();
        
        //Keep only private key 
        ownerKeys.retainAll(userIds);
   
        return ownerKeys.get(0);
    }

    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
            //System.out.println(this.getClass().getName() + " " + new Date() + " " + s);
        }
    }

    @Override
    public String searchKeysOnHkpServer(String hkpServerUrl, String substring) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String receiveKey(String hkpServerUrl, String keyId) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendKey(String hkpServerUrl, String userId) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	
    }

    @Override
    public void wipe(String file, int rounds, int taskNumber) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	
    }     
        
}
