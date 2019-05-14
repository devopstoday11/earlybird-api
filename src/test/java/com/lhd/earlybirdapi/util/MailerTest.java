package com.lhd.earlybirdapi.util;

import java.util.Properties;
import javax.mail.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MailerTest {

  private Mailer mailer = new Mailer(Session.getDefaultInstance(new Properties()));

  @Test(expected = RuntimeException.class)
  public void createAndSendMessage() {
    mailer.createAndSendMessage("tester@email.com", "issue notification");
  }

}
