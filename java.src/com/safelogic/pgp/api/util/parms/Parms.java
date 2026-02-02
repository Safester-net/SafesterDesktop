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

import java.util.Locale;


/**
 * This class contains SMTP & HTTP parms for all pgeep operations
 * 
 * @author Nicolas de Pomereu
 */

public class Parms 
{    
    /** Base product name */
    public static final String PRODUCT_NAME = "cGeep";
        
    // Product types 
    public static final String PRODUCT_NAME_FREE = PRODUCT_NAME + " Pro";
    public static final String PRODUCT_NAME_PRO  = PRODUCT_NAME + " Free";
    public static final String PRODUCT_NAME_WEB  = PRODUCT_NAME + "Web"; // No White space
    
    /** License file name */
    public static final String LICENSE_FILE = "cGeepPro_License.txt";
    
    //public static final String FREE_LICENSE_FILE = "cGeepFree_License.txt";
    
    public static final String NULL = "null";
    
    //
    // Environment settings
    //
        
    /** Contains all implemented languages */
    public static String [] AVAILABLE_LANGUAGES =
             {                
                Locale.ENGLISH.getLanguage(),
                Locale.FRENCH.getLanguage() 
             };
        
    /** The localhost pgeep URL */
    public static final String URL_LOCALHOST            = "http://localhost:8080/cgeep";
    
    /** The server cgeep URL with httpS */
    public static final String URL_CGEEP_COM            = "https://www.cgeep.com";
       
    /** The server pgeep URL */
    public static final String URL_CGEEP_COM_NOT_SECURE = "http://www.cgeep.com";
    
    /** The support email */
    public static final String SUPPORT_EMAIL            = "support@cgeep.com";
   
    /** The support Web site */
    public static final String SUPPORT_WEB              = "www.safelogic.com";
    
    /** The cGeep Tray  program */
    public static final String CGEEPTRAY                = "cgeeptray.exe";
    
    /** The tag thats says the call from Windows Explorer */
    public static final String EXPLORER_TAG             = "!explorer!";

    /** The tag thats says the call from Windows Explorer */
    public static final String WIPE_TAG                 = "!wipe!";
    
    /** Tag followinf cGeep Tray call for decryption */
    public static final String FILE_TO_DECRYPT          = " file_to_decrypt ";
    
    /** The cGeep Icon */
    public static final String CGEEP_ICON               = "cgeep.ico";
    
    //
    // SMTP parameters
    //
    //  public static String SENDER_NAME    = "cGeep Mailer";
    //  public static String SENDER_DOMAIN  = "safelogic.com";
    //  public static String SENDER_EMAIL   = "contact@safelogic.com";
    //  
    //  public static String SMTP_SERVER_TEST   = "mail.wanadoo.fr";
    //  public static String SMTP_SERVER_PROD   = "mail.confimail.com";
    
    // Servlet
    public static final String SERV_REGISTER_BEGIN      = "RegisterBegin";
    public static final String SERV_REGISTER_CONFIRM    = "RegisterConfirm";
    public static final String SERV_REGISTER_PASSPRHASE = "RegisterPassphrase";
    public static final String SERV_REGISTER_END        = "RegisterEnd";
    public static final String SERV_KEY_DOWNLOAD        = "KeyDownload";
    
    // Actions
    public static final String ACTION                       = "ACTION";
    public static final String ACTION_UPLOAD_PUBKEY         = "action_upload_pubkey";
    public static final String ACTION_UPLOAD_USERID         = "action_upload_userid";
    public static final String ACTION_UPLOAD_HASHID         = "action_upload_hashid";
    public static final String ACTION_DOWNLOAD_PUBKEY       = "action_download_pubkey";
    public static final String ACTION_DOWNLOAD_FINGERPRINT  = "action_download_fingerprint";
    public static final String ACTION_EXISTS_PUBKEY         = "action_exists_pubkey";
    public static final String ACTION_EXISTS_PRIVKEY        = "action_exists_privkey";
    public static final String ACTION_SEARCH                = "action_search";
    public static final String ACTION_SEARCH_DATE           = "action_search_date";
    
