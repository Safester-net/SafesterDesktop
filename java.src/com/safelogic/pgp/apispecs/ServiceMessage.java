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
package com.safelogic.pgp.apispecs;

import java.util.IllegalFormatException;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Describes a Service Message download from Host
 * <br>
 * A Service Message is in the format:
 * <br>
 * <smsg_id>id</smsg_id><smsg_content>content</smsg_content>
 * <br>
 * <br>
 * Method are implemented to :
 * <br> - set/get easely the Service Messqage id & content
 * <br> - Transport in String the ServiceMessage
 * @author Nicolas de Pomereu
 *
 */
public class ServiceMessage
{
    
    /** The Service Message Id */
    private String serviceMessageId = null;
    
    /** The Service Message Content */
    private String serviceMessageContent = null;
        
    
    
    
    /**
     * Constructor
     * @param serviceMessageId      the Service Message Id
     * @param serviceMessageContent      the Service Message Content
     *                              
     */
    public ServiceMessage(String serviceMessageId, String serviceMessageContent)
    {
        if (serviceMessageId == null)
        {
            throw new IllegalArgumentException("Service Message Id can not be null");
        }
        
        if (serviceMessageContent == null)
        {
            throw new IllegalArgumentException("Service Message Content can not be null");
        }
        
        this.serviceMessageId       = serviceMessageId;
        this.serviceMessageContent  = serviceMessageContent;
    }
    
        
    /**
     * Build a new Service message from a formated String as:
     * <br>
     * <tt>
     *      <SMSG_ID>serviceMessageId</SMSG_ID><SMSG_CONTENT>serviceMessageContent</SMSG_CONTENT>
     * </tt>
     * @param formatedServiceMessage   the formated String that contains the Service Message Id and
     *                                  content
     * @throws IllegalFormatException  if the message is not correctly formated                               
     */
    public static ServiceMessage getInstance(String formatedServiceMessage)
        throws IllegalFormatException 
    {
                
        String serviceMessageId = StringUtils.substringBetween(formatedServiceMessage, 
                                                            "<SMSG_ID>", 
                                                            "</SMSG_ID>");   
        
        if (serviceMessageId == null || serviceMessageId.equals(""))
        {
            throw new IllegalArgumentException("Bad format for <SMSG_ID>serviceMessageId</SMSG_ID>");
        }
        
        String serviceMessageContent = StringUtils.substringBetween(formatedServiceMessage, 
                                                                  "<SMSG_CONTENT>",
                                                                  "</SMSG_CONTENT>"); 
        
        if (serviceMessageContent == null || serviceMessageContent.equals(""))
        {
            throw new IllegalArgumentException("<SMSG_CONTENT>serviceMessageContent</SMSG_CONTENT>");
        }
        
        return new ServiceMessage(serviceMessageId, serviceMessageContent);
        
    }
    
    /**
     * Use this method to transport the service message 
     */
    public String toString()
    {
        String s = "<SMSG_ID>" + serviceMessageId + "</SMSG_ID>" +
                   "<SMSG_CONTENT>" + serviceMessageContent + "</SMSG_CONTENT>";
        
        return s;
    }


    /**
     * @return the Service Message Content
     */
    public String getServiceMessageContent()
    {
        return serviceMessageContent;
    }


    /**
     * @return the Service Message Id
     */
    public String getServiceMessageId()
    {
        return serviceMessageId;
    }
    
   
}
