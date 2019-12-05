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
package net.safester.clientserver;

import net.safester.noobs.clientserver.specs.Local;

/**
 * Defines local instance of a Autoresponder
 * @author Nicolas de Pomereu
 */
public class AutoresponderLocal2 implements Local{

    private int userNumber = -1;
    private boolean responderOn = false;
    private long dtBegin = -1;
    private long dtExpire= -1;
    private String subject = null;
    private String body = null;

    /**
     * @return the userNumber
     */
    public int getUserNumber() {
        return userNumber;
    }

    /**
     * @param userNumber the userNumber to set
     */
    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    /**
     * @return the responderOn
     */
    public boolean getResponderOn() {
        return responderOn;
    }

    /**
     * @param responderOn the responderOn to set
     */
    public void setResponderOn(boolean responderOn) {
        this.responderOn = responderOn;
    }

    /**
     * @return the dtBegin
     */
    public long getDtBegin() {
        return dtBegin;
    }

    /**
     * @param dtBegin the dtBegin to set
     */
    public void setDtBegin(long dtBegin) {
        this.dtBegin = dtBegin;
    }

    /**
     * @return the dtExpire
     */
    public long getDtExpire() {
        return dtExpire;
    }

    /**
     * @param dtExpire the dtExpire to set
     */
    public void setDtExpire(long dtExpire) {
        this.dtExpire = dtExpire;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }


}
