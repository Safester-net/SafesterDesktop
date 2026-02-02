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

//CgeepCmdLine
//Copyright (c) SafeLogic, 2000 - 2009

//Last Updates:
//29/09/09 16:20 NDP : recvKey() maps now correctly on new receiveKey() from api specs 
//09/10/09 16:35 NDP : Refactor class for clean call by both dll and Java
//09/10/09 18:15 NDP : Class is no more callable from C++ (calls are made in CgeepApi)
//14/10/09 16:08 ABE : Add HELP & VERSION options
//16/10/09 14:13 ABE : Fix missing help for one option
//19/10/09 12:06 ABE : Add a '.' at the end of all printed messages
//21/10/09 12:06 ABE : Fix symmetric decryption
//26/10/09 12:06 ABE : Fix passphrase option
//27/10/09 12:10 NDP : CgeepCmdLine is now package protected


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.safelogic.pgp.api.PgpFileStatusOne;
import com.safelogic.pgp.apispecs.PgpFileStatus;
import com.safelogic.utilx.Debug;

// No more "public" keyword ==> package protected
class CgeepCmdLine {

    /** The debug flag */ 
    protected boolean DEBUG = Debug.isSet(this);
    
	//Available options if 2 Strings first is short 2 long
	//					if 1 String only long is available

	private static String[] DETACH_SIGN = {"b","detach-sign"};
	private static String[] ENCRYPT = {"e","encrypt"};
	private static String[] SYMMETRIC = {"c","symmetric"};
	private static String[] DECRYPT = {"d","decrypt"};
//	private static String[] LIST_SECRET_KEYS = {"K","list-secret-keys"};
	private static String[] RECIPIENTS = {"r","recipient"};
	private static String[] OUTPUT = {"o","output"};
	private static String[] VERIFY = {"verify"};
	private static String[] LIST_KEYS = {"list-keys"};
	private static String[] GEN_KEY = {"gen-key"};
	private static String[] DELETE_KEY = {"delete-keys"};             
//	private static String[] DELETE_SECRET_KEY = {"delete-secret-keys"};
	private static String[] SIGN_KEY = {"sign-key"};
	private static String[] GEN_REVOKE = {"gen-revoke"};
	private static String[] EXPORT = {"export"};
	private static String[] SEND_KEYS = {"send-keys"};
	private static String[] RECV_KEYS = {"recv-keys"};
	private static String[] SEARCH_KEYS = {"search-keys"};
	private static String[] DIRECTORY  = {"directory"};
	private static String[] IMPORT		= {"import"};
	private static String[] PASSPHRASE = {"p", "passphrase"};
	private static String[] VERSION		= {"v", "version"};
	private static String[] HELP		= {"h", "help"};
		
	private static String DEFAULT_EXTENTION = ".pgp";
	private static String DEFAULT_EXTENTION_FOR_KEY_FILE = ".asc";
	private static String DEFAULT_EXTENTION_FOR_SIGNATURE = ".sig";
	
	private static final String CR_LF = System.getProperty("line.separator");
	
	
	private static String USAGE = 
		"Usage: cgeep" + CR_LF +
		"-b,--detach-sign <file> <user_id> [<signature_file>]   Create a detached signature of file" + CR_LF +
		"-c,--symmetric                                         Use symmetric encryption (to be used with -e)" + CR_LF +
		"-d,--decrypt <file> [<user_id>]                        Decrypt file" + CR_LF +
		"--delete-keys <user_id>                                Delete key" + CR_LF +
		"--directory <directory>                                Directory of keyring" + CR_LF +
		"-e,--encrypt <file> [<destination_file>]               Encrypt file" + CR_LF +
		"--export <user_id> [<destination_file>]                Export a key in armored format" + CR_LF +
		"--gen-key                                              Generate a key pair" + CR_LF +
		"--gen-revoke <user_id> <user_id_private>               Revoke a key" + CR_LF +
		"--import <key_file>                                    import a key in keyring" + CR_LF +
		"--list-keys <substring>                                List keys in keyring" + CR_LF +
		"-h                                                     Print this message" + CR_LF +
		"-o,--output <file>                                     Output file" + CR_LF +
		"-p,--passphrase                                        Passphrase to use" + CR_LF +
		"-r,--recipient <user_id>                               User id of recipient" + CR_LF +
		"--recv-keys <server> <user_id>                         Receive a key from a remote HKP server" + CR_LF +
		"--search-keys <server> <substring>                     Search a key on a remote HKP server" + CR_LF +
		"--send-keys <server> <user_id>                         Send a key to a remote HKP server" + CR_LF +
		"--sign-key <user_id> <user_id_private>                 Sign a key" + CR_LF +
		"--verify <file> [<signature_file>]                     Verify detached signature" + CR_LF +
		"-v, --version                                          Display version number of cGeep" + CR_LF;



	private CgeepApi cgeepApi;
	private OutputStream output = System.out;
	
	private char[] passphrase = null; 
	        
	
    
	public CgeepCmdLine ()
	{
		//cgeepApi = new CgeepApi();
	}
	
