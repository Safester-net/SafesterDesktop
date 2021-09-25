/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.safester.application.util;

import java.util.Objects;

/**
 * Allows to easely scale the UI for the sun.java2d.uiScale
 * @author ndepo
 */
public class SunUiScalingUtil {

    /**
     * Sets the Sun UI Scaling for the session, aka the sun.java2d.uiScale property
     * @param scaling the Sun UI scaling, between 1.0 and 2.5.
     */
    public static void setScalingForSession(String scaling) {
        Objects.requireNonNull(scaling, "scaling cannot be null!");
        System.setProperty("sun.java2d.uiScale", scaling);
    }
    
    /**
     * Gest the Sun UI scaling stored in the preferences.
     * @return the Sun UI scaling stored in the preference
     */
    public static String getPreferenceScaling() {
        String scaling = UserPrefManager.getPreference(UserPrefManager.SUN_SCALING, "1.0");
        return scaling;
    }    

    /**
     * Sets in the preferences the Sun UI scaling to store
     * @param scaling the Sun UI scaling
     */
    public static void setPreferenceScaling(String scaling) {
        Objects.requireNonNull(scaling, "scaling cannot be null!");
                 
        if (scaling.compareTo("1.0") < 0) {
            throw new IllegalArgumentException("Scaling can not be less than 1.0");
        }
        
        if (scaling.compareTo("3.0") > 0) {
            throw new IllegalArgumentException("Scaling can not be more than 3.0");
        }
        
        UserPrefManager.setPreference(UserPrefManager.SUN_SCALING, scaling);
    }
    
    
}
