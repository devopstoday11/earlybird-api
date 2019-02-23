package com.lhd.earlybirdapi.util;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.lhd.earlybirdapi.githubrepo.GithubRepoService;
import com.lhd.earlybirdapi.subscription.SubscriptionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledEmailerTest {

  @Mock
  private GithubRepoService githubRepoServiceMock;

  @Mock
  private SubscriptionService subscriptionServiceMock;

  @InjectMocks
  private ScheduledEmailer scheduledEmailerMock;

  @Test
  public void sendEmailNotificationsForNewIssues() {
    scheduledEmailerMock.sendEmailNotificationsForNewIssues();

    verify(githubRepoServiceMock, times(1)).updateAllGithubReposLatestIssueTimestampsAndUrls();
    verify(subscriptionServiceMock, times(1)).sendEmailsForSubscriptionsWithNewIssues();
  }

}
