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
package net.safester.application.engines;

import com.safelogic.utilx.Debug;

/**
 * Dedicated wait engine that starts quickly a new Progress Monitor.
 * To be used when main thread is too long to start
 * All operations are done in the run().
 * 
 */
public class WaiterEngine extends Thread
{   
    /** A blank */
    private static final String BLANK = " ";

    /** The debug flag */
    protected boolean DEBUG = Debug.isSet(this);

   /** Current value of progression out of MAXIMUM_PROGRESS */
    private int m_current;

    /** Init Message */
    private String m_initMessage = null;
    
    /** Note to pass to the progression bar */
    private String m_note = null;

    /** Maximum value of progression bar (defaults to 100)*/
    public static int MAXIMUM = 100;
    
    /** 
     * if true waiter will be stopped. 
     * *Necessary* to stop this thread from another thread 
     */
    private boolean waiterStop = false;
    
    /**
     * Constructor.
     * @param initMessage   the init please wait message
     */
    public WaiterEngine(String initMessage)
    {
        m_current = 0;
        m_initMessage = initMessage;
    }

    /**
     * @param number or blanks to return 
     * @return  a numner * blank string
     */ 
    private String getBlanks(int number)
    {
        String blanks = "";
        for (int i = 0; i < number; i++)
        {
            blanks += BLANK;
        }
        
        return blanks;
    }
    
    /**
     * Run the Crypto Engine when this thread is started
     */
    @Override
    public void run()
    {        
        
        int i = 0;
        
        // Add a long blank to be sure following message won't be cut
        setNote(m_initMessage + getBlanks(40));
         
        while (true)
        {   
            if (waiterStop)
            {
                setCurrent(MAXIMUM);
                return;
            }
            
            i++;
            if (i % 100 == 0)
            {
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    //e.printStackTrace();
                }
            }
            
            if (i < 1800 * 100)
            {
                setCurrent(i/1800);           
            }
        }
           
    }

    
    
    /**
     * @return the waiterStop
     */
    public boolean isWaiterStop()
    {
        return waiterStop;
    }

    /**
     * @param waiterStop the waiterStop to set
     */
    public void setWaiterStop(boolean waiterStop)
    {
        //System.out.println("m_waiterEngine.setWaiterStop(" + waiterStop + ")");
        this.waiterStop = waiterStop;
    }

    /**
     * @return the current value of ProgressMonitor
     */
    public int getCurrent()
    {
        return m_current;
    }

    /**
     * @param current the current progression value to set by the task
     */
    public void setCurrent(int current)
    {
        m_current = current;
    }

    /**
     * @return the note for the progression bar
     */
    public String getNote()
    {
        return m_note;
    }

    /**
     * @param note the note for the progression bar
     */
    public void setNote(String note)
    {
        m_note = note;
    }
   
    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }
}