	/**
	 * Give absolute filename of file 
	 * 
	 * @param file		 the file name (absolute or not)
	 * @param parent	 the parent directory of file (can be null) 
	 */
	private String resolveFilename(String file, String parent)
	{
		if(file.contains(System.getProperty("file.separator")))
		{
			return file;
		}
		else if(parent == null)
		{
			file = System.getProperty("user.dir") + System.getProperty("file.separator") + file;
		}
		else
		{
			file = parent + System.getProperty("file.separator") + file;
		}
		return file;
	}
    /**
     * Execute the command (for Java)
     * Mainly used with Unix/Linux
     * @param args      the Main args 
     * 
     * The exception will be thrown on the console, this is OK as we use this only from
     * Java with a Linux Prompt
     */
    public void executeCommandFromJava(CgeepApi cgeepApi, String[] args) 
    {           
        debug("public void executeCommandFromJava(String[] args) ");
        
        this.cgeepApi = cgeepApi;
        
        Options options = null;        
        
        try{
            options = buildOptions();
            executeCommand(args);
        }
        catch (org.apache.commons.cli.ParseException pe) {
            System.out.println("Invalid command line options: ");
//            HelpFormatter h = new HelpFormatter();
//            h.printHelp("cgeep", options);
            pe.printStackTrace();
            boolean argsIsNull = false;
            if(args == null)
            {
            	argsIsNull = true;
            }
            printUsage(argsIsNull);
            return;
        }      
           
    }
    
    /**
     * Execute the command (for Java AND c++ dll)
     * @param args      the  args 
     */
    private void executeCommand(String[] args) 
        throws org.apache.commons.cli.ParseException 
        // Full qualification because ParseException is a common name!!! So silly!!!
    {        

    	if(args == null)
    	{
    		printUsage(true);
    	}
        Options options = buildOptions();

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
                                
        cmd = parser.parse( options, args);
        
        if(cmd.hasOption(VERSION[0]))
        {
        	System.out.println("cGeep API " + ApiVersion.getVersionWithDate());
        	return;
        }
        if(cmd.hasOption(HELP[0]))
        {
        	printUsage(false);
        	return;
        }
        if(cmd.hasOption(DIRECTORY[0]))
        {
             if(!directoryFlag(cmd))
             {
                 System.out.println("Error initialising keyring directory : " + cgeepApi.getErrorCode());
                 return;
             }
        }
        if(cmd.hasOption(OUTPUT[0]))
        {
            if(!changeOutput(cmd))
            {
                    System.out.println("Unable to change output, using defaul.t".getBytes());
            }
        }
        if(cmd.hasOption(PASSPHRASE[0]))
        {
            this.passphrase = cmd.getOptionValue(PASSPHRASE[0]).toCharArray(); 
        }
        if(cmd.hasOption(DETACH_SIGN[0]))
        {
            detachSign(cmd);            
            return;
        }
        
        if(cmd.hasOption(ENCRYPT[0]))
        {
            if(cmd.hasOption(SYMMETRIC[0]))
            {
                encrytpSymmetric(cmd);
                return;
            }
            if(cmd.hasOption(RECIPIENTS[0]))
            {
                encryptAsymmetric(cmd);
                return;
            }
            print("No recipients specified");
            return;
        }
        
        if(cmd.hasOption(SYMMETRIC[0]))
        {
            if(!cmd.hasOption(ENCRYPT[0]))
            {
                print("Missing -e flag");
                return;
            }
        }
        
        if(cmd.hasOption(DECRYPT[0]))
        {
            decrypt(cmd);
            return;
        }
        
        if(cmd.hasOption(VERIFY[0]))
        {
            verifyDetachSign(cmd);
            return;
        }
        
        if(cmd.hasOption(LIST_KEYS[0]))
        {
            listKeys(cmd);
            return;
        }

//      if(cmd.hasOption(LIST_SECRET_KEYS[0]))
//      {
//          listSecretKeys(cmd);
//          return;
//      }
        if(cmd.hasOption(GEN_KEY[0]))
        {
            genKeyPair(cmd);
            return;
        }
        if(cmd.hasOption(DELETE_KEY[0]))
        {
            deleteKey(cmd);
            return;
        }
//      if(cmd.hasOption(DELETE_SECRET_KEY[0]))
//      {
//          deleteSecretKey(cmd);
//          return;
//      }
        if(cmd.hasOption(SIGN_KEY[0]))
        {
            signKey(cmd);
            return;
        }
        if(cmd.hasOption(GEN_REVOKE[0]))
        {
            revokeKey(cmd);
            return;
        }
        if(cmd.hasOption(EXPORT[0]))
        {
            exportKey(cmd);
            return;
        }
        if(cmd.hasOption(SEND_KEYS[0]))
        {
            sendKey(cmd);
            return;
        }
        if(cmd.hasOption(RECV_KEYS[0]))
        {
            recvKey(cmd);
            return;
        }
        if(cmd.hasOption(SEARCH_KEYS[0]))
        {
            searchKey(cmd);
            return;
        }
        
        if(cmd.hasOption(IMPORT[0]))
        {
            importKey(cmd);
            return;
        }

        printUsage(false);
    }
    
    
	/**
	 * Creates the list of all options available
	 * @return	A List of CgeepCmdLineOption
	 */

