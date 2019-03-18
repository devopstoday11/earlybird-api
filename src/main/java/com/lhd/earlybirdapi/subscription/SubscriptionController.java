package com.lhd.earlybirdapi.subscription;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SubscriptionController {

  private SubscriptionService subscriptionService;

  SubscriptionController(SubscriptionService subscriptionService) {
    this.subscriptionService = subscriptionService;
  }

  // TODO: revisit cross origin. do we need this for deploying with AWS?
  @CrossOrigin(origins = "http://localhost:3000")
  @PutMapping(path = "/create-subscription", consumes = APPLICATION_JSON_VALUE)
  ResponseEntity<?> save(@RequestBody SubscriptionRequestDto subscriptionRequest) {
    subscriptionService.saveSubscription(subscriptionRequest);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

}
