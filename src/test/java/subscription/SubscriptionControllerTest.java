package subscription;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    Subscription expectedSubscription = Subscription.builder()
        .email("dummy@gmail.com")
        .repositoryUrl("dummy.github.com")
        .build();

    // when
    subscriptionControllerMock.save(expectedSubscription);

    // then
    verify(subscriptionRepositoryMock, times(1)).save(expectedSubscription);
  }

}
