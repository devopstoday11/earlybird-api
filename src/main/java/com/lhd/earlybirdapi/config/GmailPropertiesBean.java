package com.lhd.earlybirdapi.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GmailPropertiesBean {

  // TODO: create new gmail account named earlybirdnoreply
  @Value("${gmail.user}")
  private String gmailUser;

  @Value("${gmail.password}")
  private String gmailPassword;

  @Bean
  public Properties gmailProperties() {
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "465");
    props.put("gmail.user", gmailUser);
    props.put("gmail.password", gmailPassword);
    return props;
  }

}
