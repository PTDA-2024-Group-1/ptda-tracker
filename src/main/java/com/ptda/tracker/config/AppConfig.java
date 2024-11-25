package com.ptda.tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.ptda.tracker")
public class AppConfig {
    public final static String APP_NAME = "Swing App";
    public final static String COMPANY_NAME = "Some Company";
    public final static String COPYRIGHT_DETAILS = "© 2022 Some Company";
    public final static String HOME_URL = "https://github.com/MetalAZ/swing-app";
    public final static String DEFAULT_DARK_THEME = "Dark";
    public final static String DEFAULT_LIGHT_THEME = "Light";

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("address@gmail.com");
        mailSender.setPassword("password");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
