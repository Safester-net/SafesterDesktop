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



/**
 * @author Nicolas de Pomereu
 *
 */
public class CgeepApiXmlRpcServerWrapper
{
    /** Because we must always return a value to XML RPC Client for void methods */
    private static final String nullValue = "null";
        
    /** 
     * The ICgeepApi instance. 
     * MUST be static because each CgeepApiXmlRpcServerWrapper launch is context less.
     */
    public static ICgeepApi cgeepAPI = new CgeepApi();
          
    /**
     * Default contructor
     * Builds a CgeepApi concrete instance
     */
    public CgeepApiXmlRpcServerWrapper()
    {
        
    }

    /**
     * @param parameter
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#addCommandLineParameter(java.lang.String)
     */
    public String addCommandLineParameter(String parameter)
    {
        cgeepAPI.addCommandLineParameter(parameter);
        return nullValue;
    }

    /**
     * Wrap a string if it's null
     * @param s     the string
     * @return      the string or "null" string if it's null
     */
    private String wrapNull(String s)
    {
        if (s == null)
        {
            return "null";
        }
        else
        {
            return s;
        }
    }
    
    /**
     * @param userId
     * @param photo
     * @param passphrase
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#addPhoto(java.lang.String, java.lang.String, char[])
     */
    public String addPhoto(String userId, String photo, String passphrase)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }

        cgeepAPI.addPhoto(userId, photo, passphrase.toCharArray());                        
        return nullValue;        
    }

    /**
     * @param userId
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#addRecipientKey(java.lang.String)
     */
    public String addRecipientKey(String userId)
    {
        cgeepAPI.addRecipientKey(userId);
        return nullValue;          
    }

    /**
     * @param taskNumber
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#cancelTask(int)
     */
    public Integer cancelTask(int taskNumber)
    {
        boolean b = cgeepAPI.cancelTask(taskNumber);
        return  (b)?1:0;
    }

    /**
     * @param userId
     * @param passphraseOld
     * @param passphraseNew
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#changePassphrase(java.lang.String, char[], char[])
     */
    public String changePassphrase(String userId, String passphraseOld,
            String passphraseNew)
    {
        if (passphraseOld == null)         
        {
            passphraseOld = "";
        }
        if (passphraseNew == null)         
        {
            passphraseNew = "";
        }

        cgeepAPI.changePassphrase(userId, passphraseOld.toCharArray(), passphraseNew.toCharArray());
        return nullValue;
    }

    /**
     * @param file
     * @param decryptedFile
     * @param userId
     * @param passphrase
     * @param taskNumber
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#decrypt(java.lang.String, java.lang.String, java.lang.String, char[], int)
     */
    public String decrypt(String file, String decryptedFile, String userId,
            String passphrase, int taskNumber)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        cgeepAPI.decrypt(file, decryptedFile, userId, passphrase.toCharArray(), taskNumber);
        return nullValue;
    }

    /**
     * @param encryptedString
     * @param userId
     * @param passphrase
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#decryptString(java.lang.String, java.lang.String, char[])
     */
    public String decryptString(String encryptedString, String userId,
            String passphrase)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        return wrapNull(cgeepAPI.decryptString(encryptedString, userId, passphrase.toCharArray()));
    }

    /**
     * @param file
     * @param decryptedFile
     * @param passphrase
     * @param taskNumber
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#decryptSymmetric(java.lang.String, java.lang.String, char[], int)
     */
    public String decryptSymmetric(String file, String decryptedFile,
            String passphrase, int taskNumber)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        cgeepAPI.decryptSymmetric(file, decryptedFile, passphrase.toCharArray(), taskNumber);
        return nullValue;
    }

    /**
     * @param string
     * @param passphrase
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#decryptSymmetricString(java.lang.String, char[])
     */
    public String decryptSymmetricString(String string, String passphrase)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        return wrapNull(cgeepAPI.decryptSymmetricString(string, passphrase.toCharArray()));
    }

    /**
     * @param userId
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#deleteKey(java.lang.String)
     */
    public String deleteKey(String userId)
    {
        cgeepAPI.deleteKey(userId);
        return nullValue;        
    }

    /**
     * @param userId
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#deletePhoto(java.lang.String)
     */
    public String deletePhoto(String userId)
    {
        cgeepAPI.deletePhoto(userId);
        return nullValue;        
    }

    /**
     * @param file
     * @param encryptedFile
     * @param taskNumber
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#encrypt(java.lang.String, java.lang.String, int)
     */
    public String encrypt(String file, String encryptedFile, int taskNumber)
    {
        cgeepAPI.encrypt(file, encryptedFile, taskNumber);
        return nullValue;           
    }

    /**
     * @param string
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#encryptString(java.lang.String)
     */
    public String encryptString(String string)
    {
        return wrapNull(cgeepAPI.encryptString(string));
    }

    /**
     * @param file
     * @param encryptedFile
     * @param passphrase
     * @param taskNumber
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#encryptSymmetric(java.lang.String, java.lang.String, char[], int)
     */
    public String encryptSymmetric(String file, String encryptedFile,
            String passphrase, int taskNumber)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        cgeepAPI.encryptSymmetric(file, encryptedFile, passphrase.toCharArray(), taskNumber);
        return nullValue;         
    }

    /**
     * @param string
     * @param passphrase
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#encryptSymmetricString(java.lang.String, char[])
     */
    public String encryptSymmetricString(String string, String passphrase)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        return wrapNull(cgeepAPI.encryptSymmetricString(string, passphrase.toCharArray()));
    }

    /**
     * 
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#executeCommandLine()
     */
    public String executeCommandLine()
    {
        cgeepAPI.executeCommandLine();
        return nullValue; 
    }

    /**
     * @param userId
     * @param file
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#exportPublicAndPrivateKey(java.lang.String, java.lang.String)
     */
    public String exportPublicAndPrivateKey(String userId, String file)
    {
        cgeepAPI.exportPublicAndPrivateKey(userId, file);
        return nullValue; 
    }

    /**
     * @param userId
     * @param file
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#exportPublicKey(java.lang.String, java.lang.String)
     */
    public String exportPublicKey(String userId, String file)
    {
        cgeepAPI.exportPublicKey(userId, file);
        return nullValue; 
    }

    /**
     * @param userId
     * @param passphrase
     * @param algoAsym
     * @param keyLengthAsym
     * @param algoSym
     * @param expirationDate
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#generateKeyPair(java.lang.String, char[], java.lang.String, int, java.lang.String, java.lang.String)
     */
    public String generateKeyPair(String userId, String passphrase,
            String algoAsym, int keyLengthAsym, String algoSym,
            String expirationDate)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        cgeepAPI.generateKeyPair(userId, passphrase.toCharArray(), algoAsym, keyLengthAsym,
                algoSym, expirationDate);
        return nullValue; 
    }

    /**
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getCharset()
     */
    public String getCharset()
    {
        return wrapNull(cgeepAPI.getCharset());
    }

    /**
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getErrorCode()
     */
    public String getErrorCode()
    {
        return wrapNull(cgeepAPI.getErrorCode());
    }

    /**
     * @param taskNumber
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getErrorCodeForTask(int)
     */
    public String getErrorCodeForTask(int taskNumber)
    {
        return wrapNull(cgeepAPI.getErrorCodeForTask(taskNumber));
    }

    /**
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getException()
     */
    public String getException()
    {
        return wrapNull(cgeepAPI.getException());
    }

    /**
     * @param taskNumber
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getExceptionForTask(int)
     */
    public String getExceptionForTask(int taskNumber)
    {
        return wrapNull(cgeepAPI.getExceptionForTask(taskNumber));
    }

    /**
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getKeyRingDirectory()
     */
    public String getKeyRingDirectory()
    {
        return wrapNull(cgeepAPI.getKeyRingDirectory());
    }

    /**
     * @param taskNumber
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getPercentProcessedForTask(int)
     */
    public int getPercentProcessedForTask(int taskNumber)
    {
        return cgeepAPI.getPercentProcessedForTask(taskNumber);
    }

    /**
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getSignatureStatus()
     */
    public String getSignatureStatus()
    {
        return wrapNull(cgeepAPI.getSignatureStatus());
    }

    /**
     * @param taskNumber
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getSignatureStatusForTask(int)
     */
    public String getSignatureStatusForTask(int taskNumber)
    {
        return wrapNull(cgeepAPI.getSignatureStatusForTask(taskNumber));
    }

    /**
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getStackTrace()
     */
    public String getStackTrace()
    {
        return wrapNull(cgeepAPI.getStackTrace());
    }

    /**
     * @param taskNumber
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getStackTraceForTask(int)
     */
    public String getStackTraceForTask(int taskNumber)
    {
        return wrapNull(cgeepAPI.getStackTraceForTask(taskNumber));
    }

    /**
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getSymmetricAlgorithm()
     */
    public String getSymmetricAlgorithm()
    {
        return wrapNull(cgeepAPI.getSymmetricAlgorithm());
    }

    /**
     * @param encryptedFile
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getUserIdForDecryption(java.lang.String)
     */
    public String getUserIdForDecryption(String encryptedFile)
    {
        return wrapNull(cgeepAPI.getUserIdForDecryption(encryptedFile));
    }

    /**
     * @param encryptedString
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getUserIdForStringDecryption(java.lang.String)
     */
    public String getUserIdForStringDecryption(String encryptedString)
    {
        return wrapNull(cgeepAPI.getUserIdForStringDecryption(encryptedString));
    }

    /**
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#getVersion()
     */
    public String getVersion()
    {
        return wrapNull(cgeepAPI.getVersion());
    }

    /**
     * @param file
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#importKey(java.lang.String)
     */
    public String importKey(String file)
    {
        cgeepAPI.importKey(file);
        return nullValue;         
    }

    /**
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#isOperationOk()
     */
    public Integer isOperationOk()
    {
        int i = (cgeepAPI.isOperationOk())?1:0;
        return i;
    }

    /**
     * @param taskNumber
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#isOperationOkForTask(int)
     */
    public Integer isOperationOkForTask(int taskNumber)
    {
        int i = (cgeepAPI.isOperationOkForTask(taskNumber))?1:0;
        return i;        
    }

    /**
     * @param substring
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#listKeys(java.lang.String)
     */
    public String listKeys(String substring)
    {
        return wrapNull(cgeepAPI.listKeys(substring));
    }

    /**
     * @param hkpServerUrl
     * @param keyId
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#receiveKey(java.lang.String, java.lang.String)
     */
    public String receiveKey(String hkpServerUrl, String keyId)
    {
        return wrapNull(cgeepAPI.receiveKey(hkpServerUrl, keyId));
    }

    /**
     * @param userId
     * @param userIdPrivate
     * @param passphrase
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#removeKeySignature(java.lang.String, java.lang.String, char[])
     */
    public String removeKeySignature(String userId, String userIdPrivate,
            String passphrase)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        cgeepAPI.removeKeySignature(userId, userIdPrivate, passphrase.toCharArray());
        return nullValue; 
    }

    /**
     * 
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#resetRecipientsKeys()
     */
    public String resetRecipientsKeys()
    {
        cgeepAPI.resetRecipientsKeys();
        return nullValue; 
    }

    /**
     * @param userId
     * @param passphrase
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#revokeKey(java.lang.String, char[])
     */
    public String revokeKey(String userId, String passphrase)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        cgeepAPI.revokeKey(userId, passphrase.toCharArray());
        return nullValue; 
    }

    /**
     * @param hkpServerUrl
     * @param substring
     * @return
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#searchKeysOnHkpServer(java.lang.String, java.lang.String)
     */
    public String searchKeysOnHkpServer(String hkpServerUrl, String substring)
    {
        return wrapNull(cgeepAPI.searchKeysOnHkpServer(hkpServerUrl, substring));
    }

    /**
     * @param hkpServerUrl
     * @param userId
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#sendKey(java.lang.String, java.lang.String)
     */
    public String sendKey(String hkpServerUrl, String userId)
    {
        cgeepAPI.sendKey(hkpServerUrl, userId);
        return nullValue; 
    }

    /**
     * @param armorMode
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#setArmorModeForFiles(boolean)
     */
    public String setArmorModeForFiles(Integer armorMode)
    {
        boolean b = (armorMode != 0);
        cgeepAPI.setArmorModeForFiles(b);
        return nullValue;
    }

    /**
     * @param charset
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#setCharset(java.lang.String)
     */
    public String setCharset(String charset)
    {
        cgeepAPI.setCharset(charset);
        return nullValue; 
    }

    /**
     * @param directory
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#setKeyRingDirectory(java.lang.String)
     */
    public String setKeyRingDirectory(String directory)
    {
        //JOptionPane.showMessageDialog(null, "directory: " + directory);
        cgeepAPI.setKeyRingDirectory(directory);
        return nullValue; 
    }

    /**
     * @param algorithm
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#setSymmetricAlgorithm(java.lang.String)
     */
    public String setSymmetricAlgorithm(String algorithm)
    {
        cgeepAPI.setSymmetricAlgorithm(algorithm);
        return nullValue; 
    }

    /**
     * @param file
     * @param encryptedFile
     * @param userId
     * @param passphrase
     * @param taskNumber
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#signAndEncrypt(java.lang.String, java.lang.String, java.lang.String, char[], int)
     */
    public String signAndEncrypt(String file, String encryptedFile,
            String userId, String passphrase, int taskNumber)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        cgeepAPI.signAndEncrypt(file, encryptedFile, userId, passphrase.toCharArray(),
                taskNumber);
        return nullValue; 
    }

    /**
     * @param file
     * @param detachedFileSignature
     * @param userId
     * @param passphrase
     * @param taskNumber
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#signDetached(java.lang.String, java.lang.String, java.lang.String, char[], int)
     */
    public String signDetached(String file, String detachedFileSignature,
            String userId, String passphrase, int taskNumber)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
        
        cgeepAPI.signDetached(file, detachedFileSignature, userId, passphrase.toCharArray(),
                taskNumber);
        return nullValue; 
    }

    /**
     * @param userId
     * @param userIdPrivate
     * @param passphrase
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#signKey(java.lang.String, java.lang.String, char[])
     */
    public String signKey(String userId, String userIdPrivate, String passphrase)
    {
        if (passphrase == null)         
        {
            passphrase = "";
        }
                
        cgeepAPI.signKey(userId, userIdPrivate, passphrase.toCharArray());
        return nullValue;         
    }

    /**
     * @param file
     * @param detachedFileSignature
     * @param taskNumber
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#verifyDetached(java.lang.String, java.lang.String, int)
     */
    public String verifyDetached(String file, String detachedFileSignature,
            int taskNumber)
    {
        cgeepAPI.verifyDetached(file, detachedFileSignature, taskNumber);
        return nullValue; 
    }

    /**
     * @param file
     * @param rounds
     * @param taskNumber
     * @see com.safelogic.pgp.api.toolkit.ICgeepApi#wipe(java.lang.String, int, int)
     */
    public String wipe(String file, int rounds, int taskNumber)
    {
        cgeepAPI.wipe(file, rounds, taskNumber);
        return nullValue; 
    }
    
    
}
