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
package net.safester.noobs.clientserver;

import net.safester.noobs.clientserver.specs.Local;


/**
 * @author Nicolas de Pomereu
 * Defines an instance of Attachment
 * <br>
 * <br>
 * Warning:
 * <br>the RecipientLocal instance does *not* contain the Message Id, because
 * the info would be useless or dangerous (the contain class MessageLocal already contains it for get() operation,
 * and it's useless for put operation (created by the server just before insert). 
 */

public class AttachmentLocal implements Local
{
    private int attach_position;
    //File name on PC
    private String file_name;
    //File name on server for upload
    private String remoteFile_name;

    private long fileSize = 0;

    /** Return field value */
    public int getAttachPosition()
    {
        return this.attach_position;
    }
    /** Return field value */
    public String getFileName()
    {
        return this.file_name;
    }

    /**
     * @return the fileSize
     */
    public long getFileSize() {
        return fileSize;
    }

    
    public String getRemoteFileName(){
        return remoteFile_name;
    }

    public void setRemoteFileName(String name){
        remoteFile_name = name;
    }
    /** Set field value */
    public void setAttachPosition(int attach_position)
    {
        this.attach_position = attach_position;
    } 
    /** Set field value */
    public void setFileName(String file_name)
    {
        this.file_name = file_name;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "AttachmentLocal [attach_position=" + attach_position
                + ", file_name=" + file_name + "]";
    }
       
    
    
}