	private List<CgeepCmdLineOption> buildOptionsList()
	{
		List<CgeepCmdLineOption> optionsList = new Vector<CgeepCmdLineOption>();

		CgeepCmdLineOption optionDetachSign = new CgeepCmdLineOption(DETACH_SIGN[0], DETACH_SIGN[1], "Create a detach Signature of file");

		optionDetachSign.addArg("file", "file to sign");
		optionDetachSign.addArg("sig_file", "Signature file");
		optionDetachSign.addArg("user_id", "signing user id");

		optionsList.add(optionDetachSign);

		CgeepCmdLineOption optionEncrypt = new CgeepCmdLineOption(ENCRYPT[0], ENCRYPT[1], "Encrypt file");
		optionDetachSign.addArg("file", "file to encrypt");
		optionDetachSign.addArg("dest_file", "destination file");
		optionEncrypt.setOptional(true);
		
		optionsList.add(optionEncrypt);
			
		CgeepCmdLineOption optionSymmetric = new CgeepCmdLineOption(SYMMETRIC[0], SYMMETRIC[1], "Use symmetric encryption");

		optionsList.add(optionSymmetric);

		CgeepCmdLineOption optionDecrypt = new CgeepCmdLineOption(DECRYPT[0], DECRYPT[1], "Decrypt file");
		optionDecrypt.addArg("file", "file to decrypt");
		optionDecrypt.addArg("user_id", "user id used for decryption");
		optionDecrypt.addArg("dest_file", "destination file");
		

		optionsList.add(optionDecrypt);

//		CgeepCmdLineOption optionListSecretKeys = new CgeepCmdLineOption(LIST_SECRET_KEYS[0], LIST_SECRET_KEYS[0], "List secret keys in keyring");
//		optionListSecretKeys.addArg("substring", "searched substring of user id");

//		optionsList.add(optionListSecretKeys);

		CgeepCmdLineOption optionRecipient = new CgeepCmdLineOption(RECIPIENTS[0], RECIPIENTS[1], "user id of recipient");
		optionRecipient.addArg("user_id", "recipient's user id");

		optionsList.add(optionRecipient);

		CgeepCmdLineOption optionOutput = new CgeepCmdLineOption(OUTPUT[0], OUTPUT[1], "output file");
		optionOutput.addArg("file", "file name");

		optionsList.add(optionOutput);

		CgeepCmdLineOption optionVerify = new CgeepCmdLineOption(null, VERIFY[0], "Verify detached signature");
		optionVerify.addArg("file", "file to verify");
		optionVerify.addArg("sig_file", "file containing detached signature");

		optionsList.add(optionVerify);

		CgeepCmdLineOption optionListKeys = new CgeepCmdLineOption(null, LIST_KEYS[0], "list keys in keyring");
		optionListKeys.addArg("substring", "searched substring of user id");
		optionListKeys.setOptional(true);

		optionsList.add(optionListKeys);

		CgeepCmdLineOption optionGenKey = new CgeepCmdLineOption(null, GEN_KEY[0], "Generate a key pair");

		optionsList.add(optionGenKey);

		CgeepCmdLineOption optionDeleteKey = new CgeepCmdLineOption(null, DELETE_KEY[0], "delete key");
		optionDeleteKey.addArg("substring", "substring of user id to delete");

		optionsList.add(optionDeleteKey);

//		CgeepCmdLineOption optionDeleteSecretKey = new CgeepCmdLineOption(null, DELETE_SECRET_KEY[0], "delete secret key");
//		optionDeleteSecretKey.addArg("substring", "substring of user id to delete");
//
//		optionsList.add(optionDeleteSecretKey);

		CgeepCmdLineOption optionSignKey = new CgeepCmdLineOption(null, SIGN_KEY[0], "Sign a key");
		optionSignKey.addArg("user_id", "the user ID of the public key to sign");
		optionSignKey.addArg("user_id_private", "");

		optionsList.add(optionSignKey);

		CgeepCmdLineOption optionGenRevoke = new CgeepCmdLineOption(null, GEN_REVOKE[0], "Revoke a key");
		optionGenRevoke.addArg("user_id", "the PGP User ID of the key to revoke");

		optionsList.add(optionGenRevoke);

		CgeepCmdLineOption optionExport = new CgeepCmdLineOption(null, EXPORT[0], "export a key in armored format");
		optionExport.addArg("user_id", "the PGP User ID");
		optionExport.addArg("file", "destination file");

		optionsList.add(optionExport);

		CgeepCmdLineOption optionSendKey = new CgeepCmdLineOption(null, SEND_KEYS[0], "send a key to a remote HKP server");
		optionSendKey.addArg("user_id", "user id of key to send");
		optionSendKey.addArg("server", "HKP server url");

		optionsList.add(optionSendKey);

		CgeepCmdLineOption optionRecvKey = new CgeepCmdLineOption(null, RECV_KEYS[0], "receive a key from a remote HKP server");
		optionRecvKey.addArg("user_id", "user id of key to retreive");
		optionRecvKey.addArg("server", "HKP server url");

		optionsList.add(optionRecvKey);

		CgeepCmdLineOption optionSearchKey = new CgeepCmdLineOption(null, SEARCH_KEYS[0], "search a key on a remote HKP server");
		optionSearchKey.addArg("substring", "substring to search on server");
		optionSearchKey.addArg("server", "HKP server url");

		optionsList.add(optionSearchKey);
		
		CgeepCmdLineOption optionDirectory = new CgeepCmdLineOption(null, DIRECTORY[0], "directory of keyring");
		optionDirectory.addArg("directory", "full path to directory");

		optionsList.add(optionDirectory);
		
		CgeepCmdLineOption optionImport = new CgeepCmdLineOption(null, IMPORT[0], "import a key into keyring");
		optionImport.addArg("file", "file containing key");
		
		optionsList.add(optionImport);
		
		CgeepCmdLineOption optionPassphrase = new CgeepCmdLineOption(PASSPHRASE[0], PASSPHRASE[1], "passphrase to use");
		optionPassphrase.addArg("passphrase", "the passphrase");
		
		optionsList.add(optionPassphrase);
		
		CgeepCmdLineOption optionHelp = new CgeepCmdLineOption(HELP[0], HELP[1], "help");
		
		optionsList.add(optionHelp);
		
		CgeepCmdLineOption optionVersion = new CgeepCmdLineOption(VERSION[0], VERSION[1], "help");
		optionsList.add(optionVersion);
		
		return optionsList;
				
	}