    public static final String ACTION_UPLOAD_SIGNED_PUBKEY      = "action_upload_signed_pubkey";
    public static final String ACTION_SEARCH_INCLUDE_KEY_BLOK   = "action_search_include_key_blok";
    public static final String ACTION_GET_LAST_VERSION          = "action_get_last_version";
    
    public static final String ACTION_UPLOAD_PUBKEY_NEW			= "action_upload_pubkey_new";
    public static final String ACTION_UPLOAD_SERVER_SETTINGS	= "action_upload_server_settings";
    public static final String ACTION_GET_SERVER_SETTINGS		= "action_get_server_settings";
    public static final String ACTION_DELETE_SIGNED_PUBKEY      = "action_delete_signed_pubkey";
    
    // For invitations
    public static final String ACTION_INVITE_EMAILS          = "action_invite_email";
    public static final String ACTION_STORE_INVITATION		 = "action_store_invitation";
        
    // To store that a use has delete a key
    public static final String ACTION_DELETE_PUBKEY         = "action_delete_pubkey";
    
    // Service Message Transfer Action & Objects
    public static final String ACTION_GET_SERVICE_MESSAGE   = "action_get_service_message";
    public static final String PGEEP_VERSION                = "pgeep_version";
    public static final String FORMATTED_SERVICE_MESSAGE    = "formatted_service_message";
    
    // SQL Objects
    public static final String USER_NAME            = "user_name";
    public static final String USER_EMAIL           = "user_email";
    public static final String KEY_ID               = "key_id";
    public static final String PGP_ID               = "pgp_id";
    public static final String FINGERPRINT          = "fingerprint";    
    public static final String PUBLIC_KEY_BLOCK     = "public_key_block";
    public static final String STEALTH_MODE         = "stealth_mode"; 
    public static final String RECEIVE_INFOS        = "receive_infos";
    public static final String USER_LANGUAGE        = "user_language";
    public static final String HASH_ID              = "hash_id";
    
    public static final String USER_PASS_HASH       = "user_pass_hash";
    public static final String KEY_TYPE             = "key_type";
    public static final String KEY_LENGTH           = "key_length";
    public static final String SYM_ALGORITHM        = "sym_algorithm";
    public static final String DT_CREATE            = "dt_create";
    public static final String DT_EXPIRE            = "dt_expire";
    public static final String PRIVATE_KEY_BLOCK    = "private_key_block";
    
    // parms
    public static final String KEY_EXISTS                   = "key_exists";
    public static final String REGISTER_STATUS              = "register_status";
    public static final String HASHID_TOKEN_VALIDITY        = "hashid_token_validity";    
    public static final String DO_INCLUDE_PUB_KEY_BLOC      = "do_include_pub_key_bloc";
    
    // Register Code
    public static final int REGISTER_IS_SUSPENDED           = -1;
    public static final int REGISTER_ERROR_EMAIL_NOT_SEND   =  0;
    public static final int REGISTER_OK_EMAIL_SEND          =  1;
    
    // Invitations email status
    public static final String  INVITED_EMAILS               = "invited_emails";
    public static final String  INVITE_STATUS                = "invite_status";
    public static final int     INVITE_ERROR_EMAIL_NOT_SEND  =  0;
    public static final int     INVITE_OK_EMAIL_SEND         =  1;    
    
    
    // Errors in Recv
    public static final String ERR_GET_METHOD_NOT_SUPPORTED = "ERR_GET_METHOD_NOT_SUPPORTED";
    public static final String ERR_MAP_NOT_CREATED          = "ERR_MAP_NOT_CREATED";
    public static final String ERR_ACTION_NOT_SET           = "ERR_ACTION_NOT_SET";
    public static final String ERR_ACTION_INVALID           = "ERR_ACTION_INVALID";
    public static final String ERR_JAVA_EXCEPTION           = "ERR_JAVA_EXCEPTION";
    public static final String ERR_INVALID_TOKEN            = "ERR_INVALID_TOKEN";
    
