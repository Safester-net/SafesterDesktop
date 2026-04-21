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
package net.safester.application.icons;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import net.safester.application.scale.DisplayScaleLevel;
import net.safester.application.scale.DisplayScaleManager;

/**
 * Centralized icon loader that selects the best raster source for the current
 * display scale while keeping the same logical icon size in Swing layouts.
 */
public final class AppIconManager {

    private static final String FILES_2_ROOT = "/net/safester/application/images/files_2";
    private static final String[] APPLICATION_ICON_PATHS = {
        "/net/safester/application/images/files/safester-icon-60.png",
        "/net/safester/application/images/files/safester-icon-80.png"
    };
    private static final String[] LOGIN_LOGO_PATHS = {
        "/net/safester/application/images/files/logo-blue-on-white-300x99.png",
        "/net/safester/application/images/files/logo-blue-on-white-350x116.png",
        "/net/safester/application/images/files/logo-blue-on-white-2019.png"
    };
    private static final int[] SOURCE_SIZES = {16, 24, 32, 48, 64};
    private static final Pattern FILES_2_PATH_PATTERN = Pattern.compile(
            "(?:^|.*/)(?:net/safester/application/)?images/files_2/(\\d+)x(\\d+)/([^/]+)\\.png$");

    private static final ConcurrentMap<String, BufferedImage> RAW_IMAGE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, ImageIcon> ICON_CACHE = new ConcurrentHashMap<>();

    private AppIconManager() {
    }

    /**
     * Returns an icon that keeps the requested logical size in the UI while
     * exposing a sharper raster variant for the active display scale.
     *
     * @param iconId the icon identifier
     * @param logicalSize the logical Swing icon size in pixels
     * @return the icon ready to use on Swing components
     */
    public static ImageIcon getIcon(AppIconId iconId, int logicalSize) {
        Objects.requireNonNull(iconId, "iconId cannot be null!");
        return getIcon(iconId.getBaseName(), logicalSize);
    }

    /**
     * Returns an icon from the files_2 inventory by base file name.
     *
     * @param baseName the icon file name without extension
     * @param logicalSize the logical Swing icon size in pixels
     * @return the icon ready to use on Swing components
     */
    public static ImageIcon getIcon(String baseName, int logicalSize) {
        Objects.requireNonNull(baseName, "baseName cannot be null!");

        if (logicalSize <= 0) {
            throw new IllegalArgumentException("logicalSize must be greater than 0!");
        }

        String cacheKey = baseName
                + "|"
                + logicalSize
                + "|"
                + DisplayScaleManager.getCurrentLevel().getStoredValue();

        return ICON_CACHE.computeIfAbsent(cacheKey, key -> buildIcon(baseName, logicalSize));
    }

    /**
     * Returns a HiDPI-aware replacement when the path points to files_2.
     *
     * @param resourcePath the resource path or URL description
     * @return a replacement icon, or null when the path is not managed here
     */
    public static ImageIcon getIconFromPath(String resourcePath) {
        if (isLoginLogoPath(resourcePath)) {
            return getLoginLogoIcon();
        }

        IconResource iconResource = parseIconResource(resourcePath);
        if (iconResource == null) {
            return null;
        }

        return getIcon(iconResource.baseName, iconResource.logicalSize);
    }

    public static ImageIcon getReplacementIcon(ImageIcon imageIcon) {
        if (imageIcon == null) {
            return null;
        }

        return getIconFromPath(imageIcon.getDescription());
    }

    public static ImageIcon getApplicationIcon() {
        String cacheKey = "APPLICATION_ICON|" + DisplayScaleManager.getCurrentLevel().getStoredValue();
        return ICON_CACHE.computeIfAbsent(cacheKey, key -> buildApplicationIcon());
    }

    public static ImageIcon getLoginLogoIcon() {
        String cacheKey = "LOGIN_LOGO|" + DisplayScaleManager.getCurrentLevel().getStoredValue();
        return ICON_CACHE.computeIfAbsent(cacheKey, key -> buildMultiResolutionIcon(LOGIN_LOGO_PATHS));
    }

    /**
     * Clears the in-memory icon caches.
     */
    public static void clearCache() {
        ICON_CACHE.clear();
        RAW_IMAGE_CACHE.clear();
    }

    private static ImageIcon buildIcon(String baseName, int logicalSize) {
        BufferedImage logicalSource = loadBestSource(baseName, logicalSize);
        BufferedImage logicalVariant = adaptVariant(logicalSource, logicalSize);

        DisplayScaleLevel scaleLevel = DisplayScaleManager.getCurrentLevel();
        if (scaleLevel == DisplayScaleLevel.NORMAL) {
            return new ImageIcon(logicalVariant, buildDescription(baseName, logicalSize));
        }

        int scaledSize = DisplayScaleManager.getScaledSize(logicalSize);
        BufferedImage hiDpiSource = loadBestSource(baseName, scaledSize);
        BufferedImage hiDpiVariant = adaptVariant(hiDpiSource, scaledSize);

        if (logicalVariant.getWidth() == hiDpiVariant.getWidth()
                && logicalVariant.getHeight() == hiDpiVariant.getHeight()) {
            return new ImageIcon(logicalVariant, buildDescription(baseName, logicalSize));
        }

        Image multiResolutionImage = new BaseMultiResolutionImage(logicalVariant, hiDpiVariant);
        return new ImageIcon(multiResolutionImage, buildDescription(baseName, logicalSize));
    }

