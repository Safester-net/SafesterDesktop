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
package net.safester.application.util;

import java.awt.Component;
import java.awt.Container;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.safester.application.icons.AppIconId;
import net.safester.application.icons.AppIconManager;

/**
 * Central installer for screen visuals that are too rigid in the Swing forms.
 */
public final class UiVisualsInstaller {

    private UiVisualsInstaller() {
    }

    public static void refreshComponentTreeIcons(Container container) {
        if (container == null) {
            return;
        }

        refreshComponentIcon(container);
        Component[] components = container.getComponents();
        for (Component component : components) {
            refreshComponentIcon(component);
            if (component instanceof Container) {
                refreshComponentTreeIcons((Container) component);
            }
        }
    }

    public static void applyIcon(AbstractButton button, AppIconId iconId, int logicalSize) {
        if (button == null) {
            return;
        }

        button.setIcon(AppIconManager.getIcon(iconId, logicalSize));
    }

    public static void applyIcon(JLabel label, AppIconId iconId, int logicalSize) {
        if (label == null) {
            return;
        }

        label.setIcon(AppIconManager.getIcon(iconId, logicalSize));
    }

    public static Icon getIcon(AppIconId iconId, int logicalSize) {
        return AppIconManager.getIcon(iconId, logicalSize);
    }

    private static void refreshComponentIcon(Component component) {
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            label.setIcon(replaceIfManaged(label.getIcon()));
            return;
        }

        if (component instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) component;
            button.setIcon(replaceIfManaged(button.getIcon()));
            button.setDisabledIcon(replaceIfManaged(button.getDisabledIcon()));
            button.setPressedIcon(replaceIfManaged(button.getPressedIcon()));
            button.setRolloverIcon(replaceIfManaged(button.getRolloverIcon()));
            button.setRolloverSelectedIcon(replaceIfManaged(button.getRolloverSelectedIcon()));
            button.setSelectedIcon(replaceIfManaged(button.getSelectedIcon()));
            button.setDisabledSelectedIcon(replaceIfManaged(button.getDisabledSelectedIcon()));

            if (button instanceof JMenu) {
                refreshMenuIcons((JMenu) button);
            }
        }
    }

    private static void refreshMenuIcons(JMenu menu) {
        for (Component component : menu.getMenuComponents()) {
            refreshComponentIcon(component);
            if (component instanceof Container) {
                refreshComponentTreeIcons((Container) component);
            }
        }
    }

    private static Icon replaceIfManaged(Icon icon) {
        if (!(icon instanceof ImageIcon)) {
            return icon;
        }

        ImageIcon replacementIcon = AppIconManager.getReplacementIcon((ImageIcon) icon);
        if (replacementIcon == null) {
            return icon;
        }

        return replacementIcon;
    }
}
