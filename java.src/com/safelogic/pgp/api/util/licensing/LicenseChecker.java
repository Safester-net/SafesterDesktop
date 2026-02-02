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
package com.safelogic.pgp.api.util.licensing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.openpgp.PGPPublicKey;

import com.safelogic.pgp.api.BooleanContainer;
import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.apispecs.PgpActions;
import com.safelogic.pgp.util.FileUtil;
import com.safelogic.pgp.util.JOptionPaneCustom;
import com.safelogic.pgp.util.UrlUtil;
import com.safelogic.pgp.util.UserPreferencesManager;
import com.safelogic.pgp.util.UtilDate;
import com.safelogic.utilx.Hex;

/**
 * Class to verify a cGeep Pro License file existence, signature and value.
 * @author Nicolas de Pomereu
 *
 */
public class LicenseChecker extends LicenseTags
{
    /** NEVER means the customer has bought cGeep */   
    public static final String NEVER = "NEVER";
    
    /** The PGP signer key identity */
    private static String SIGNER_KEY_ID = "0x75CF78EF"; // Todo: Obsfucate!
    
    private static final String CR_LF = System.getProperty("line.separator") ;
        
    private static final String SPECIAL_CR_LF = "\r\n";
    
    // End
    
    /** The licence file */
    private String licenseFile = null;

    /** The pubKeyring file to use **/
    private String pubKeyring;
    
    /** Stores the cause when a _License is invalid */
    private int invalidCause = 0;
    
    /** The license infos block */
    private String licenseInfos = null;

    /**  the number of days to expiration of the license */
    private int daysToExpiration = 14;
    
    /** The license file */
    public static final String CGEEPPRO_LICENSE_TXT       = "cGeepPro_License.txt";
    public static final String CGEEPAPI_LICENSE_TXT       = "cGeepAPI_License.txt";
    public static final String CORPPUBRING_PKR            = "CorpPubring.pkr";
    
    /**
     * To be called from the command line
     * @param args the command line arguments
     */
    public static void main(String [] args)
    {
        int value = getLicenseCheckerForToolkit();
        System.out.println(value);
    }
    
    /**
     * Built an instance for the toolit usage
     */
    public static int getLicenseCheckerForToolkit()
    {
        String licenseFile= null;
        String pubKeyring = null;
                        
        try
        {                    
                       
            // Extract the directory of cgeep_api.jar, which is in the classpath
            File cgeepApiJar = getFileFromClassPath("cgeep_api.jar");   
            
            if (cgeepApiJar == null || ! cgeepApiJar.exists())
            {
                return LicensesStatus.LICENSE_INVALID_FILE_NOT_FOUND;
            }
            
            File licenseDirectory = cgeepApiJar.getParentFile();
            
            //System.out.println("cgeepApiJar     : " + cgeepApiJar);
            //System.out.println("licenseDirectory: " + licenseDirectory);
            
            licenseFile = licenseDirectory + File.separator + CGEEPAPI_LICENSE_TXT;
            pubKeyring = licenseDirectory  + File.separator + CORPPUBRING_PKR;
                                   
            LicenseChecker licenseChecker = new LicenseChecker(licenseFile, pubKeyring);
            
            int licenseStatus = licenseChecker.verifyLicenseForToolkit();
                        
            return licenseStatus;
        }
        catch (Exception e)
        {
            // For security reason
            e.printStackTrace();
        }       
        
        return LicensesStatus.LICENSE_INVALID_SIGNATURE;

    }
    /**
     * Constructor
     * @param licenceFile     the licence file signed with a PGP key
     * @param pubKeyring      the public key ring file
     */
    public LicenseChecker(String licenseFile, String pubKeyring)
    {                      
        this.licenseFile= licenseFile;
        this.pubKeyring = pubKeyring;
    }
    
    
    /**
     * return the first full file name in the classpath
     * @param fileName      the rw file name to find in the classpath
     * 
     * @return  the first full file name in the classpath
     */
    private static File getFileFromClassPath(String fileName)
    {
        String pathSeparator   = new String(System.getProperty("path.separator"));
        String fileSeparator   = new String(System.getProperty("file.separator"));
        String classpath       = new String(System.getProperty("java.class.path"));
         
        StringTokenizer stringTokenizer = new StringTokenizer(classpath, pathSeparator);
                        
        while (stringTokenizer.hasMoreTokens())
        {        
            String token = stringTokenizer.nextToken();
                        
            if (token.endsWith(fileName))
            {
                return new File(token);
            }
        }
        
        return null;
    }


