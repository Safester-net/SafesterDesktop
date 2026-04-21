/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.safester.application.util;

import java.util.Objects;
import net.safester.application.scale.DisplayScaleLevel;
import net.safester.application.scale.DisplayScaleManager;

/**
 * Allows to easely scale the UI for the sun.java2d.uiScale
 * @author ndepo
 */
public class SunUiScalingUtil {

    public static final String SCALING_100 = "1.0";
    public static final String SCALING_125 = "1.25";
    public static final String SCALING_150 = "1.5";
    public static final String SCALING_200 = "2.0";
    public static final String SCALING_250 = "2.5";

    /**
     * Gest the Sun UI scaling stored in the preferences.
     * Will automatically fit for bigger screens
     * @return the Sun UI scaling stored in the preference
     */
    private static String getPreferenceScaling() {
        return DisplayScaleManager.getStoredLevel().getSunUiScale();
    }    

    /**
     * Sets in the preferences the Sun UI scaling to store
     * @param scaling the Sun UI scaling
     */
    public static void setPreferenceScaling(String scaling) {
        Objects.requireNonNull(scaling, "scaling cannot be null!");
        DisplayScaleLevel level = DisplayScaleLevel.fromSunUiScale(scaling);
        DisplayScaleManager.storeLevel(level);
    }
    
    
}
