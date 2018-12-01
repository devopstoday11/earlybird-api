package subscription;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionRepository subscriptionRepositoryMock;

    @InjectMocks
    private SubscriptionController subscriptionControllerMock;


    @Test
    public void save() {
        // given
        Subscription subscription = Subscription.builder()


        // when


        // then

    }

}