    /**
     * Method to verify the license for cGeep Console Product. If the license is not OK, an invalid cause is formated in numeric
     * for obsfucation reasons.
     *
     * @return the license status
     */
    public int verifyLicense()
    {  
        return verifyLicense(false);
    }
    
    /**
     * Method to verify the license for cGeep Console Product. If the license is not OK, an invalid cause is formated in numeric
     * for obsfucation reasons.
     *
     * @return the license status
     */
    public int verifyLicenseForToolkit()
    {  
        return verifyLicense(true);
    }    
    
    
    /**
     * Put the content of a file as text into a String
     * 
     * @param fileIn        The text file
     * @return              The content in text
     * @throws IOException
     */
    private String getTextContentWithSpecialCrLf(File fileIn)
        throws IOException
    {
        
        if (fileIn == null)
        {
            throw new IllegalArgumentException("File name can't be null!");
        }
                
        String text = "";
                
        BufferedReader br
        = new BufferedReader(new FileReader(fileIn));

        String line = null;
        
        while ((line = br.readLine()) != null) 
        {
            //System.out.println(line);
            text += line + SPECIAL_CR_LF;
        }
        
        return text;
    }
    
    /**
     * Class to verify the license. If the license is not OK, an invalid cause is formated in numeric
     * for obsfucation reasons.
     * @param isForTooklit          if true, the usage is for toolkit
     * 
     * @return the license status
     */
    private int verifyLicense(boolean isForTooklit)
    {          
        FileUtil fileUtil =new FileUtil();
        String toVerify = null;
        
        File fileLicenseFile = new File(licenseFile);
        
        // If the file does not exists: We are a Free User!
        if (! fileLicenseFile.exists() )
        {           
            // If there is no cGeep key files ==> allow to try cGeep Pro
            //             
            
            if (isForTooklit)
            {
                return LicensesStatus.LICENSE_INVALID_FILE_NOT_FOUND;
            }
            
            if (UrlUtil.keyringExist())
            {
                return LicensesStatus.LICENSE_INVALID_FILE_NOT_FOUND;
            }
            else
            {
                return LicensesStatus.LICENSE_IS_VALID_PRO_TRIAL;
            }            
        }
        
        //
        // Get licence file content
        //
        try
        {
            toVerify = getTextContentWithSpecialCrLf(fileLicenseFile);
        }
        catch (IOException e)
        {
            return LicensesStatus.LICENSE_INVALID_FILE_NOT_FOUND;
        }
        
        if (pubKeyring == null)
        {
            return LicensesStatus.LICENSE_KEY_FILE_NOT_FOUND;
        }
                
        File filePubKeyring  = new File(pubKeyring);
        
        if (! filePubKeyring.exists())
        {
            return LicensesStatus.LICENSE_KEY_FILE_NOT_FOUND;
        }
                
        PgpActions pgpActions = new PgpActionsOne();
        BooleanContainer bcontainer = new BooleanContainer();
                
        // Verify file signature

        try
        {
            licenseInfos = pgpActions.verifyPgp(toVerify, filePubKeyring, bcontainer);
        }
        catch (Exception e)
        {
            return LicensesStatus.LICENSE_SIGN_SYSTEM_ERROR;
        }

        // Verify file signature return value
        if (! bcontainer.getBooleanValue())
        {
            return LicensesStatus.LICENSE_INVALID_SIGNATURE;
        }                
        else  // verify the signer key identity
        {
            // Get The signing key infos
            PGPPublicKey publicKey = pgpActions.getLastSignPublicKey().getKey();
            
            if (! isSignerKeyAuthentic(publicKey))
            {
                return LicensesStatus.LICENSE_INVALID_SIGNATURE;
            }            
        }
                        
        // If we are checking the Tookit usage, the name must contain TOOLKIT
        if (isForTooklit)
        {
            try
            {
                LicenseInfosExtractor licenseInfosExtractor = new  LicenseInfosExtractor(new File(this.licenseFile));        
                String product = licenseInfosExtractor.getProduct();
                
                if (! product.toLowerCase().contains("toolkit"))
                {
                    return LicensesStatus.LICENSE_INVALID_SIGNATURE;
                }
            }
            catch (IOException e1)
            {
                //e.printStackTrace();
                JOptionPaneCustom.showException(null, e1);
            }
        }
        
        // is license perpetual
        if (isLicensePerpetual())
        {
            // Good for perpetual usage
            return LicensesStatus.LICENSE_IS_VALID_PRO_PERPETUAL;        
        }
        
        if ( isLicenseTrialPeriodStillValid())
        {   
            boolean acceptUser = false;
            
            try
            {
                acceptUser = isUserAcceptedForTrial();
            }
            catch (Exception e)
            {
                //e.printStackTrace();
                JOptionPaneCustom.showException(null, e);
            }
            
            // Test that the user has not registered
            if (acceptUser)
            {
            	daysToExpiration = daysOfTrialRemaining();
            	
                return LicensesStatus.LICENSE_IS_VALID_PRO_TRIAL;
            }
            else
            {
                return LicensesStatus.LICENSE_INVALID_NO_MORE_TRIAL;
            }
        }
        else
        {
            return LicensesStatus.LICENSE_INVALID_EXPIRED;
        }
    }

    
    
    
    /**
     * @return the daysToExpiration
     */
    public int getDaysToExpiration()
    {
        return daysToExpiration;
    }