    private static ImageIcon buildApplicationIcon() {
        return buildMultiResolutionIcon(APPLICATION_ICON_PATHS);
    }

    private static ImageIcon buildMultiResolutionIcon(String[] imagePaths) {
        List<Image> variants = new ArrayList<>();
        for (String imagePath : imagePaths) {
            URL resource = AppIconManager.class.getResource(imagePath);
            if (resource != null) {
                variants.add(loadRawImage(imagePath));
            }
        }

        if (variants.isEmpty()) {
            throw new IllegalArgumentException("No image resource found!");
        }

        if (variants.size() == 1) {
            return new ImageIcon(variants.get(0), imagePaths[0]);
        }

        Image multiResolutionImage = new BaseMultiResolutionImage(variants.toArray(new Image[variants.size()]));
        return new ImageIcon(multiResolutionImage, imagePaths[0]);
    }

    private static BufferedImage loadBestSource(String baseName, int requestedSize) {
        List<Integer> availableSizes = getAvailableSizes(baseName);
        if (availableSizes.isEmpty()) {
            throw new IllegalArgumentException("No image resource found for icon " + baseName + "!");
        }

        int sourceSize = selectBestSize(availableSizes, requestedSize);
        String resourcePath = FILES_2_ROOT + "/" + sourceSize + "x" + sourceSize + "/" + baseName + ".png";
        return loadRawImage(resourcePath);
    }

    private static List<Integer> getAvailableSizes(String baseName) {
        List<Integer> availableSizes = new ArrayList<>();

        for (int sourceSize : SOURCE_SIZES) {
            String resourcePath = FILES_2_ROOT + "/" + sourceSize + "x" + sourceSize + "/" + baseName + ".png";
            URL resource = AppIconManager.class.getResource(resourcePath);
            if (resource != null) {
                availableSizes.add(sourceSize);
            }
        }

        return availableSizes;
    }

    private static int selectBestSize(List<Integer> availableSizes, int requestedSize) {
        for (Integer availableSize : availableSizes) {
            if (availableSize >= requestedSize) {
                return availableSize;
            }
        }

        return availableSizes.get(availableSizes.size() - 1);
    }

    private static BufferedImage loadRawImage(String resourcePath) {
        return RAW_IMAGE_CACHE.computeIfAbsent(resourcePath, AppIconManager::readImage);
    }

    private static BufferedImage readImage(String resourcePath) {
        try (InputStream inputStream = AppIconManager.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Missing image resource: " + resourcePath);
            }

            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IllegalArgumentException("Unreadable image resource: " + resourcePath);
            }

            return image;
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to load image resource " + resourcePath + ": " + exception, exception);
        }
    }

    private static BufferedImage adaptVariant(BufferedImage sourceImage, int targetSize) {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();

        if (sourceWidth <= targetSize && sourceHeight <= targetSize) {
            return sourceImage;
        }

        double ratio = Math.min((double) targetSize / sourceWidth, (double) targetSize / sourceHeight);
        int targetWidth = Math.max(1, (int) Math.round(sourceWidth * ratio));
        int targetHeight = Math.max(1, (int) Math.round(sourceHeight * ratio));

        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = scaledImage.createGraphics();
        try {
            graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2d.drawImage(sourceImage, 0, 0, targetWidth, targetHeight, null);
        } finally {
            graphics2d.dispose();
        }

        return scaledImage;
    }

    private static IconResource parseIconResource(String resourcePath) {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            return null;
        }

        String normalizedPath = resourcePath.replace('\\', '/');
        Matcher matcher = FILES_2_PATH_PATTERN.matcher(normalizedPath);
        if (!matcher.matches()) {
            return null;
        }

        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        if (width != height || width <= 0) {
            return null;
        }

        return new IconResource(matcher.group(3), width);
    }

    private static boolean isLoginLogoPath(String resourcePath) {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            return false;
        }

        String normalizedPath = resourcePath.replace('\\', '/');
        return normalizedPath.endsWith("/images/files/logo-blue-on-white-300x99.png")
                || normalizedPath.endsWith("/images/files/logo-blue-on-white-350x116.png")
                || normalizedPath.endsWith("/images/files/logo-blue-on-white-2019.png");
    }

    private static String buildDescription(String baseName, int logicalSize) {
        return FILES_2_ROOT + "/" + logicalSize + "x" + logicalSize + "/" + baseName + ".png";
    }

    private static final class IconResource {

        private final String baseName;
        private final int logicalSize;

        private IconResource(String baseName, int logicalSize) {
            this.baseName = baseName;
            this.logicalSize = logicalSize;
        }
    }
}
