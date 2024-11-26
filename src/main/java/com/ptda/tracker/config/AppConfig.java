package com.ptda.tracker.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.ptda.tracker")
public class AppConfig {
    public final static String APP_NAME = "Swing App";
    public final static String COMPANY_NAME = "Some Company";
    public final static String COPYRIGHT_DETAILS = "© 2022 Some Company";
    public final static String HOME_URL = "https://github.com/MetalAZ/swing-app";
    public final static String DEFAULT_DARK_THEME = "Dark";
    public final static String DEFAULT_LIGHT_THEME = "Light";
}
