/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.safester.clientserver.util;

import java.sql.Connection;

import net.safester.application.parms.ConnectionParms;

/**
 *
 * @author Nicolas de Pomereu
 *
 * Get a Awake Connection for tests
 * 
 */
public class TestAwakeConnection {

    public static Connection getConnection()
    {
        String login = "ndepomereu@safelogic.com";
        String password = "*2loveme$";
        
        ConnectionParms connectionParms = new ConnectionParms(login, password.toCharArray(), null, null);
        Connection connection = null;

        try
        {
            connection = connectionParms.getConnection();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return connection;
    }
}
