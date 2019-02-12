package com.lhd.earlybirdapi.config;

import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailSessionConfig {

  private Properties gmailProperties;

  public MailSessionConfig(Properties gmailProperties) {
    this.gmailProperties = gmailProperties;
  }

  @Bean
  public Session mailSession() {
    return Session.getDefaultInstance(
        gmailProperties,
        new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(
                gmailProperties.getProperty("gmail.user"),
                gmailProperties.getProperty("gmail.password"));
          }
        }
    );
  }

}