	/**
	 * Create the Options object
	 * @return	Options with all options
	 */
	private Options buildOptions()
	{
		Options options = new Options();
		List<CgeepCmdLineOption> optionsList = buildOptionsList();

		for(CgeepCmdLineOption cmdLineOption : optionsList)
		{
			Option option = getOption(cmdLineOption);
			options.addOption(option);		
		}

		return options;
	}


	/**
	 * Creates Option object from CgeepCmdLineOption
	 * @param cmdLineOption  	the CgeepCmdLineOption
	 * @return					the Option object
	 */
	private Option getOption(CgeepCmdLineOption cmdLineOption) {

		Map<String, String> args = cmdLineOption.getArguments();

		OptionBuilder.withValueSeparator();
		OptionBuilder.withDescription(cmdLineOption.getDesc());
		OptionBuilder.withLongOpt(cmdLineOption.getLongName());

		if(args != null)
		{
			int nbArgs = cmdLineOption.getArguments().size();
			OptionBuilder.hasArgs(nbArgs);
			Set<String > argsName = args.keySet();
			int i = 0;
			for(String key : argsName)
			{
				i++;
				String argumentDef = key + "=" + args.get(key);
				OptionBuilder.withArgName(argumentDef);
			}	

			if(cmdLineOption.isOptional())
			{
				OptionBuilder.hasOptionalArgs();
			}
		}
		Option option;
		if(cmdLineOption.getShortName() != null)
		{
			return OptionBuilder.create(cmdLineOption.getShortName());
		}

		return OptionBuilder.create();
	}


	/**
	 * Import a key into keyring
	 * @param cmd	CommandLine object with IMPORT option (param : file)
	 */
	private void importKey(CommandLine cmd) {
		String file = cmd.getOptionValue(IMPORT[0]);
		
		if(file == null)
		{
			String [] values = cmd.getArgs();
		
			if(values == null)
			{
				print("Missing arguments.");
				return;
			}
			file = values[0];
		}
		file = resolveFilename(file, null);
		cgeepApi.importKey(file);
		
		if(!cgeepApi.isOperationOk())
		{
			print(cgeepApi.getErrorCode());
			if(cgeepApi.getException() != null)
			{
				print(cgeepApi.getException());
			}
			return;
		}
		print("Key successfully imported.");
	}

