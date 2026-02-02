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
package com.safelogic.pgp.api.toolkit;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.safelogic.pgp.api.engines.CryptoEngine;
import com.safelogic.pgp.api.util.licensing.LicenseChecker;
import com.safelogic.pgp.api.util.licensing.LicensesStatus;
import com.safelogic.pgp.util.FileUtil;

/**
 * @author Nicolas de Pomereu
 *
 * Contains all infos related to a long encryption tasks on files.
 */
public class TaskContainer
{
    /** The default value when doing a synchronuous task */
    public static int DEFAULT_SYNC_TASK = 0;
    
    /** The licenseStatus for Toolkit usage */
    private static int LICENSE_STATUS = -999;
     
    /** The store for all the things related to the task */
    public static Map<Integer, CryptoEngine> tasksEngines   = new HashMap<Integer, CryptoEngine>();
    
    public static Map<Integer, Integer> tasksPercentProgress    = new HashMap<Integer, Integer>();
    public static Map<Integer, Boolean> tasksOperationOk        = new HashMap<Integer, Boolean>();
    public static Map<Integer, String> tasksErrorCode           = new HashMap<Integer, String>();
    public static Map<Integer, Exception> tasksException        = new HashMap<Integer, Exception>();
    public static Map<Integer, String> tasksStackTrace          = new HashMap<Integer, String>();
    public static Map<Integer, String> tasksSignatureStatus     = new HashMap<Integer, String>();
    
    // The TaskNumber
    private int taskNumber = 0;
       
    /**
     * @param taskNumber
     */
    public TaskContainer(int taskNumber)
    {
        try
        {
            this.taskNumber = taskNumber;             
            
            if (LICENSE_STATUS == -999)
            {
                LICENSE_STATUS = LicenseChecker.getLicenseCheckerForToolkit();
            }
        }
        catch (Exception e)
        {
            // Paranoïd! Should never happen, but it's cleaner
            e.printStackTrace();
        }     
        
    }
    
    /**
     * 
     * @return true if the license is valid
     */
    public boolean isLicenseValid()
    {
        String userDir = System.getProperty("user.dir");
                
        if (userDir.equalsIgnoreCase("I:\\_dev_pgeep\\java_API"))
        {   
            //System.out.println(userDir);
            return true;
        }
        
        if (LICENSE_STATUS <0 )
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * @param operationOk the operationOk to set
     */
    public void setOperationOk()
    {            
        tasksOperationOk.put(taskNumber, true);
                
        // Throw an Exception so that the task does get executed if the
        // license is invalid
        if (! isLicenseValid())
        {
            throw new IllegalArgumentException("Invalid License File. Reason: " + LICENSE_STATUS);
        }
    }
    
    
    /**
     * @return the operationOk
     */
    public boolean isOperationOk()
    {
        // If the license is invalid, we don't care about the operation
        if (! isLicenseValid())
        {
            return false;
        }
        
        //System.out.println("taskNumber      :" + taskNumber);
        //System.out.println("tasksOperationOk:" + tasksOperationOk);        
        //System.out.println("tasksOperationOk.get(taskNumber): " 
        //            + tasksOperationOk.get(taskNumber));
        
        // Necessary to avoid nullPointerException
        Boolean myBool = tasksOperationOk.get(taskNumber);
        if (myBool == null)
        {
            return false;
        }
        else
        {
            return myBool;
        }

    }
    
    /**
     * @return the errorCode
     */
    public String getErrorCode()
    {
        // If the license is invalid, we send in return the license code error
        // As a String. We musto do better
        
        if (! isLicenseValid())
        {
            return (LICENSE_STATUS + " " + getLicenseMessage(LICENSE_STATUS));
        }
        
        return tasksErrorCode.get(taskNumber);
    }
    

    //
    // Return the license error message in plain language with the corresponding long return code
    //
    String getLicenseMessage(int status) 
    {
        String strResult;

        if (status == LicensesStatus.LICENSE_KEY_FILE_NOT_FOUND)
        {
            strResult = "LICENSE_KEY_FILE_NOT_FOUND";
        }
        else if (status == LicensesStatus.LICENSE_SIGN_SYSTEM_ERROR)
        {
            strResult = "LICENSE_SIGN_SYSTEM_ERROR";
        }
        else if (status == LicensesStatus.LICENSE_INVALID_FILE_NOT_FOUND)
        {
            strResult = "LICENSE_FILE_NOT_FOUND";
        }
        else if (status == LicensesStatus.LICENSE_INVALID_SIGNATURE)
        {
            strResult = "LICENSE_INVALID_SIGNATURE";
        }
        else if (status == LicensesStatus.LICENSE_INVALID_EXPIRED)
        {
            strResult = "LICENSE_INVALID_EXPIRED";
        }
        else if (status == LicensesStatus.LICENSE_INVALID_NO_MORE_TRIAL)
        {
            strResult = "LICENSE_INVALID_NO_MORE_TRIAL";
        }
        else
        {
            strResult = "Unknown License error...";
        }

        return strResult;
    }    
    
    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode, Exception e)
    {
        tasksErrorCode.put(taskNumber, errorCode);
        tasksException.put(taskNumber, e);
        
        tasksOperationOk.put(taskNumber, false);
        
    }
    
