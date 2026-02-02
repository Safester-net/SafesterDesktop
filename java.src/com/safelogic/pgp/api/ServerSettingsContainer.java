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
package com.safelogic.pgp.api;

/**
 * @author Nicolas de Pomereu
 * 
 * Container for Server Settings info for a Public Key
 * 
 */
public class ServerSettingsContainer
{
    /** If true, keep the Public Key on cGeep Central Directory */
    private boolean keepKeyOnCentralDir = false;
     
    /** If true, user will be on stealth mode on Central Directory */
    private boolean stealthMode = false;
    
    /** If true, user wants to receive info */
    private boolean receiveInfos = false;    

    /**
     * Constructor
     */
    public ServerSettingsContainer()
    {
        
    }

    /**
     * @return the keepKeyOnCentralDir
     */
    public boolean isKeepKeyOnCentralDir()
    {
        return keepKeyOnCentralDir;
    }

    /**
     * @param keepKeyOnCentralDir the keepKeyOnCentralDir to set
     */
    public void setKeepKeyOnCentralDir(boolean keepKeyOnCentralDir)
    {
        this.keepKeyOnCentralDir = keepKeyOnCentralDir;
    }

    /**
     * @return the stealthMode
     */
    public boolean isStealthMode()
    {
        return stealthMode;
    }

    /**
     * @param stealthMode the stealthMode to set
     */
    public void setStealthMode(boolean stealthMode)
    {
        this.stealthMode = stealthMode;
    }

    /**
     * @return the receiveInfos
     */
    public boolean isReceiveInfos()
    {
        return receiveInfos;
    }

    /**
     * @param receiveInfos the receiveInfos to set
     */
    public void setReceiveInfos(boolean receiveInfos)
    {
        this.receiveInfos = receiveInfos;
    }
    

    

}

/**
 * 
 */
