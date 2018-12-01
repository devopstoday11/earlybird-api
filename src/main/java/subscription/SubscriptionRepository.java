package subscription;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SubscriptionRepository extends CrudRepository<Long, Subscription> {

}
