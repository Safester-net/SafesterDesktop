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
package com.safelogic.pgp.api.util.parms;

/**
 * @author Alexandre Becquereau
 * OCX Return Codes
 */
public class CmPgpCodes
{
	
	// OK for all OK operation 
    public static int RC_OK                     =  0;
    public static int RC_ERROR                  = -1;
    
    //Sender status (Sending process)
	public static int SENDER_OK 				= 0;
	public static int PRIV_KEY_NOT_FOUND 		= 1;
	public static int BAD_PASSPHRASE 			= 2;
	public static int INVALID_SENDER_FORMAT 	= 3;
	public static int INVALID_RECIPIENT_FORMAT 	= 4;
    public static int CANCELED_PASSPHRASE       = 5;
    public static int PASSPHRASE_OK             = 6;
	
	//Recipient status
	public static int SERVER_KEY_OK = 10;
	public static int LOCAL_KEY_OK 	= 11;
	public static int KEY_NOT_FOUND = 12;
	
	//////////////////////////////////////////////
	// Encryption Actions to perform            // 
	//////////////////////////////////////////////
	
	// Symmetric encryption
	public static int  ENCRYPT_SYMMETRIC    = 20;
	
	// Asymmetric encryption
	public static int  ENCRYPT_ASYM 		= 21; // Never change the value. Outlook used in VBA!
	public static int  SIGN_ATTACHED		= 22;
	public static int  ENCRYPT_AND_SIGN     = 23;
    public static int  SIGN_DETACHED        = 24;
                
    // End Encryption Actions to perform     
    
	//Initialization return codes
	public static int INIT_OK 				= 30;
	public static int INI_FILE_ERROR 		= 31;
	public static int PRIV_KEY_CORRUPTED 	= 32;
	
	//Sender status (receiving process)
	public static int PUB_KEY_OK 			= 40;
	public static int PUB_KEY_NOT_FOUND 	= 41;
	
	// SIGNATURE VERIFICATION RETURN CODES
	public static int ERR_UNKNOWN			= -1;
    public static final int SIGN_OK         = 50;
    public static final int SIGN_BAD        = 51;
    
    // KEY REMOVAL RETURN CODES
    public static int KEY_REMOVED           = 61;
    public static int KEY_NOT_REMOVED       = 62;
    
    //OUTLOOK SEND POLICY
    
    //Not used any more ==> Must be forced to 3 on retrieval
    public static final int OL_ASK_ON_SEND_OBSOLETE     = 2;
   
    //Rule Let me Decide on each Send
    public static final int OL_DECIDE_EACH_SEND         = 0;
    
    //Rule Encrypt only for users with a cGeep Account or a known OpenPGP key
    //Confirmation is asked 
    public static final int OL_ENCRYPT_ON_SEND_WITH_CONFIRM          = 1;
    
    //Rule Encrypt only for users with a cGeep Account or a known OpenPGP key
    //No confirmation ask
    public static final int OL_ENCRYPT_ON_SEND_SILENT   = 3;
    
    //Force Encryption on each Send Depending on the Email Body or Title
    public static final int OL_ENCRYPT_DEPEND_DOMAINS       = 4;
    
    //Force Encryption on each Send Depending on the Domain Names (or Email Addresses)
    public static final int OL_ENCRYPT_DEPEND_KEYWORDS         = 5;
    
    //Force Encryption on each Send Depending on content or email
    public static final int OL_ENCRYPT_DEPEND_DOMAINS_AND_KEYWORDS          = 9;
    
    
    public static final int SAVE_DECRYPT        = 1;
    public static final int SAVE_ENCRYPT        = 0;
    
    public static final int AUTO_DECRYPT        = 0;
    public static final int NO_AUTO_DECRYPT     = 1;
    
    //ATTACHMENT RETURN CODES 
    public static final int ATTACH_OK           = 71;
    public static final int ATTACH_NOT_FOUND    = 72;
    public static final String NO_DIR           = "NO_DIR";
    
    public static final int KEEP_ATTACH         = 73;
    public static final int DETACH_ATTACHMENT   = 74;
    public static final int DECRYPT_ATTACHMENT  = 75;
    //public static final int KEEP_ATTACH_ENC     = 76;
     
    public static final String ATTACHMENT_HEADER = "**BEGIN ATTACH**";
    public static final String ATTACHMENT_FOOTER = "**END ATTACH**";
    
    public static final int ASK_IMPORT			= 80;
    public static final int DO_NOT_ASK_IMPORT	= 81;
   

}
