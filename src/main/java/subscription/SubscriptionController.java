package subscription;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
public class SubscriptionController {

    @PutMapping(name = "/save", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody Subscription subscription) {
        return null;
    }

}
