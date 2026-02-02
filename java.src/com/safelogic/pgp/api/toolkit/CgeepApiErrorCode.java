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

//CgeepApiErrorCode
//Copyright (c) SafeLogic, 2000 - 2009

//Last Updates:
//29/09/09 16:20 NDP : Add ERR_PARSE_EXCEPTION
//19/09/09 16:30 NDP : ERR_UNSUPPORTED_CHARSET_EXCEPTION
//20/10/09 11:40 NDP : After ABE Merge

public class CgeepApiErrorCode {
	
	//For unknown error
	public static final String ERR_UNKNOWN_SYSTEM_ERROR		= "err_unknown_system_error";
	
	//Missing (null) parameter
	public static final String ERR_MISSING_PARAMETER 		= "err_missing_parameter";
	
	//Keyring directory error
	public static final String ERR_DIRECTORY_NOT_FOUND		= "err_directory_not_found";
	public static final String ERR_DIRECTORY_CANT_READ		= "err_directory_cant_read";
	public static final String ERR_DIRECTORY_CANT_WRITE		= "err_directory_cant_write";
	public static final String ERR_NOT_A_DIRECTORY			= "err_not_a_directory";

	//Key creation errors
	public static final String ERR_INVALID_ASYMMETRIC_KEY_LENGTH = "err_invalid_asymmetric_key_length";
	
	public static final String ERR_EXPIRATION_DATE_INVALID_FORMAT  = "err_expiration_date_invalid_format";
	public static final String ERR_INVALID_ASYMMETRIC_ALGO		   = "err_invalid_asymmetric_algo";
	public static final String ERR_INVALID_SYMMETRIC_ALGO		   = "err_invalid_symmetric_algo";
	public static final String ERR_UNABLE_TO_GET_SEED              = "err_unable_to_get_seed";
	
	//Keyring access errors
	public static final String ERR_ACCESSING_KEYRING		   = "err_accessing_keyring";
	public static final String ERR_KEYRING_NOT_READ_WRITE_MODE = "err_keyring_not_read_write_mode";
	public static final String ERR_KEYRING_NOT_EXIST 		   = "err_keyring_not_exist";
		
	//Key search error
	public static final String ERR_PUB_KEY_NOT_FOUND		= "err_pub_key_not_found";
	public static final String ERR_PRIV_KEY_NOT_FOUND		= "err_priv_key_not_found";
	
	//File access error
	public static final String ERR_FILE_NOT_FOUND			= "err_file_not_found";
	public static final String ERR_FILE_CANT_BE_READ	    = "err_file_cant_be_read";
	public static final String ERR_KEY_FILE_NOT_WRITABLE    = "err_key_file_not_writable";
	
	
	//Key signature error
	public static final String ERR_KEY_ALREADY_SIGNED		= "err_key_already_signed";
	
	//Key remove error
	public static final String ERR_KEY_NOT_FOUND			= "err_key_not_found";
	public static final String ERR_KEY_NOT_REMOVED			= "err_key_not_removed";
	
	//File wipe error
	public static final String ERR_FILE_CANT_BE_WRITTEN		= "err_file_cant_be_written";
	
	//Sym algo error
	public static final String ERR_UNSUPPORTED_SYMMETRIC_ALGO = "err_unsupported_symmetric_algo";
		
	//Task access error
	public static final String ERR_UNKNOWN_TASK 			= "err_unknown_task";
	
	
	//Passpharse error
	public static final String ERR_BAD_PASSPHRASE			= "err_bad_passphrase";
	
	//Recipient error
	public static final String ERR_NO_RECIPIENTS			= "err_no_recipients";
	
	//User id extraction error
	public static final String ERR_UNABLE_TO_FIND_IDS		= "err_unable_to_find_ids";
	
	//Key import error
	public static final String ERR_KEY_ALREADY_IN_KEYRING	= "err_key_already_in_keyring";

	// Parsing error
    public static final String ERR_PARSE_EXCEPTION          = "err_parse_exception";

    // Unsupported Charset Exception
    public static final String ERR_UNSUPPORTED_CHARSET_EXCEPTION = "err_unsupported_charset_exception";
}


