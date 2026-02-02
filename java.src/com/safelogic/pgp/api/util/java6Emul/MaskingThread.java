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

/**
 * This class attempts to erase characters echoed to the console.
 */

class MaskingThread extends Thread {
   private volatile boolean stop;
   private char echochar = ' ';

  /**
   *@param prompt The prompt displayed to the user
   */
   public MaskingThread(String prompt) {
       java.lang.System.out.print(prompt);
   }

  /**
   * Begin masking until asked to stop.
   */
   public void run() {

      int priority = Thread.currentThread().getPriority();
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

      try {
         stop = true;
         while(stop) {
             java.lang.System.out.print("\010" + echochar);
           try {
              // attempt masking at this rate
              Thread.currentThread().sleep(1);
           }catch (InterruptedException iex) {
              Thread.currentThread().interrupt();
              return;
           }
         }
      } finally { // restore the original priority
         Thread.currentThread().setPriority(priority);
      }
   }

  /**
   * Instruct the thread to stop masking.
   */
   public void stopMasking() {
      this.stop = false;
   }
}

