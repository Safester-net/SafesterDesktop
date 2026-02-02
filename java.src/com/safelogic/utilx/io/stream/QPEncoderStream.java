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
package com.safelogic.utilx.io.stream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
 
/** 
 * This class implements a Quoted Printable Encoder. It is implemented as 
 * a FilterOutputStream, so one can just wrap this class around 
 * any output stream and write bytes into this filter. The Encoding 
 * is done as the bytes are written out. 
 *  
  * @author John Mani 
 */ 
 public class QPEncoderStream extends FilterOutputStream { 
  



     private int count = 0; 
     private int bytesPerLine; 
     private boolean gotSpace = false; 
     private boolean gotCR = false; 
      
     /** 
      * Create a QP encoder that encodes the specified input stream 
      * @param out        the output stream 
      * @param bytesPerLine  the number of bytes per line. The encoder 
      *                   inserts a CRLF sequence after this many number 
      *                   of bytes. 
     */ 
     public QPEncoderStream(OutputStream out, int bytesPerLine) { 
         super(out); 
         this.bytesPerLine = bytesPerLine - 1; 
     } 
      
     /** 
      * Create a QP encoder that encodes the specified input stream. 
      * Inserts the CRLF sequence after outputting 76 bytes. 
      * @param out        the output stream 
     */ 
     public QPEncoderStream(OutputStream out) { 
         this(out, 76); 
     } 
      
     /** 
      * Encodes <code>len</code> bytes from the specified 
      * <code>byte</code> array starting at offset <code>off</code> to 
      * this output stream. 
      * 
      * @param      b     the data. 
      * @param      off   the start offset in the data. 
      * @param      len   the number of bytes to write. 
      * @exception  IOException  if an I/O error occurs. 
     */ 
     public void write(byte[] b, int off, int len) throws IOException { 
         for (int i = 0; i < len; i++) write(b[off + i]); 
     } 
      
     /** 
      * Encodes <code>b.length</code> bytes to this output stream. 
      * @param      b   the data to be written. 
      * @exception  IOException  if an I/O error occurs. 
     */ 
     public void write(byte[] b) throws IOException { 
         write(b, 0, b.length); 
     } 
      
     /** 
      * Encodes the specified <code>byte</code> to this output stream. 
      * @param      c   the <code>byte</code>. 
      * @exception  IOException  if an I/O error occurs. 
     */ 
     public void write(int c) throws IOException { 
         c = c & 255; 
         if (gotSpace) { 
             if (c == '\r' || c == '\n') output(' ', true); else output(' ', false); 
             gotSpace = false; 
         } 
         if (c == '\r') { 
             gotCR = true; 
             outputCRLF(); 
         } else { 
             if (c == '\n') { 
                 if (gotCR) ; else outputCRLF(); 
             } else if (c == ' ') { 
                 gotSpace = true; 
             } else if (c < 32 || c >= 127 || c == '=') output(c, true); else output(c, false); 
             gotCR = false; 
         } 
     } 
      
     /** 
      * Flushes this output stream and forces any buffered output bytes 
      * to be encoded out to the stream. 
      * @exception  IOException  if an I/O error occurs. 
     */ 
     public void flush() throws IOException { 
         out.flush(); 
     } 
      
     /** 
      * Forces any buffered output bytes to be encoded out to the stream 
      * and closes this output stream 
     */ 
      public void close() throws IOException { 
          out.close(); 
      } 
       
      private void outputCRLF() throws IOException { 
          out.write('\r'); 
          out.write('\n'); 
          count = 0; 
      } 
      private static final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'}; 
       
      protected void output(int c, boolean encode) throws IOException { 
          if (encode) { 
              if ((count += 3) > bytesPerLine) { 
                  out.write('='); 
                  out.write('\r'); 
                  out.write('\n'); 
                  count = 3; 
              } 
              out.write('='); 
              out.write(hex[c >> 4]); 
              out.write(hex[c & 15]); 
          } else { 
              if (++count > bytesPerLine) { 
                  out.write('='); 
                  out.write('\r'); 
                  out.write('\n'); 
                  count = 1; 
              } 
              out.write(c); 
          } 
      } 
  }

