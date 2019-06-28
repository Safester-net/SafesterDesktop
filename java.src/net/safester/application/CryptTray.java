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



import net.safester.application.util.UserPrefManager;
import java.awt.AWTException;
import java.awt.Frame;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.SystemUtils;

import com.safelogic.utilx.Debug;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Window;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;
import net.safester.application.version.Version;


/**
 * Tray Launcher	
 */
public class CryptTray { 

    /**
     * The debug flag
     */
    protected static boolean DEBUG = Debug.isSet(CryptTray.class);

    /**
     * Wrapper for SystemTray.isSupported(). 
     * To be used because we need to simulate wrapped method changes for tests
     * @return the value of SystemTray.isSupported().
     */
    public static boolean isSupported() {
        /*
        boolean simulateSystemTrayNotSupported = false;
        if (simulateSystemTrayNotSupported) {
            return false;
        }
        */
        
        return SystemTray.isSupported();
    }

    /**
     * The System Tray
     */
    private SystemTray tray = SystemTray.getSystemTray();
    /**
     * The Tray Icon
     */
    private TrayIcon trayIcon;
    /**
     * The Https Uploader Console instance
     */
    private Main main = null;

    
    /**
     * Constructor.
     *
     * Will launch Https Uploader Main Window and the Https Uploader Tray
     */
    public CryptTray() {
    }

    
    private void setVisibleAndOnTopOneSecond() throws SecurityException {
        main.setState(Frame.NORMAL);
        main.setVisible(true);

        if (SystemUtils.IS_OS_MAC_OSX) {
            main.setVisible(false);
            main.setAlwaysOnTop(false);
            main.setAlwaysOnTop(true);
            main.setVisible(true);

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        main.setAlwaysOnTop(false);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(CryptTray.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            t.start();
        }
    }
  
    /**
     * Start Safester as tray
     *
     * @param args args passed to jar
     */
    public void startAsTray(final Main main) {

        this.main = main;
        PopupMenu menu;
        MenuItem menuItem;
        System.setProperty("javax.swing.adjustPopupLocationToFit", "false");
        
        MessagesManager messagesManager = new MessagesManager();

        /*
         Console
         ____________
         Quit
         */
        menu = new PopupMenu(Parms.PRODUCT_NAME);

        // JMenuItems
        menuItem = new MenuItem(Parms.PRODUCT_NAME + " Console");
        
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               setVisibleAndOnTopOneSecond();
            }
        });

        menu.add(menuItem);

        menuItem = new MenuItem(messagesManager.getMessage("about"));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                About about = new About((Window)main);
            }
        });
        menu.add(menuItem);
        
        //Font font = menuItem.getFont().deriveFont(Font.BOLD);
        //menuItem.setFont(font);
        menu.addSeparator();

        // "Quit" menu item
        menuItem = new MenuItem(messagesManager.getMessage("logout") );
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.logout();
            }
        });
        menu.add(menuItem);

        //menuItem.setShortcut(new MenuShortcut(KeyEvent.VK_Q | KeyEvent.VK_CONTROL));
        
        ImageIcon i = new ImageIcon(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        trayIcon = new TrayIcon(i.getImage(), Parms.PRODUCT_NAME + " " + Version.VERSION, menu);
        //trayIcon.setImageAutoSize(true);

        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisibleAndOnTopOneSecond();
            }

        });
        
        try {
            tray.add(trayIcon);
        } catch (AWTException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(main, "Impossible to start " + Parms.PRODUCT_NAME + " : " + ex.toString());
        }

        UserPrefManager userPrefManager = new UserPrefManager();
        if (! userPrefManager.getBooleanPreference("DISPLAY_TRAY_MESSAGE_DONE")) {
            trayIcon.displayMessage(null, messagesManager.getMessage("click_the_icon_to_access") + " " + Parms.PRODUCT_NAME+ " " + messagesManager.getMessage("when_the_window_is_closed"), TrayIcon.MessageType.INFO);
            userPrefManager.setPreference("DISPLAY_TRAY_MESSAGE_DONE", true);
        }
       
    }


    /**
     * For Advanced Installer
     *
     * @param args
     */
    static void secondaryMain(String args[]) {

    }

    /**
     * debug tool
     */
    private static void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
            // System.out.println(this.getClass().getName() + " " + new Date() +
            // " " + s);
        }
    }

    /**
     * @return the trayIcon
     */
    public TrayIcon getTrayIcon() {
        return trayIcon;
    }
}
