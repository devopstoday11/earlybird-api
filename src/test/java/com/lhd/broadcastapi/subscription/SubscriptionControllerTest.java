package com.lhd.broadcastapi.subscription;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionControllerTest {

  @Mock
  private SubscriptionRepository subscriptionRepositoryMock;

  @InjectMocks
  private SubscriptionController subscriptionControllerMock;


  @Test
  public void save() {
    Subscription expectedSubscription = Subscription.builder()
        .email("dummy@gmail.com")
        .repositoryUrl("dummy.github.com")
        .build();

    ResponseEntity<?> responseEntity = subscriptionControllerMock.save(expectedSubscription);

    verify(subscriptionRepositoryMock, times(1)).save(expectedSubscription);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
  }

}
