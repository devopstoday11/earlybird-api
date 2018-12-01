package com.lhd.broadcastapi.subscription;

import com.lhd.broadcastapi.util.Mailer;
import org.springframework.scheduling.annotation.Scheduled;

public class ScheduledEmailer {

  Mailer mailer = new Mailer();

  @Scheduled(fixedDelay = 1000)
  public void sendEmailNotifications() {
    mailer.send();
  }

}
