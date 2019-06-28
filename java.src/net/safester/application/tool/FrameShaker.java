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
package net.safester.application.tool;

import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * Class to "shake" a Window
 * Derived from Swing Hack Chapter 5; use a JFrame instead of JDialog 
 * 
 * @author Nicolas de Pomereu
 *
 */

public class FrameShaker extends Object {

    public static final int SHAKE_DISTANCE = 10;
    public static final double SHAKE_CYCLE = 50;
    public static final int SHAKE_DURATION = 400;
    public static final int SHAKE_UPDATE = 5;

    private Window frame;
    private Point naturalLocation;
    private long startTime;
    private Timer shakeTimer;
    private final double HALF_PI = Math.PI / 2.0;
    private final double TWO_PI = Math.PI * 2.0;

    public FrameShaker (Window d) {
        frame = d;
    }

    public void startShake() {
        naturalLocation = frame.getLocation();
        startTime = System.currentTimeMillis();
        shakeTimer =
            new Timer(SHAKE_UPDATE,
                      new ActionListener() {
                          public void actionPerformed (ActionEvent e) {
                              // calculate elapsed time
                              long elapsed = System.currentTimeMillis() -
                                  startTime;
                              // use sin to calculate an x-offset
                              double waveOffset = (elapsed % SHAKE_CYCLE) /
                                  SHAKE_CYCLE;
                              double angle = waveOffset * TWO_PI;

                              // offset the x-location by an amount 
                              // proportional to the sine, up to
                              // shake_distance
                              int shakenX = (int) ((Math.sin (angle) *
                                                    SHAKE_DISTANCE) +
                                                   naturalLocation.x);
                              frame.setLocation (shakenX, naturalLocation.y);
                              frame.repaint();

                              // should we stop timer?
                              if (elapsed >= SHAKE_DURATION)
                                  stopShake();
                          }
                      }
                      );
        shakeTimer.start();
    }

    public void stopShake() {
        shakeTimer.stop();
        frame.setLocation (naturalLocation);
        frame.repaint();

        //frame.setVisible(false);
        //frame.repaint();
        
        try
        {
            Thread.sleep(400); // For Outlook so that it can repaint itself
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
                
        frame.repaint();
    }


//public static void main (String[] args) 
//{       
//    PgeepPassphrase  pgeepPassphrase = new PgeepPassphrase(null); 
//    
//     FrameShaker frameShaker = new FrameShaker (pgeepPassphrase);
//     frameShaker.startShake();
//
//    // wait (forever) for a non-null click and then quit
//    while (1 <= 1 ) {
//        try { Thread.sleep(100); }
//        catch (InterruptedException ie) {}
//    }
//    //System.exit(0);
//}

}


