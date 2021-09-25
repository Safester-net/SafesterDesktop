/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.safester.application.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Objects;

/**
 * Allows to easely scale the UI for the sun.java2d.uiScale
 * @author ndepo
 */
public class SunUiScalingUtil {

    public static final String SCALING_100 = "1.0";
    public static final String SCALING_150 = "1.5";
    public static final String SCALING_200 = "2.0";
    public static final String SCALING_250 = "2.5";
    public static final String SCALING_300 = "3.0";
    private static final double MIN_WIDTH_FOR_SCALING_150 = 2600;        

    
    /**
     * Gest the Sun UI scaling stored in the preferences.
     * Will automatically fit for bigger screens
     * @return the Sun UI scaling stored in the preference
     */
    private static String getPreferenceScaling() {
        
        String defaultScaling = SCALING_100;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        
        if (width > MIN_WIDTH_FOR_SCALING_150) {
            defaultScaling = SCALING_150;
        }
        
        String scaling = UserPrefManager.getPreference(UserPrefManager.SUN_SCALING, defaultScaling);
        return scaling;
    }    

    /**
     * Sets in the preferences the Sun UI scaling to store
     * @param scaling the Sun UI scaling
     */
    public static void setPreferenceScaling(String scaling) {
        Objects.requireNonNull(scaling, "scaling cannot be null!");
                 
        if (scaling.compareTo(SCALING_100) < 0) {
            throw new IllegalArgumentException("Scaling can not be less than 1.0");
        }
        
        if (scaling.compareTo(SCALING_300) > 0) {
            throw new IllegalArgumentException("Scaling can not be more than 3.0");
        }
        
        UserPrefManager.setPreference(UserPrefManager.SUN_SCALING, scaling);
    }
    
    
}
