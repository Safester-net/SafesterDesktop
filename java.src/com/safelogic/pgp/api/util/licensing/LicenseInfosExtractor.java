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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.safelogic.pgp.util.FileUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class LicenseInfosExtractor
{
    private static String CR_LF = System.getProperty("line.separator") ;
               
    /** The client full name */
    private String clientName= null;
    
    /** The client full e-mail */
    private String clientEmail= null;
    
    /** The client company */
    private String company= "";
    
    /** The country */
    private String country = "";
    
    /** The Product. Defaults to client cGeep Pro */
    private String product = "";
    
    /** The expiration date of the license as YYYYMMDD. 99999999 for eternity */
    private String expirationDate   = null;

    /**
     * Constructor
     * @param licenceFile           the licence file signed with a PGP key
     */
    public LicenseInfosExtractor(File licenseFile)
        throws IOException               
    {           

        if (licenseFile == null)
        {
            throw new IllegalArgumentException("File name can't be null!");
        }
    
        if (! licenseFile.exists())
        {
        	throw new FileNotFoundException("License File does not exists: " + licenseFile);
        }
        
        // Do the extraction
        extractLicenseInfos(licenseFile);
    }
    
    /**
     * Extract all license infos and put results in global variables
     *
     */
    private void extractLicenseInfos(File licenseFile)
        throws IOException
    {
        FileUtil fileUtil = new FileUtil();
        
        String licenceInfos = fileUtil.getTextContent(licenseFile);
        
        this.clientName    =  StringUtils.substringAfter(licenceInfos, "Name: ");
        this.clientName    =  StringUtils.substringBefore(clientName, CR_LF);
        
        this.clientEmail    =  StringUtils.substringAfter(licenceInfos, "Email: ");
        this.clientEmail    =  StringUtils.substringBefore(clientEmail, CR_LF);
        
        this.company        =  StringUtils.substringAfter(licenceInfos, "Company: ");
        this.company        =  StringUtils.substringBefore(company, CR_LF);
        
        this.country        =  StringUtils.substringAfter(licenceInfos, "Country: ");
        this.country        =  StringUtils.substringBefore(country, CR_LF);
        
        this.product        =  StringUtils.substringAfter(licenceInfos, "Product: ");
        this.product        =  StringUtils.substringBefore(product, CR_LF);
        
        this.expirationDate =  StringUtils.substringAfter(licenceInfos, "Expiration Date: ");
        this.expirationDate =  StringUtils.substringBefore(expirationDate, CR_LF);
               
    }
        
    /**
     * @return the product
     */
    public String getProduct()
    {
        return product;
    }

    public String getClientEmail()
    {
        return clientEmail;
    }

    public String getClientName()
    {
        return clientName;
    }

    public String getCompany()
    {
        return company;
    }

    public String getCountry()
    {
        return country;
    }

    public String getExpirationDate()
    {
        return expirationDate;
    }
    

}

/**
 * 
 */
