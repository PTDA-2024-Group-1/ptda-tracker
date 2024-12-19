package com.ptda.tracker.util;

import javax.swing.*;
import java.net.URL;
import java.util.Objects;

/**
 * ImageResourceManager handles theme-based image retrieval and ensures fallback icons are used
 * if the specified resources are unavailable.
 */
public class ImageResourceManager {

    private static ImageIcon lightIcon;
    private static ImageIcon darkIcon;

    // Static initialization block for loading images
    static {
        try {
            System.out.println("Loading theme-based icons...");

            // Load light theme icon
            URL lightUrl = ImageResourceManager.class.getResource("/images/divi.png");
            if (lightUrl != null) {
                lightIcon = new ImageIcon(lightUrl);
                System.out.println("Light theme icon loaded successfully.");
            } else {
                System.err.println("Light theme icon not found: /images/divi.png");
            }

            // Load dark theme icon
            URL darkUrl = ImageResourceManager.class.getResource("/images/divi_dark.png");
            if (darkUrl != null) {
                darkIcon = new ImageIcon(darkUrl);
                System.out.println("Dark theme icon loaded successfully.");
            } else {
                System.err.println("Dark theme icon not found: /images/divi_dark.png");
            }

        } catch (Exception e) {
            System.err.println("Error occurred while loading icons: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the theme-based icon based on the isDark parameter.
     * If the theme-specific icon is not available, it returns a fallback icon.
     *
     * @param isDark true for dark theme, false for light theme
     * @return the ImageIcon corresponding to the theme
     */
    public static ImageIcon getThemeBasedIcon(boolean isDark) {
        String iconPath = isDark ? "/path/to/dark/logo.png" : "/path/to/light/logo.png";
        return new ImageIcon(ImageResourceManager.class.getResource(iconPath));
    }

    /**
     * Provides a default fallback icon if the theme-specific icons are unavailable.
     *
     * @return a fallback ImageIcon
     */
    private static ImageIcon getDefaultFallbackIcon() {
        try {
            URL fallbackUrl = ImageResourceManager.class.getResource("/images/default_fallback.png");
            if (fallbackUrl != null) {
                System.out.println("Fallback icon loaded successfully.");
                return new ImageIcon(fallbackUrl);
            } else {
                System.err.println("Fallback icon not found: /images/default_fallback.png");
            }
        } catch (Exception e) {
            System.err.println("Error occurred while loading fallback icon: " + e.getMessage());
            e.printStackTrace();
        }
        System.err.println("Returning an empty placeholder icon as fallback.");
        return new ImageIcon(new byte[0]); // Placeholder empty icon
    }

    /**
     * Clears all loaded icons. Used for debugging or reinitialization.
     */
    public static void clearCache() {
        lightIcon = null;
        darkIcon = null;
        System.out.println("Icon cache cleared.");
    }
}