    /**
     * Test if the user  is allowed for the trial.
     * <br>
     * A user is accepted for trial if:
     * <br> - 1) He has never done a trial.
     * <br> - 2) He always uses the same email in the license file for the trial.
     * <br>
     * <br>If the user has changed the email in license file ==> he has requested a second trial 
     * and this is refused.
     * <br>
     * <br> Implementation:
     * <br> - If the user has never been registered ==> We store his email address and accept him.
     * <br> - If the use has already been registered, we accpet him if he sues the same address.
     * 
     * @return true if the user is accepted for trial
     */
    private boolean isUserAcceptedForTrial()
        throws Exception
    {
        String licenseEmail = getLicenseEmail();        
        licenseEmail = licenseEmail.toLowerCase();
        
        String storedEmail= this.getEmailInRegistryPref();
        // If the license email has never been registered ==> Store it in prefs and accept the trial
        if (storedEmail == null || storedEmail.equals(""))
        {
            setEmaiInRegistryPref(licenseEmail.toLowerCase());
            return true;
        }
                
        // An email has been stored previously. It must be the same as the license file email, otw 
        // trial is refused

        if (storedEmail.equals(licenseEmail))
        {
            return true;
        }
        else
        {
            return false;
        }
            
    }
    
    /**
     * @return the stored email hash value
     */
    private static String getEmailInRegistryPref()
    {        
       UserPreferencesManager userPreferencesManager = new UserPreferencesManager();       
       Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
       String email = prefs.get("email", "");
     
       return email;
   }

