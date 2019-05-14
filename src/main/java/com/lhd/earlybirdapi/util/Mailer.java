package com.lhd.earlybirdapi.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class Mailer {

  private Session mailSession;

  Mailer(Session mailSession) {
    this.mailSession = mailSession;
  }

  public void createAndSendMessage(String to, String notification) {
    try {
      Message message = createMessage(to, notification);
      Transport.send(message);
    } catch (MessagingException e) {
      // TODO: more graceful error handling here?
      throw new RuntimeException(e);
    }
  }

  private Message createMessage(String to, String notification) throws MessagingException {
    Message message = new MimeMessage(mailSession);
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    message.setSubject("A New Issue is Open!");
    message.setText(notification);
    return message;
  }

}
