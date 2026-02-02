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

public class StringChecker   
{
    
    /**
     * class of allowed basic items
     */
    
    public static final class ITEM
    {        
      // Base chars
        public static final int NOT_NULL  = 0;
        public static final int NUMERIC   = 1;
        public static final int ALPHA     = 2;
		public static final int ACCENTS_1 = 3;
		public static final int ACCENTS_2 = 4;
        //
        public static final int SPACE      = 5;
        public static final int UNDERSCORE = 6;
        public static final int DASH       = 7; 
        public static final int SLASH      = 8;
        public static final int DOT        = 9;   
        public static final int AROBAS     = 10;
        public static final int BACKSLASH  = 11;
		
           
    }
    
    /**
     * class of allowed lists of patterns (or jobs oriented lists)
     */
    
    public static final class PATTERN
    { 
      public static int ALPHA_NUM[] = {ITEM.NUMERIC, ITEM.ALPHA};
        
      // npomereu is the login of npomereu@safelogic.com
      public static int LOGIN[]     = {ITEM.NOT_NULL, ITEM.NUMERIC, ITEM.ALPHA, 
                                       ITEM.DASH, ITEM.DOT, ITEM.UNDERSCORE};
      
      // npomereu is the login of npomereu@safelogic.com
      public static int EMAIL_FULL[] = {ITEM.NOT_NULL, ITEM.NUMERIC, ITEM.ALPHA, 
                                        ITEM.DASH, ITEM.DOT, ITEM.UNDERSCORE, ITEM.AROBAS};      
      
      // defines mailboxes authorized chars
      public static int FOLDER[]   = {ITEM.NOT_NULL, ITEM.ALPHA, ITEM.NUMERIC,    
									  ITEM.SPACE, ITEM.SLASH, ITEM.DASH, ITEM.DOT,
                                      ITEM.UNDERSCORE, ITEM.AROBAS, 
									  ITEM.ACCENTS_1,  ITEM.ACCENTS_2};
      
      // defines base DNS pattern
      public static int DNS[]       =  ALPHA_NUM;
            
    }    
       
	/**
	 * Checks the validity of the specified character.
	 * <br>
	 * The character is checked against an array of patterns.
	 * If the char satisfies *all* the patterns ==> return true 
	 * @param	c	        the character to verify
	 * @param   nPattern    the array of allowed items
	 * 
	 * @return	true if the character is valid, false otherwise
	 */
	
	private boolean isAllowed(char c, int[] nPattern)
	{
        boolean bReturn = false;
        
        for(int n=0 ; n < nPattern.length && !bReturn ; n++)
        {
            switch( nPattern[n])
            {
            case ITEM.ALPHA:
                bReturn = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ;
                break ;
            case ITEM.NUMERIC:
                bReturn = (c >= '0' && c <= '9') ;
                break ;
            case ITEM.SPACE:
                bReturn = (c == ' ') ;
                break ;     
            case ITEM.ACCENTS_1:
                bReturn = (c == 'à') || (c == 'â') || (c == 'ç') || (c == 'è') 
					   || (c == 'é') || (c == 'î') || (c == 'ô') || (c == 'ù') ;
                break ;   	
            case ITEM.ACCENTS_2:
                bReturn = (c == 'ì') || (c == 'ù') || (c == 'ï') 
						             || (c == 'ö') || (c == 'ü');
                break ;   					
            case ITEM.UNDERSCORE:
                bReturn = (c == '_') ;
                break ;                     
            case ITEM.DASH:
                bReturn = (c == '-') ;
                break ;
            case ITEM.SLASH:
                bReturn = (c == '/') ;
                break ;    
            case ITEM.DOT:
                bReturn = (c == '.') ;
                break ;                
            case ITEM.AROBAS:
                bReturn = (c == '@') ;
                break ;  
            case ITEM.BACKSLASH:
                bReturn = (c == '\\') ;
                break ;                  
            }
        }
        
        return bReturn ;
    }
      
    
	/**
	 * Checks the validity of the specified String.
	 * <br>
	 * The String is checked against an array of patterns.
	 * If *all* chars in the String satisfies *all* the patterns ==> return true 
	 * @param	sIn	        the String to verify
	 * @param   nPattern    the array of allowed items
	 * 
	 * @return	true if the character is valid, false otherwise
	 */
    
	public boolean isAllowed(String sIn, int[] nPattern)
	{
        if (sIn == null || sIn.trim().length() == 0)
        {
            for(int n=0 ; n < nPattern.length; n++)
            {
                if (nPattern[n] == ITEM.NOT_NULL)
                    return false;
            }
             
            return true;                    
        }
        
		char c;		
		for(int nCnt = 0 ; nCnt < sIn.length() ; nCnt++)
		{
			c = sIn.charAt(nCnt) ;
			
			if(isAllowed(c, nPattern))
			{
				// do nothing
			}
			else
			{
				return false ;
			}
		}
		
        return true;
	}    
        
    
    
    /**
     * isFirstAllowed()
     */
    public boolean isFirstAllowed(String sIn, int[] nPattern)
    {
        String sInFirst = (sIn==null )? null:sIn.substring(0,1);
        return isAllowed(sInFirst, nPattern);
    }
    
}

// END