    // Errors in API
    public static final String ERR_PUBLIC_KEY_FILE_NOT_FOUND= "err_public_key_file_not_found";
    public static final String ERR_INVALID_USER_ID          = "err_invalid_user_id";
    public static final String ERR_INVALID_PARM             = "err_invalid_parm";
    public static final String ERR_IO_EXCEPTION             = "err_io_exception";
    public static final String ERR_SYSTEM_EXCEPTION         = "err_system_exception";
    public static final String ERR_FILE_NOT_FOUND           = "err_file_not_found";
    public static final String ERR_PRIVATE_KEY_NOT_FOUND	= "err_private_key_not_found";
    
    //
    // HTTP Errors in API
    //
    
    // Map HttpTransfer.send() Exception
    //@throws SocketException           if there is no HTTP access 
    //@throws ConnectException          if the www.cgeep.com is not reachable (Tomcat down, etc.)
    //@throws UnknownServiceException   if the wwwcgeep.com/do/Recv servlet is not found
    //@throws ProtocolException         if the Recv does not send a clear feedback (program error)
    //@throws IOException               For all other IO / Network / System Error
    
    public static final String ERR_HTTP_SOCKET_EXCEPTION            = "err_http_socket_exception";
    public static final String ERR_HTTP_CONNECT_EXCEPTION           = "err_http_connect_exception";
    public static final String ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION   = "err_http_unknown_service_exception";
    public static final String ERR_HTTP_PROTOCOL_EXCEPTION          = "err_http_protocol_exception";
    public static final String ERR_HTTP_IO_EXCEPTION                = "err_http_io_exception";   
    
    //Passphrase
    public static final String BAD_PASSPHRASE               = "err_bad_passphrase";
    public static final String PASSPHRASE_CANCELED          = "err_cancel_passphrase";
    
    //TYPE OF MAIL
    public static final int ENCRYPTED                       = 0;
    public static final int SIGNED                          = 1;
    
    public static final String TOKEN						= "token";

    // Buy Servlet parms
    public static final String ACTION_BUY_EUR= "action_buy_eur";
    public static final String ACTION_BUY_USD= "action_buy_usd";

    //EVALUATION LICENCE ACTIONS
    public static final String EVAL_STATUS = "eval_status";
    public static final String GET_EVAL_LICENCE = "get_eval_licence";
    
    //EVALUATION LICENCE RETURN CODES
    public static final String ASK_FOR_EVAL = "ask_for_eval";
    public static final String EVAL_IN_PROGRESS = "eval_in_progress";
    public static final String EVAL_COMPLETED = "eval_completed";
    //ACTION EVAL_STATUS PARAM
    public static final String DATE     = "date";
    
    public static final String LICENSE = "license";
    
    /** cGeep Pro Installer Download Directory */
    public static String DOWNLOAD_PRO_DIRECTORY =  "download_pro";
    
    /** The directory where resides the custm users xml look and feels */
    public static String LOOK_AND_FEELS_DIRECTORY  = "look_and_feels";
    
    public static String LANGUAGE				   = "language";
    
    //Upload server settings codes
    public static String RETURN_CODE			   = "return_code";
    public static int    USER_UNKNOWN			   = 0;
    public static int	 SIGNATURE_BAD			   = -1;
    public static int 	 SETTINGS_UPDATED		   = 1; 
    
    public static String KEEP_KEY_ON_CENTRAL_DIR   = "keep_key_on_central_dir";
    
    public static final String NO_ENCRYPTION_KEY_FOR_ID = "No encryption key for id";
    
    
    

}

// End
