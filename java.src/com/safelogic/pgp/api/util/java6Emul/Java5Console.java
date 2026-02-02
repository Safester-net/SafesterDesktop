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
package com.safelogic.pgp.api.util.java6Emul;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import java.util.Scanner;


/**
 * This class prompts the user for a password.
 * 
 * This is ans emulator for Java 1.6 java.io.Console
 */

public class Java5Console {

    /**
     *  Return a line from the console
     * @return
     */
    public String readLine()
    {
        Scanner scanner = new Scanner(java.lang.System.in);
        try
        {
            return scanner.nextLine();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            System.out.println();
            System.exit(-1);
        } 
        
        return null;
    }
    
    /**
     * 
     * @return  a password read on the console
     * 
     * @throws IOException
     */
    public char[] readPassword() 
        throws IOException
    {
        try
        {
            return readPassword(java.lang.System.in);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            System.out.println();            
            System.exit(-1);
        }
        
        return null;        
    }
    
  /**
   *@param input stream to be used (e.g. System.in)
   *@param prompt The prompt to display to the user.
   *@return The password as entered by the user.
   */

   public char[] readPassword(InputStream in) throws IOException {
      MaskingThread maskingthread = new MaskingThread("");
      Thread thread = new Thread(maskingthread);
      thread.start();
	
      char[] lineBuffer;
      char[] buf;
      int i;

      buf = lineBuffer = new char[128];

      int room = buf.length;
      int offset = 0;
      int c;

      loop:   while (true) {
         switch (c = in.read()) {
            case -1:
            case '\n':
               break loop;

            case '\r':
               int c2 = in.read();
               if ((c2 != '\n') && (c2 != -1)) {
                  if (!(in instanceof PushbackInputStream)) {
                     in = new PushbackInputStream(in);
                  }
                  ((PushbackInputStream)in).unread(c2);
                } else {
                  break loop;
                }

                default:
                   if (--room < 0) {
                      buf = new char[offset + 128];
                      room = buf.length - offset - 1;
                      java.lang.System.arraycopy(lineBuffer, 0, buf, 0, offset);
                      Arrays.fill(lineBuffer, ' ');
                      lineBuffer = buf;
                   }
                   buf[offset++] = (char) c;
                   break;
         }
      }
      maskingthread.stopMasking();
      if (offset == 0) {
         return null;
      }
      char[] ret = new char[offset];
      java.lang.System.arraycopy(buf, 0, ret, 0, offset);
      Arrays.fill(buf, ' ');
      return ret;
   }
}
 


