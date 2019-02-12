package com.lhd.earlybirdapi.util;

import javax.mail.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MailerTest {

  @Mock
  Session mailSessionMock;

  @InjectMocks
  private Mailer mailer;

  @Test
  public void createAndSendMessage() {
    // given

    // when

    // then
  }

}
