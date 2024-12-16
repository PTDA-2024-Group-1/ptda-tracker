package com.ptda.tracker.util;

import javax.swing.*;
import java.net.URL;

public class ImageResourceManager {
    private static ImageIcon lightIcon;
    private static ImageIcon darkIcon;

    static {
        try {
            URL lightUrl = ImageResourceManager.class.getResource("/images/divi.png");
            if (lightUrl != null) {
                lightIcon = new ImageIcon(lightUrl);
            }

            URL darkUrl = ImageResourceManager.class.getResource("/images/divi_dark.png");
            if (darkUrl != null) {
                darkIcon = new ImageIcon(darkUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ImageIcon getThemeBasedIcon(boolean isDark) {
        if (isDark) {
            if (darkIcon != null) {
                return darkIcon;
            }
        }
        return lightIcon != null ? lightIcon : getDefaultFallbackIcon();
    }

    private static ImageIcon getDefaultFallbackIcon() {
        try {
            URL fallbackUrl = ImageResourceManager.class.getResource("/images/default_fallback.png");
            if (fallbackUrl != null) {
                return new ImageIcon(fallbackUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