    /**
     * @param hash email to store in registry
     */
    private static void setEmaiInRegistryPref(String email)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("email", email);
    }  

    /**
     * @return the license email stored in license file
     */
    private String getLicenseEmail()
        throws IOException
    {
        LicenseInfosExtractor licenseInfosExtractor = new  LicenseInfosExtractor(new File(this.licenseFile));        
        return licenseInfosExtractor.getClientEmail();
    }
    
    /**
     * return true if the signer key is authentic
     * @param publicKey     the signer public key
     */
    private boolean isSignerKeyAuthentic(PGPPublicKey publicKey)
    {
        if (publicKey == null)
        {
            return false;
        }
        // User Id is always the first of all User Ids for a Public Key
        Iterator<String> it = publicKey.getUserIDs();
        String userId = it.next();
        
        String fingerprint = Hex.toString(publicKey.getFingerprint());
        String signerKeyId =  "0x" + fingerprint.substring(32);

        if (signerKeyId.equalsIgnoreCase(SIGNER_KEY_ID))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
    * Obsfucation method
    * @return the following message: "Expiration Date:"
    *
    */

    private String getExpirationDateMessage()
    {
         return E + x + p + i + r + a + t + i + o + n + space + D + a + t + e + column + space;
    }
    
    /**
     * @return true if the license is perpetual
     */
    private boolean isLicensePerpetual()
    {        
        // Obsfucated String 
        String tagExpirationDate = getExpirationDateMessage();    
        String expirationDate =  StringUtils.substringAfter(licenseInfos, tagExpirationDate);
        expirationDate =  StringUtils.substringBefore(expirationDate, CR_LF);
        
        //System.out.println("expirationDate :" + expirationDate + ":");
        
        if (expirationDate.startsWith(NEVER))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * @return true if the date is not expired
     */
    private boolean isLicenseTrialPeriodStillValid()
    {        
        // Obsfucated String 
        String tagExpirationDate = getExpirationDateMessage();       
        
        String expirationDate =  StringUtils.substringAfter(licenseInfos, tagExpirationDate);
        expirationDate = expirationDate.substring(0, 10);
        
        //expirationDate =  StringUtils.substringBefore(expirationDate, CR_LF);
                        
        String rawExpirationDate = StringUtils.remove(expirationDate, "-");
                
        //System.out.println("expirationDate    :" + expirationDate + ":");
        //System.out.println("rawExpirationDate :" + rawExpirationDate + ":");        
        
        int dateLicense = new Integer(rawExpirationDate).intValue();
        
        String sDateCurrent = getDateReverse();
        
        int dateCurrent = new Integer(sDateCurrent).intValue();
                        
        if (dateLicense >= dateCurrent)
        {
            return true;
        }
        else {
            return false;
        }      
    }
    
    /**
     * Get the number of days remaining for trial
     * @return
     */
    public int daysOfTrialRemaining()
    {
    	int days = 0;
    	//Get expriration date
    	String tagExpirationDate = getExpirationDateMessage();       
        
        String expirationDate =  StringUtils.substringAfter(licenseInfos, tagExpirationDate);

        //expirationDate =  StringUtils.substringBefore(expirationDate, CR_LF);
        expirationDate = expirationDate.substring(0, 10);
        
        //Format date in DD/MM/YYYY
    	String year = expirationDate.substring(0, 4);
    	String month = expirationDate.substring(5, 7);
    	String day = expirationDate.substring(8);
    	
    	expirationDate = day + "/" + month + "/" + year;
    	
    	Date date = UtilDate.getDate(expirationDate);
    	
    	//Get current date
        Date today = new Date(System.currentTimeMillis());
        
        //Get the nb of day between the 2 dates
        long nb_days = (date.getTime() - today.getTime())/(24 * 60 * 60 * 1000 );
        
        days = (int)nb_days;
    	return days;
    }
    
    /**
     * Get the formated date of the trace FileDownloadTest.
     * @return  the date as "yyyy-mm-dd"
     */
    private String getDateReverse()
    {
        String sDate = new String();

        int nDay    = new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
        int nMonth  = new GregorianCalendar().get(Calendar.MONTH) + 1;

        sDate =  "" + new GregorianCalendar().get(Calendar.YEAR);
        
        if (nMonth < 10)
        {
            sDate += "0" + nMonth;
        }
        else {
            sDate += ""  + nMonth;
        }
        
        if (nDay < 10)
        {
            sDate += "0" + nDay;
        }
        else {
            sDate += "" + nDay;
        }
        
        return sDate;
    }
       
    

}

