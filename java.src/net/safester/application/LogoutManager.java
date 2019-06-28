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
package net.safester.application;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.safester.application.util.CacheFileHandler;

/**
 *
 * @author Nicolas de Pomereu
 */
public class LogoutManager {
    
    private static boolean DELETE_CACHED_FILE_RUNNING = true;
    private static int MAXIMUM_LOGOUT_SECONDS_WAIT = 4;
    
    public static void logoutAndExit() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {

                    CacheFileHandler cacheFileHandler = new CacheFileHandler();
                    cacheFileHandler.deletedAllCachedFiles();

                } catch (Exception e) {
                    e.printStackTrace(); // We don' bother user
                }
                finally {
                    DELETE_CACHED_FILE_RUNNING = false;
                }
            }
        };
        t.start();
            
        long start = new Date().getTime();
        while (DELETE_CACHED_FILE_RUNNING) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(LogoutManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            long now = new Date().getTime();
            
            if ((start - now) * 1000 >= MAXIMUM_LOGOUT_SECONDS_WAIT) {
                break;
            }
        }
    }    
}
