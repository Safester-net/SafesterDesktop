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

/**
 * Supported global display scale levels.
 */
public enum DisplayScaleLevel {

    NORMAL("NORMAL", "100%", 1.0d),
    COMFORTABLE("COMFORTABLE", "125% (Default)", 1.25d),
    LARGE("LARGE", "150%", 1.5d);

    private final String storedValue;
    private final String displayLabel;
    private final double scaleFactor;

    DisplayScaleLevel(String storedValue, String displayLabel, double scaleFactor) {
        this.storedValue = Objects.requireNonNull(storedValue, "storedValue cannot be null!");
        this.displayLabel = Objects.requireNonNull(displayLabel, "displayLabel cannot be null!");
        this.scaleFactor = scaleFactor;
    }

    public String getStoredValue() {
        return storedValue;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public String getSunUiScale() {
        if (scaleFactor == 1.0d) {
            return "1.0";
        }
        if (scaleFactor == 1.25d) {
            return "1.25";
        }
        if (scaleFactor == 1.5d) {
            return "1.5";
        }

        return Double.toString(scaleFactor);
    }

    public int scale(int baseSize) {
        if (baseSize <= 0) {
            throw new IllegalArgumentException("baseSize must be greater than 0!");
        }

        return Math.max(baseSize, (int) Math.round(baseSize * scaleFactor));
    }

    public static DisplayScaleLevel fromStoredValue(String storedValue) {
        if (storedValue == null || storedValue.trim().isEmpty()) {
            return COMFORTABLE;
        }

        String normalizedStoredValue = storedValue.trim();
        for (DisplayScaleLevel level : values()) {
            if (level.storedValue.equalsIgnoreCase(normalizedStoredValue)) {
                return level;
            }
        }

        return fromSunUiScale(normalizedStoredValue);
    }

    public static DisplayScaleLevel fromSunUiScale(String scaling) {
        if (scaling == null || scaling.trim().isEmpty()) {
            return COMFORTABLE;
        }

        String normalizedScaling = scaling.trim();
        if ("1.0".equals(normalizedScaling) || "1".equals(normalizedScaling)) {
            return NORMAL;
        }
        if ("1.25".equals(normalizedScaling)) {
            return COMFORTABLE;
        }
        if ("1.5".equals(normalizedScaling)) {
            return LARGE;
        }

        try {
            double scalingValue = Double.parseDouble(normalizedScaling);
            if (scalingValue <= 1.0d) {
                return NORMAL;
            }
            if (scalingValue <= 1.25d) {
                return COMFORTABLE;
            }
            return LARGE;
        } catch (NumberFormatException exception) {
            return COMFORTABLE;
        }
    }
}