    /**
     * @return the exception
     */
    public String getException()
    {
        Exception e = tasksException.get(taskNumber);
        if(e != null)
        {
            return e.toString();
        }
        return null;
        
    }
        
    /**
     * @return the stackTrace
     */
    public String getStackTrace()
    {
        Exception e = tasksException.get(taskNumber);
        if (e == null)
        {
            return null;
        }
        
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        
        PrintWriter pw = new PrintWriter(byteArrayOutputStream);
        e.printStackTrace(pw);
        pw.close();
        
        String sException = byteArrayOutputStream.toString();                
        return sException;
    }    
    
    /**
     * @param percentProgress the percentProgress to set
     */
    public void setEngine(CryptoEngine cryptoEngine)
    {
        if (taskNumber == 0)
        {
            return;
        }
        
        tasksEngines.put(taskNumber, cryptoEngine);
    }    
    
    
    /**
     * Cancel a background task.
     * @param taskNumber        the task number thats references the operation.
     */
    public boolean cancelTask(int taskNumber)
    {
        if (! tasksEngines.containsKey(taskNumber))
        {
            return false;
        }
        
        CryptoEngine cryptoEngine = tasksEngines.get(taskNumber);
        cryptoEngine.interrupt();
        
        return true;
    }
    
    /**
     * @return the percentProgress
     */
    public int getPercentProgress()
    {   
        Integer integer = tasksPercentProgress.get(taskNumber);
        
        if (integer == null)
        {
            return 0;
        }
        else
        {
            return integer.intValue();
        }
    }
    /**
     * @param percentProgress the percentProgress to set
     */
    public void setPercentProgress(int percentProgress)
    {
        if (taskNumber == 0)
        {
            // Nothing;
        }
        
        if (percentProgress < 0 || percentProgress > 100)
        {
            throw new IllegalArgumentException("Invalid value - percentProgress must be between 0 and 100. Value is: " + percentProgress);
        }
        
        tasksPercentProgress.put(taskNumber, percentProgress);
    }

    /**
     * @return the tasksSignatureStatus
     */
    public String getSignatureStatus()
    {
        return tasksSignatureStatus.get(taskNumber);
    }
    
    /**
     * @param tasksSignatureStatus the tasksSignatureStatus to set
     */
    public void setSignatureStatus( String SignatureStatus)
    {
        tasksSignatureStatus.put(taskNumber, SignatureStatus);
    }
    
    
    @Override
    public String toString()
    {
        String myString =  "Task Number     : " + this.taskNumber 
                         +  FileUtil.CR_LF
                         + "Signature Status: " + this.getSignatureStatus()        
                         +  FileUtil.CR_LF
                         + "Percent Progress: " + this.getPercentProgress()
                         +  FileUtil.CR_LF
                         + "Error Code      : " + this.getErrorCode()
                         +  FileUtil.CR_LF
                         + "Exception       : " + this.getException()
                         +  FileUtil.CR_LF
                         + "Stack Trace     : " 
                           + FileUtil.CR_LF + this.getStackTrace()                         
                         ;
       return myString;                  
                             
    }
    
    /**
     *  MAIN METHOD    
     * @param args
     */
    public static void main(String[] args)         
    { 
        TaskContainer taskContainer = new TaskContainer(1);
        taskContainer.setOperationOk();
        taskContainer.setPercentProgress(50);
        taskContainer.setErrorCode(null, null);
        taskContainer.setSignatureStatus(null);
        
        System.out.println(taskContainer);
    }
        
}
