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
    SubscriptionRequestDto subscriptionRequestDto = new SubscriptionRequestDto();

    ResponseEntity<?> responseEntity = subscriptionControllerMock.save(subscriptionRequestDto);

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
  }

}
