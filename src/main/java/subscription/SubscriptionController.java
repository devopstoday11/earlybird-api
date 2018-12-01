package subscription;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
class SubscriptionController {

  private SubscriptionRepository subscriptionRepository;

  SubscriptionController(SubscriptionRepository subscriptionRepository) {
    this.subscriptionRepository = subscriptionRepository;
  }

  @PutMapping(name = "/save", consumes = APPLICATION_JSON_VALUE)
  ResponseEntity<?> save(@RequestBody Subscription subscription) {
    subscriptionRepository.save(subscription);
    return null;
  }

}
