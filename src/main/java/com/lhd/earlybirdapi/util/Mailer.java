package com.lhd.earlybirdapi.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Mailer {

  @Value("${gmail.user}")
  private String gmailUser;

  @Value("${gmail.password}")
  private String gmailPassword;

  private Properties gmailProperties;

  Mailer(Properties gmailProperties) {
    this.gmailProperties = gmailProperties;
  }

  public void send(String to, String notification) {
    // TODO: create new gmail account named earlybirdnoreply
    Session session = Session.getDefaultInstance(
        gmailProperties,
        new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(gmailUser, gmailPassword);
          }
        }
    );

    try {
      Message message = new MimeMessage(session);
      message.setRecipients(Message.RecipientType.TO,
          InternetAddress.parse(to));
      message.setSubject("A New Issue is Open!");
      message.setText(notification);

      Transport.send(message);

      System.out.println("Email sent to: " + to);

    } catch (MessagingException e) {
      // TODO: more graceful error handling here?
      throw new RuntimeException(e);
    }
  }

}
