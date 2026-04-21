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
package net.safester.application.scale;

import java.util.Objects;
import net.safester.application.util.UserPrefManager;

/**
 * Central manager for the global application display scale.
 */
public final class DisplayScaleManager {

    public static final String DISPLAY_SIZE_LEVEL = "DISPLAY_SIZE_LEVEL";

    private DisplayScaleManager() {
    }

    public static void initializePreferenceIfMissing() {
        String storedValue = UserPrefManager.getPreference(DISPLAY_SIZE_LEVEL);
        if (storedValue != null && !storedValue.trim().isEmpty()) {
            return;
        }

        DisplayScaleLevel migratedLevel = migrateLegacyLevel();
        storeLevel(migratedLevel);
    }

    public static DisplayScaleLevel getStoredLevel() {
        initializePreferenceIfMissing();
        String storedValue = UserPrefManager.getPreference(DISPLAY_SIZE_LEVEL);
        return DisplayScaleLevel.fromStoredValue(storedValue);
    }

    public static DisplayScaleLevel getCurrentLevel() {
        return getStoredLevel();
    }

    public static void storeLevel(DisplayScaleLevel level) {
        Objects.requireNonNull(level, "level cannot be null!");

        UserPrefManager.setPreference(DISPLAY_SIZE_LEVEL, level.getStoredValue());
        UserPrefManager.setPreference(UserPrefManager.SUN_SCALING, level.getSunUiScale());
        removeFlatLafScalingPreference();
    }

    public static void applyStartupScale() {
        initializePreferenceIfMissing();
        DisplayScaleLevel level = getStoredLevel();
        System.setProperty("sun.java2d.uiScale", level.getSunUiScale());
        System.clearProperty("flatlaf.uiScale");
    }

    public static void removeLegacyScalingPreferences() {
        UserPrefManager.removePreference(UserPrefManager.SUN_SCALING);
        removeFlatLafScalingPreference();
    }

    public static double getScaleFactor() {
        return getCurrentLevel().getScaleFactor();
    }

    public static int getScaledSize(int baseSize) {
        return getCurrentLevel().scale(baseSize);
    }

    public static boolean requiresRestart() {
        return true;
    }

    public static String getMenuLabel(DisplayScaleLevel level) {
        Objects.requireNonNull(level, "level cannot be null!");
        return level.getDisplayLabel();
    }

    private static DisplayScaleLevel migrateLegacyLevel() {
        String sunScaling = UserPrefManager.getPreference(UserPrefManager.SUN_SCALING);
        if (sunScaling != null && !sunScaling.trim().isEmpty()) {
            return DisplayScaleLevel.fromSunUiScale(sunScaling);
        }

        String flatLafScaling = UserPrefManager.getPreference(UserPrefManager.FLATLAF_SCALING);
        if (flatLafScaling != null && !flatLafScaling.trim().isEmpty()) {
            return DisplayScaleLevel.fromSunUiScale(flatLafScaling);
        }

        return DisplayScaleLevel.COMFORTABLE;
    }

    private static void removeFlatLafScalingPreference() {
        UserPrefManager.removePreference(UserPrefManager.FLATLAF_SCALING);
    }
}