	/**
	 * Retreive a key from hkp server
	 * @param cmd	CommandLine object with RECV_KEYS option (param : hkpServerUrl userId)
	 */
	private void recvKey(CommandLine cmd) {
		String [] values = cmd.getOptionValues(RECV_KEYS[0]);
		if(values == null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		if(values.length<2)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		String hkpServerUrl = values[0];
		String keyId = values[1];
		
		String keyBloc = cgeepApi.receiveKey(hkpServerUrl, keyId);
		if(!cgeepApi.isOperationOk())
		{
			print(cgeepApi.getErrorCode());
			if(cgeepApi.getException() != null)
			{
				print(cgeepApi.getException());
			}
			return;
		}
		
		print(keyBloc);
		
	}

	/**
	 * Publish a key to a hkp server
	 * @param cmd CommandLine object with SEND_KEYS option (param : hkpServerUrl userId)
	 */
	private void sendKey(CommandLine cmd) {
		String [] values = cmd.getOptionValues(SEND_KEYS[0]);
		if(values == null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		if(values.length<2)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		String hkpServerUrl = values[0];
		String userId = values[1];
		
		cgeepApi.sendKey(hkpServerUrl, userId);
		if(!cgeepApi.isOperationOk())
		{
			print(cgeepApi.getErrorCode());
			if(cgeepApi.getException() != null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print("Key sent.");
		}
	}
	

	/**
	 * Search a key from a hkp server
	 * @param cmd 	CommandLine object with SEARCH_KEYS option (param : hkpServerUrl string_to_find)
	 */
	private void searchKey(CommandLine cmd) {
		String [] values  = cmd.getOptionValues(SEARCH_KEYS[0]);
		if(values == null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		if(values.length<2)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		String hkpServerUrl = values[0];
		String substring = values[1];
		
		String result = cgeepApi.searchKeysOnHkpServer(hkpServerUrl, substring);
		if(!cgeepApi.isOperationOk())
		{
			print("Error searching key: " + cgeepApi.getErrorCode());
			if(cgeepApi.getException() != null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print(result);
		}
	}

	/**
	 * Export a key to a file
	 * @param cmd	CommandLine object with EXPORT option (param :  userId filename)
	 */
	private void exportKey(CommandLine cmd) {
		String[] values = cmd.getOptionValues(EXPORT[0]);
		
		if(values == null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		
		String userId = values[0];
		String file;

		if(values.length<2)
		{
			
			file = userId + DEFAULT_EXTENTION_FOR_KEY_FILE;
		}
		else
		{
			
			file = values[1];
		}
		file =  resolveFilename(file, null);
		
		cgeepApi.exportPublicKey(userId, file);
		if(!cgeepApi.isOperationOk())
		{
			System.out.println("Error exporting key: " + cgeepApi.getErrorCode());
			if(cgeepApi.getException() != null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print("Key successfully exported to " + file + ".");
		}
	}

     
	/**
	 * Revoke a key 
	 * @param cmd CommandLine object with GEN_REVOKE option (param :  userIdToRevoke userIdPrivate passpharse)
	 */
	private void revokeKey(CommandLine cmd) 
	{
		String userId =  cmd.getOptionValue(GEN_REVOKE[0]);
		String userIdPrivate =  cmd.getArgs()[0];
		if(userId == null || userIdPrivate == null)
		{
			print("Missing arguments.");
			return;
		}
		
		if(this.passphrase == null)
		{
			ConsoleWrapper console = new ConsoleWrapper();
			System.out.println("Enter passphrase for user id " + userIdPrivate + ":");
			passphrase = console.readPassword();
		}
		
		cgeepApi.removeKeySignature(userId, userIdPrivate, passphrase);
		if(!cgeepApi.isOperationOk())
		{
			print("Error revoking key: " + cgeepApi.getErrorCode());
			if(cgeepApi.getException() != null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print("Key revoked.");
		}
	}

	/**
	 * Sign a key
	 * @param cmd CommandLine object with SIGN_KEY option (param :  userIdToSign userIdPrivate passpharse)
	 */
	private void signKey(CommandLine cmd) {
		String [] values = cmd.getOptionValues(SIGN_KEY[0]);
		if(values == null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		if(values.length<2)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		String userId = values[0];
		String userIdPrivate = values[1];
		
		if(this.passphrase == null)
		{
            ConsoleWrapper console = new ConsoleWrapper();
			System.out.println("Enter passphrase for user id " + userIdPrivate + ":");
			passphrase = console.readPassword();
		}
		
		cgeepApi.signKey(userId, userIdPrivate, passphrase);
		if(!cgeepApi.isOperationOk())
		{
			print("Error signing key: " + cgeepApi.getErrorCode());
			if(cgeepApi.getException() != null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print("Key signed.");
		}
	}

	/**
	 * Delete a key from keyring
	 * @param cmd CommandLine object with DELETE_KEY option (param :  userIdToDelete)
	 */
	private void deleteKey(CommandLine cmd) {

		String userId = cmd.getOptionValue(DELETE_KEY[0]);
		if(userId==null)
		{
			String [] values = cmd.getArgs();
		
			if(values == null)
			{
				print("Error reading parameters.");
				return;
			}
			userId = values[0];
		}
		
		cgeepApi.deleteKey(userId);
		if(!cgeepApi.isOperationOk())
		{
			print("Error deleting key: ");
			print(cgeepApi.getErrorCode());
			if(cgeepApi.getException() !=null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print("Key deleted.");
		}
	}

	/**
	 * Generate a key pair
	 * @param cmd CommandLine object with GEN_KEY option (param :  userId passphrase algoASym keyLengthAsym algoSym expirationDate )
	 */
	private void genKeyPair(CommandLine cmd) {

		boolean selectionOk = false;
		
		String algoAsym = "";
        ConsoleWrapper console = new ConsoleWrapper();
		System.out.println("Select what kind of key you want (default is 2):");
		System.out.println("1 DSA/Elgamal");
		System.out.println("2 RSA");
   
		int algoIndex = 0;
		
		while(!selectionOk)
		{
			
			if(algoIndex == 1 || algoIndex == 2)
			{
				selectionOk = true;
			}
			else
			{
				System.out.println("Please choose 1 or 2:");
				String algoAsymIndex = console.readLine();
				if(algoAsymIndex == "")
				{
					algoIndex = 2;
					break;
				}
				try
				{
					algoIndex = Integer.parseInt(algoAsymIndex);
				}
				catch(NumberFormatException e)
				{
					algoIndex = 0;
				}
			}
		}
		if(algoIndex == 1)
		{
			algoAsym = "DSA/Elgamal";
		}
		else
		{
			algoAsym = "RSA";
		}
		
		System.out.println("Please give the key size you want (default 2048):");
		
		int keyLengthAsym = -1;
		
		selectionOk = false;
		while(!selectionOk)
		{
			System.out.println("Valid sizes are multiples of 1024");
			String sKeyLenghtAsym = console.readLine();
			if(sKeyLenghtAsym == "")
			{
				keyLengthAsym = 2048;
				break;
			}
			try{
				keyLengthAsym = Integer.parseInt(sKeyLenghtAsym);
			}
			catch(NumberFormatException e)
			{ 
				keyLengthAsym = -1;
			}
			if(keyLengthAsym % 1024 == 0)
			{
				selectionOk = true;
			}
		}
		
		selectionOk = false;
		
		System.out.println("When do you want your key to expire?");
		String expirationDate = "";
		while(!selectionOk)
		{
			System.out.println("yy/mm/dd or NEVER (default is NEVER):");
			expirationDate = console.readLine();
			if(expirationDate == "")
			{
				expirationDate = "NEVER";
				break;
			}
			if(expirationDate.equalsIgnoreCase("never"))
			{
				expirationDate = "NEVER";
				break;
			}
			SimpleDateFormat df = new SimpleDateFormat("yy/mm/dd");    
			try
			{
				//Try to extract date from string
				Date expDate = df.parse(expirationDate);
				selectionOk = true;
			}
			catch(Exception e)
			{
				//loop
			}
		}
		
	//	System.out.println("Which user ID do you want to use?");
		
		
		String userId = "";
		while(userId == "")
		{
			System.out.println("Name: ");
			userId = console.readLine();
			System.out.println("Email: ");
			String email = console.readLine();
			if(email != "")
			{
				userId +="<" + email + ">";
			}
		}
		
		String passphrase = "foo";
		String passphraseConfirm = "doe";
		selectionOk = false;
		while(!selectionOk)
		{
			System.out.println("Enter passphrase:");
			passphrase = new String(console.readPassword());
			if(passphrase == "" || passphrase.length()<8)
			{
				System.out.println("Passphrase must be at least 8 characters length.");
			}
			else
			{
				System.out.println("Repeat passphrase:");
				passphraseConfirm =  new String(console.readPassword());
				if(passphraseConfirm.equals(passphrase))
				{
					selectionOk = true;
				}
				else
				{
					System.out.println("Passphrases do not match! ");
				}
				
			}
		}

		System.out.println("Please choose the Symmetric key type:");
		System.out.println("0 : CAST-128"); 
		System.out.println("1 : Blowfish-128"); 
		System.out.println("2 : AES-128"); 
		System.out.println("3 : AES-192"); 
		System.out.println("4 : AES-256"); 
		System.out.println("5 : 3DES-168");
		
		selectionOk = false;
		int algoSymIndex = -1;
		while(!selectionOk)
		{
			System.out.println("Your choice (default is 4):");
			String sAlgoSym = console.readLine();
			if(sAlgoSym == "")
			{
				sAlgoSym = "4";
			}
			try
			{
				algoSymIndex = Integer.parseInt(sAlgoSym);
				if(algoSymIndex>-1 && algoSymIndex<6)
				{
					selectionOk = true;
				}
			}
			catch(NumberFormatException e)
			{
				//loop;
			}
		}

		String algoSym ="";
		switch(algoSymIndex)
		{
			case 0: algoSym= "CAST-128";
				break;
			case 1: algoSym= "Blowfish-128";
				break;
			case 2: algoSym= "AES-128";
				break;
			case 3: algoSym= "AES-192";
				break;
			case 4: algoSym= "AES-256";
				break;
			case 5: algoSym= "3DES-168";
				break;
		}
			//int keyLengthAsym = Integer.parseInt(sKeyLengthAsym);
        print("Generating Key pair...");
        
		cgeepApi.generateKeyPair(userId, passphrase.toCharArray(), algoAsym, keyLengthAsym, algoSym, expirationDate);
		if(!cgeepApi.isOperationOk())
		{
			print("Error generating key pair: ");
			print(cgeepApi.getErrorCode());
			if(cgeepApi.getException() !=null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print("Key pair generated.");
		}
	
	}

	/**
	 * List key from keyring
	 * @param cmd CommandLine object with LIST_KEYS option (param : substring )
	 */
	private void listKeys(CommandLine cmd) {
		String substring = cmd.getOptionValue(LIST_KEYS[0]);
		
		if(substring == null)
		{
			String [] values = cmd.getArgs();
		
			if(values != null && values.length > 0 )
			{
				substring = values[0];
			}
		}
		
		String result = cgeepApi.listKeys(substring);
        if(!cgeepApi.isOperationOk())
		{
        	print("Error listing keys: ");
			print(cgeepApi.getErrorCode());
			if(cgeepApi.getException() !=null)
			{
				print(cgeepApi.getException());
			}
		}
        else
        {
        	print(result);
        }
        
		
	}

	/**
	 * Verify detached signature
	 * @param cmd CommandLine object with VERIFY option (param : file detach_sig )	
	 */
	private void verifyDetachSign(CommandLine cmd) {
		String [] values = cmd.getOptionValues(VERIFY[0]);
		if(values == null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
	//	String currentDir = System.getProperty("user.dir") + System.getProperty("file.separator");
		
		String file = values[0];
		
		file = resolveFilename(file, null);
		String detachedFileSignature;
		if(values.length<2)
		{
			detachedFileSignature = file + DEFAULT_EXTENTION_FOR_SIGNATURE;
		}
		else
		{
			File f = new File(file);
			
			detachedFileSignature = values[1];
			detachedFileSignature = resolveFilename(detachedFileSignature, f.getParent());
		}

		
		cgeepApi.verifyDetached(file, detachedFileSignature, 0);
		
        if(!cgeepApi.isOperationOk())
		{
        	print("Error verifying signature: ");
			print(cgeepApi.getErrorCode());
			if(cgeepApi.getException() !=null)
			{
				print(cgeepApi.getException());
			}
		}
        
        String result = cgeepApi.getSignatureStatus();
        
        if(result.equals(cgeepApi.SIGN_OK))
        {
        	print("Signature successfully verified.");
        }
        else
        {
        	print("Bad signature.");
        }
	}

	/**
	 * Decrypt a file 
	 * @param cmd CommandLine object with DECRYPT option (param : file decryptedFile [userId]  [passphrase]  )	
	 */
	private void decrypt(CommandLine cmd) {
		String [] values = cmd.getOptionValues(DECRYPT[0]);
		if(values==null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Error reading parameters.");
			return;
		}
		//String currentDir = System.getProperty("user.dir") + System.getProperty("file.separator");
		String file =values[0];
		String decryptedFile = null;
		
		file = resolveFilename(file, null);
		System.out.println(file);
		decryptedFile = file.substring(0, file.lastIndexOf('.'));

		File fileIn = new File(file);
		if(!fileIn.exists())
		{
			print("Input file not found: " + file);
			return;
		}

        PgpFileStatus pgpFileStatus= new PgpFileStatusOne();
        int pgpStatus;
		try {
			pgpStatus = pgpFileStatus.getPgpStatus(fileIn);
		
		
	        if(pgpStatus == PgpFileStatus.STATUS_CRYPTED_SYMM)
	        {
	        	//String passphrase = values[2];
	        	
	        	if(this.passphrase == null)
	        	{
	                ConsoleWrapper console = new ConsoleWrapper();
	    		
	        		while(passphrase == null)
	        		{
	        			System.out.println("Password:");
	        			passphrase = console.readPassword();	    			
	        		}
	        	}
	        	cgeepApi.decryptSymmetric(file, decryptedFile, passphrase, 0);
	        	
	        }
	        else if(pgpStatus != PgpFileStatus.STATUS_NOT_PGP)
	        {

	        	String userId = null;
	        	if(values.length >= 2 )
	        	{
	        		userId = values[1];
	        	}
	        	else
	        	{
	                ConsoleWrapper console = new ConsoleWrapper();
	        		while (userId == null)
	        		{
	        			System.out.println("User id to use for decryption: ");
	        			userId = console.readLine();
	        		}
	        	}
	        	if(passphrase == null)
	        	{
	                ConsoleWrapper console = new ConsoleWrapper();
	
		    		while(passphrase == null)
		    		{
		    			System.out.println("Passphrase:");
		    			passphrase = console.readPassword();	    			
		    		}
	        	}
        		cgeepApi.decrypt(file, decryptedFile, userId, passphrase, 0);
        	}
	        else
	        {
	        	print("File " + file + " is not encrypted");
	        	return;
	        }
	        if(!cgeepApi.isOperationOk())
			{
	        	print("Error decrypting file " + fileIn + ": ");
				print(cgeepApi.getErrorCode());
				if(cgeepApi.getException() !=null)
				{
					print(cgeepApi.getException());
				}
			}
	        else
	        {
	        	print("File decrypted to: "  + decryptedFile + ".");
	        }
		} catch (Exception e) {
			print("Unknown error:");
			e.printStackTrace();
		}
	}

	/**
	 * Encrypt a file using asymmetric encryption
	 * @param cmd  CommandLine object with ENCRYPT option (param : file encryptedFile  )
	 */
	private void encryptAsymmetric(CommandLine cmd) {
		String [] recipients = cmd.getOptionValues(RECIPIENTS[0]);
		
		for(String recipient : recipients )
		{
			cgeepApi.addRecipientKey(recipient);
		}
		
		String [] values = cmd.getOptionValues(ENCRYPT[0]);
		String value = "";
		if(values == null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}

		//String currentDir = System.getProperty("user.dir") + System.getProperty("file.separator");
		String file = values[0];
		file = resolveFilename(file, null);
		String encryptedFile;
		
		if(values.length<2)
		{
			encryptedFile = file + DEFAULT_EXTENTION;
		}
		else
		{
			File f = new File(file);
			
			encryptedFile =  values[1];
			encryptedFile = resolveFilename(encryptedFile, f.getParent());
		}
		cgeepApi.encrypt(file, encryptedFile, 0);
		
		if(!cgeepApi.isOperationOk())
		{
			print("Error encrypting file " + file + ": ");
			print(cgeepApi.getErrorCode());
			if(cgeepApi.getException() !=null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print("File encrypted to " + encryptedFile + ".");
		}
	}

	/**
	 * Encrypt a file using symmetric encryption
	 * @param cmd CommandLine object with SYMMETRIC option (param : file encryptedFile passphrase )
	 */
	private void encrytpSymmetric(CommandLine cmd) {
	
		String [] values = cmd.getOptionValues(SYMMETRIC[0]);
		
		if(values == null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
	//	String currentDir = System.getProperty("user.dir") + System.getProperty("file.separator");
		String fileIn = values[0];
		fileIn = resolveFilename(fileIn, null);
		String encryptedFile;
		if(values.length<2)
		{
			encryptedFile = fileIn + DEFAULT_EXTENTION;
		}
		else
		{
			File f = new File(fileIn);
			encryptedFile = values[1];
			encryptedFile = resolveFilename(encryptedFile, f.getParent());
		}

		
        ConsoleWrapper console = new ConsoleWrapper();

		if(this.passphrase == null)
		{
			String pass = "foo";
			String passphraseConfirm = "doe";
			boolean selectionOk = false;
			while(!selectionOk)
			{
				System.out.println("Enter password:");
				pass = new String(console.readPassword());
				if(pass == "")
				{
					System.out.println("Password can't be null.");
				}
				else
				{
					System.out.println("Repeat password:");
					passphraseConfirm =  new String(console.readPassword());
					if(passphraseConfirm.equals(pass))
					{
						selectionOk = true;
					}
					else
					{
						System.out.println("Passwords do not match! ");
					}		
				}
			}
			this.passphrase = pass.toCharArray();		
		}
		
		cgeepApi.encryptSymmetric(fileIn, encryptedFile, passphrase, 0);
		if(!cgeepApi.isOperationOk())
		{
			print("Error encrypting file " + fileIn + ": ");
			print(cgeepApi.getErrorCode());
			if(cgeepApi.getException() !=null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print("File encrypted to " + encryptedFile + ".");
		}
	}

	/**
	 * Generate a detached signature of a file
	 * @param cmd CommandLine object with DETACH_SIGN option (param : file detachedFileSignature userId passphrase )
	 */
	private void detachSign(CommandLine cmd) {
		String[] values = cmd.getOptionValues(DETACH_SIGN[0]);
		if(values == null)
		{
			values = cmd.getArgs();
		}
		if(values == null)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		
		if(values.length<2)
		{
			print("Missing arguments.");
			//printUsage();
			return;
		}
		//String currentDir = System.getProperty("user.dir") + System.getProperty("file.separator");
		String fileIn = values[0];
		String userId = values[1];
		
		fileIn = resolveFilename(fileIn, null);
    	String detachedFileSignature;
    	if(values.length<3)
    	{
    		detachedFileSignature = fileIn + DEFAULT_EXTENTION_FOR_SIGNATURE; 
    	}
    	else
    	{
    		File f = new File(fileIn);
    		detachedFileSignature = values[2];
    		detachedFileSignature =resolveFilename(detachedFileSignature, f.getParent());
    	}

        ConsoleWrapper console = new ConsoleWrapper();

    	
		while(passphrase == null)
		{
			System.out.println("Passphrase:");
			passphrase = console.readPassword();	    			
		}
		cgeepApi.signDetached(fileIn, detachedFileSignature, userId, passphrase, 0);
		if(!cgeepApi.isOperationOk())
		{
			print("Error signing file " + fileIn + ": ");
			System.out.println(cgeepApi.getErrorCode());
			if(cgeepApi.getException() !=null)
			{
				print(cgeepApi.getException());
			}
		}
		else
		{
			print("File signed in " + detachedFileSignature + ".");
		}
		
	}

	/**
	 * Set output to specified file
	 * @param cmd CommandLine object with OUTPUT option (param : file )
	 * @return
	 */
	private boolean changeOutput(CommandLine cmd) {
		
		String fileOut = cmd.getOptionValue(OUTPUT[0]);
		if(fileOut == null)
		{
			String []values = cmd.getArgs();
			if(values != null)
			{
				fileOut = values[0];
			}
			else
			{
				return false;
			}
		}
		fileOut = resolveFilename(fileOut, null);
		File file = new File(fileOut);
		
		try {
			this.output = new FileOutputStream(file, true);
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
			
	}

	/**
	 * Specify directory of keyring
	 * @param cmd CommandLine object with DIRECTORY option (param : directory )
	 * @return
	 */
	private boolean directoryFlag(CommandLine cmd)
	{
		String  dirValue = cmd.getOptionValue(DIRECTORY[0]);
		if(dirValue == null)
		{
			String []values = cmd.getArgs();
		
			if(values == null)
			{
				print("missing arguments");
			//printUsage();
				return false;
			}
			dirValue = values[0];
		}
		cgeepApi.setKeyRingDirectory(dirValue);
		return cgeepApi.isOperationOk();
	}


	/**
	 * Print a string to output
	 * @param str	The string to print
	 */
	private void print(String str)
	{
		
		try {
			str += CR_LF;
			this.output.write(str.getBytes());
			this.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printUsage(boolean argsIsNull)
	{
		if(argsIsNull)
		{
			System.out.println("No arguments!");
		}
		System.out.println(USAGE);
		
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


}
