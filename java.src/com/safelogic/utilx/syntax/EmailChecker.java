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
package com.safelogic.utilx.syntax;


public class EmailChecker 
	extends EmailCheckerTools
{
    // full e-mail 
    private String m_sEmailFull;
    
    /**
     * Constructor()
     * @param   full-email address as user@confimail.com
     */
    
    public EmailChecker(String sEmailFull)
    {
        this.m_sEmailFull = sEmailFull;
    }
    
	/**
	*
	* Verifies that the E mail as a valid form.
	* Incluse both syntax & DNS extension types
	* <br>
	* @return	true if the E mail is valid
	*
	*/
                                   
	public boolean isValid()
	{
        boolean bVerify = false;
        
		bVerify = this.isSyntaxValid();
		
        if (! bVerify)
            return false;
       
        // Ckeck DNS after @
        HostNameChecker chncChecker = new HostNameChecker() ;
        //String sDNS = m_sEmailFull.substring(m_sEmailFull.indexOf("@") + 1);
		String sDNS = getDomain(m_sEmailFull) ;
        //System.out.println(sDNS);
        return chncChecker.isValid(sDNS);
        
    }    
    
    
    
	/**
	*
	* Verifies that the E mail as a valid form.
	* Includes only syntax. (No DNS extension checking).
	* <br>
	* @return	true if the E mail is valid
	*
	*/
                                   
    public boolean isSyntaxValid()
	{
		boolean bVerify = false;
        
		StringChecker cmSchecker = new StringChecker();
		bVerify = cmSchecker.isAllowed(this.m_sEmailFull, 
									   StringChecker.PATTERN.EMAIL_FULL);
 
		if (! bVerify)
			return false;
       
		// First char *must* be alphanumeric
		bVerify = cmSchecker.isFirstAllowed(this.m_sEmailFull, StringChecker.PATTERN.ALPHA_NUM);
        
		if (! bVerify)
			return false;
                
		bVerify = this.isBaseSyntaxValid();
		
		if (! bVerify)
			return false;
			
		return true;		
	}
   
	/**
	*
	* Verifies that the E mail base syntax is valid
	* <br>
	* @return	true if the E mail is valid
	*
	*/
                                   
	private boolean isBaseSyntaxValid()
	{
		boolean bVerify = false;

		int nAt     = this.m_sEmailFull.indexOf("@");
		int nDot	= this.m_sEmailFull.indexOf(".");

		int nAt2	= this.m_sEmailFull.lastIndexOf("@");
		int nDot2	= this.m_sEmailFull.lastIndexOf(".");
		
		int nFollowDot  = this.m_sEmailFull.indexOf("..");
		
		if (   (nAt == -1)							    // at least 1 @			
			|| (nAt2 == this.m_sEmailFull.length() - 1) // @ not at end
			|| (nAt != nAt2)							// only 1 @
			|| (nDot == -1)								// at least 1 .
			|| (nDot == 0)								// . is not first char
			|| (nDot2 == this.m_sEmailFull.length() - 1)// . is not last char
			|| (nDot2 < nAt)							// . is before @
			|| (nFollowDot != - 1))						// Not ".." (followind dots)
		  //|| (nBlank != -1))							// no blanks in an e mail !
		  //|| (nAt == 0)								// @ is not first char
		{
			bVerify = false;
		}
		else {
			bVerify = true;
		}
		
		return bVerify;		
	}	
	
	
	/*
	 * Gets the domain from an e-mail.
	 * @param	sEmail		the email
	 * @return	the domain for this email
	 */
	/*
	//public static String getDomain(String sEmail)
	//{
	//	String sDomain = sEmail.substring(sEmail.indexOf("@") + 1);
	//	return sDomain ;
	//}
	*/
	
    /*
    //public static void main (String args[])
    //    throws Exception
    //{
    //    EmailChecker cmEchecker = new EmailChecker(args[0]);
    //    System.out.println(cmEchecker.isValid());
    //}
    */
    
}

// END
