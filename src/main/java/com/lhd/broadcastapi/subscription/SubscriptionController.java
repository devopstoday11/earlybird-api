package com.lhd.broadcastapi.subscription;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.lhd.broadcastapi.util.Mailer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SubscriptionController {

  private Mailer mailer = new Mailer();
  private SubscriptionService subscriptionService;

  SubscriptionController(SubscriptionService subscriptionService) {
    this.subscriptionService = subscriptionService;
  }

  @PutMapping(path = "/create-subscription", consumes = APPLICATION_JSON_VALUE)
  ResponseEntity<?> save(@RequestBody SubscriptionRequestDto subscriptionRequest) {
    subscriptionService.saveSubscription(subscriptionRequest);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/send-email")
  void sendEmail() {
    mailer.send("mitchell.k.irvin@gmail.com", "notification message thingy");
  }

}
