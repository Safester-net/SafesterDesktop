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
package net.safester.application.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class StringMgr
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String teamName = JOptionPane.showInputDialog(null, "Nom de l\'Equipe");
        teamName = StringMgr.RemoveAccent(teamName);
        System.out.println("teamName: " + teamName);
    }
    
    /**
     * Convert a byte array to a byte array in UTF format
     * <br>Uses an underlying byte array output stream
     * 
     * @param   s a String to convert in UTF formated bytes
     * @return  a byte array in UTF format
     */

    public static byte [] ToUTFBytes(String s)
    throws IOException
    {           
        if (s == null)
        {
            return null;
        }
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream    dos   = new DataOutputStream(bos);
        dos.writeUTF(s);
        dos.close();

        byte []bBos = bos.toByteArray();
        return bBos;
    }

    /**
     * Convert a byte array in UTF format to a String
     * <br>Uses an underlying byte array input stream
     * 
     * @param   bBos    a byte array in UTF format s 
     * @return          a readable String
     */

    public static String FromUTFBytes(byte [] bBos)
    throws IOException
    {
        String sUTF = new String();

        ByteArrayInputStream bis = new ByteArrayInputStream(bBos);
        DataInputStream dis = new DataInputStream(bis);
        sUTF = dis.readUTF();
        dis.close();

        return sUTF;
    }

    /**
     *
     * split a String using a fast algorithm
     * <p>
     * @param  sString        The String to split.
     * @param  sExpression    the expression on wich to split
     * <p>
     * @return A String with Substring replacement.
     *
     */

    public static String [] split (String sIn, String sExpression)
    {
        if (sIn == null)
        {
            return null;
        }
        
        //if (false) return sIn.split(sExpression);

        // Special case : sIn is ""
        if (sIn.equals(""))
        {
            String [] sArray = new String[1];
            sArray[0] = "";
            return sArray;
        }       

        // NDP - remove all regex special chars
        if (sExpression.indexOf("\\") != -1)
        {
            sExpression = ReplaceAll(sExpression, "\\", "");
        }

        if (sIn.equals(sExpression))
        {
            return new String[0];
        }

        ArrayList arrayList = new ArrayList();
        int nLen = sExpression.length();
        int nIndex = 0;

        String sBegin = null;

        while (true)
        {
            nIndex = sIn.indexOf(sExpression);

            if (nIndex == -1)
            {
                if (sIn.length() != 0)
                {
                    arrayList.add(sIn); 
                } 
                break;
            }

            if (sIn.length() == 0)
            {
                break;
            }

            if (nLen == 0)
            {
                sBegin = sIn.substring(0, nIndex + 1); 
                sIn = sIn.substring(nIndex + nLen + 1);                   
            }
            else
            {
                sBegin = sIn.substring(0, nIndex); 
                sIn = sIn.substring(nIndex + nLen);                  
            }

            arrayList.add(sBegin);

            //System.out.println("sBegin :" + sBegin + ":");
            //System.out.println("sIn    :" + sIn + ":");

        }

        String [] sArray = new String[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++)
        {
            sArray[i] = (String) arrayList.get(i);
        }

        return sArray;

    }    

    /**
     *
     * Replace first occurence of a Substring by a new Substring in
     * a String
     * <p>
     * @param  sString   The String to modify.
     * @param  sOldChars The Substring to replace.
     * @param  sNewChars The new Substring to replace with.
     * <p>
     * @return A String with Substring replacement.
     *
     */ 

    public static String ReplaceFirst
    (       String sString,                                   
            String sOldSub, 
            String sNewSub)
    {
       
        if (sString == null)
        {
            return null;
        }        

        StringBuffer sbNew = new StringBuffer();
        String sOld = sString;

        int nOldLength = sOldSub.length();
        int nBindex = 0;

        if (sOld.indexOf(sOldSub) != - 1)
        {
            nBindex = sOld.indexOf(sOldSub);

            sbNew = sbNew.append(sOld.substring(0, nBindex) );
            sbNew = sbNew.append(sNewSub);

            sOld = sOld.substring(nBindex + nOldLength);

            sbNew = sbNew.append(sOld);
            return sbNew.toString();      
        }
        else {
            return sOld;
        }

    }       


    /**
     *
     * Replace first occurence of a Substring by a new Substring in
     * a String ignoring case.
     * <p>
     * @param  sString   The String to modify.
     * @param  sOldChars The Substring to replace.
     * @param  sNewChars The new Substring to replace with.
     * <p>
     * @return A String with Substring replacement.
     *
     */ 

    public static String ReplaceFirstIgnoreCase
    (       String sString,                                   
            String sOldSub, 
            String sNewSub)
    {

        if (sString == null)
        {
            return null;
        }
        
        StringBuffer sbNew = new StringBuffer();
        String sOld = sString;

        int nOldLength = sOldSub.length();
        int nBindex = 0;

        //if (sOld.indexOf(sOldSub) != - 1)
        if (sOld.toUpperCase().indexOf(sOldSub.toUpperCase()) != - 1)
        {
            nBindex = sOld.toUpperCase().indexOf(sOldSub.toUpperCase());

            sbNew = sbNew.append(sOld.substring(0, nBindex) );
            sbNew = sbNew.append(sNewSub);

            sOld = sOld.substring(nBindex + nOldLength);

            sbNew = sbNew.append(sOld);
            return sbNew.toString();      
        }
        else {
            return sOld.toString();
        }

    }       


    /**
     *
     * Replace all occurences of a Substring by a new Substring in
     * a String
     * <p>
     * @param  sString   The String to modify.
     * @param  sOldChars The Substring to replace.
     * @param  sNewChars The new Substring to replace with.
     * <p>
     * @return A String with Substring replacement.
     *
     */

    public static String ReplaceAll
    (       String sString,                                   
            String sOldSub, 
            String sNewSub)
    {

        if (sString == null)
        {
            return null;
        }

        StringBuffer sbNew = new StringBuffer();
        String sOld = sString;

        int nOldLength = sOldSub.length();
        int nBindex = 0;

        while (sOld.indexOf(sOldSub) != - 1)
        {
            nBindex = sOld.indexOf(sOldSub);

            sbNew = sbNew.append(sOld.substring(0, nBindex) );
            sbNew = sbNew.append(sNewSub);

            sOld = sOld.substring(nBindex + nOldLength);
        }

        sbNew = sbNew.append(sOld);
        return sbNew.toString();      
    }   


    /**
     * Replace all occurences of a Substring by a new Substring in
     * a String, ignoring case
     * <p>
     * @param  sString   The String to modify.
     * @param  sOldChars The Substring to replace.
     * @param  sNewChars The new Substring to replace with.
     * <p>
     * @return A String with Substring replacement.
     *
     */

    public static String ReplaceAllIgnoreCase(
            String sString,
            String sOldSub,
            String sNewSub)
    {
        
        if (sString == null)
        {
            return null;
        }        

        StringBuffer sbNew = new StringBuffer();
        String sOld = new String(sString);

        int nOldLength = sOldSub.length();
        int nBindex = 0;

        //while (sOld.indexOf(sOldSub) != - 1)
        while (sOld.toUpperCase().indexOf(sOldSub.toUpperCase()) != -1)
        {
            nBindex = sOld.toUpperCase().indexOf(sOldSub.toUpperCase());

            sbNew = sbNew.append(sOld.substring(0, nBindex));
            sbNew = sbNew.append(sNewSub);

            sOld = sOld.substring(nBindex + nOldLength);

        }

        sbNew = sbNew.append(sOld);

        return sbNew.toString();
    }


    /**
     * Count how many times a substring appears in a String
     * @param sIn       the String 
     * @param sSubIn    the substring to search in the String
     * 
     * @return how many times the substring appears in a String
     */

    public static int countSubstring(String sIn, String sSubIn)
    {
        int nOldLength = sSubIn.length();
        int nBindex = 0;

        int nCount = 0;

        while (sIn.indexOf(sSubIn) != -1)
        {
            nBindex = sIn.indexOf(sSubIn);
            sIn = sIn.substring(nBindex + nOldLength);
            nCount++;
        }

        return nCount;
    }   


    /**
     * 
     * Method to use to *not* display accuentation chars in a String
     * <br>
     * @param  sCleanMe - String to remove acccents from.
     * 
     * @return String without accentuation chars.
     * 
     */

    public static String RemoveAccent(String sCleanMe)
    {
        if (sCleanMe == null)
        {
            return null;
        }

        char cCurrent ;
        String sReturned = new String() ;
        for(int i=0 ; i < sCleanMe.length() ; i++)
        {
            cCurrent = sCleanMe.charAt(i) ;

            if( ! ( (cCurrent > 'a' && cCurrent < 'z') 
                    || (cCurrent > 'A' && cCurrent < 'Z')
                    || Character.isDigit(cCurrent) ) )
            {
                cCurrent = RemoveAccent(cCurrent) ;
            }

            sReturned += cCurrent ;
        }
        return sReturned ;
    }

    /**
     * 
     * Method to use to *not* display accuentation char
     * <br>
     * @param  cCleanMe - char to remove acccent from.
     * 
     * @return char without accentuation.
     * 
     */ 

    public static char RemoveAccent(char cCleanMe)
    {

        switch (cCleanMe)
        {
        case 'à' :

        case 'â' :

        case 'ä' :
            return 'a' ;

        case 'ç' :
            return 'c' ;

        case 'é' :

        case 'è' :

        case 'ê' :

        case 'ë' :
            return 'e' ;

        case 'î' :

        case 'ï' :
            return 'i' ;
                
        case 'ì' :
            return 'ì' ;            

        case 'ô' :

        case 'ö' :
            return 'o' ;

        case 'ù' :

        case 'û':

        case 'ü' :
            return 'u' ;
       
       //UPPERCASE     
            
        case 'À' :

        case 'Â' :

        case 'Ä' :
            return 'A' ;

        case 'Ç' :
            return 'C' ;

        case 'É' :

        case 'È' :

        case 'Ê' :

        case 'Ë' :
            return 'E' ;

        case 'Î' :

        case 'Ï' :
            return 'I' ;
                
        case 'Ì' :
            return 'I' ;            

        case 'Ô' :

        case 'Ö' :
            return 'O' ;

        case 'Ù' :

        case 'Û':

        case 'Ü' :
            return 'U' ;            
            
            // other modified chars
        case '’' :
            return '\'' ;

        case '–' :
            return '-' ;    

        default :   
            return cCleanMe ;
        }
    }   

} // End StringMgr
