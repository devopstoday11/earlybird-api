package com.lhd.earlybirdapi.subscription;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lhd.earlybirdapi.githubrepo.GithubRepo;
import com.lhd.earlybirdapi.githubrepo.GithubRepoRepository;
import com.lhd.earlybirdapi.githubrepo.GithubRepoService;
import com.lhd.earlybirdapi.githubrepo.IssueDto;
import com.lhd.earlybirdapi.util.Mailer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceTest {

  private Instant currentTime = Instant.now();
  private Instant currentTimePlus5Seconds = currentTime.plusSeconds(5L);
  private Instant currentTimeMinus5Seconds = currentTime.minusSeconds(5L);
  private IssueDto issueDto1;
  private IssueDto issueDto2;
  private GithubRepo githubRepo1;
  private GithubRepo githubRepo2;
  private Subscription subscription1;
  private Subscription subscription2;

  @Mock
  private Mailer mailerMock;

  @Captor
  ArgumentCaptor<Subscription> savedSubscriptionsCaptor;

  @Mock
  private SubscriptionRepository subscriptionRepositoryMock;

  @InjectMocks
  private SubscriptionService subscriptionService;


  @Test
  public void sendEmailNotificationsForNewIssues() {
    createTestDoublesAndStubMockedServices();

    subscriptionService.sendEmailsForSubscriptionsWithNewIssues();

    verifyEmailSentOnlyForSubscription1();
    assertSubscriptionsUpdatedAndVerifySaved();
  }

  private void createTestDoublesAndStubMockedServices() {
    createIssueDtos();
    createGithubRepos();
    createSubscriptions();
    stubSubscriptionRepositoryMock();
  }

  private void createIssueDtos() {
    issueDto1 = new IssueDto();
    issueDto1.setCreatedAt(currentTimePlus5Seconds);
    issueDto1.setHtmlUrl("http://github.com/user/repo/issue1");
    issueDto2 = new IssueDto();
    issueDto2.setCreatedAt(currentTimeMinus5Seconds);
    issueDto2.setHtmlUrl("http://github.com/user/repo/issue2");
  }

  private void createGithubRepos() {
    githubRepo1 = GithubRepo.builder()
        .id("genericRepoId1")
        .latestRecordedIssueTimestamp(issueDto1.getCreatedAt())
        .build();
    githubRepo1.setLatestRecordedIssueUrl("http://github.com/user/repo/issue1");
    githubRepo2 = GithubRepo.builder()
        .id("genericRepoId2")
        .latestRecordedIssueTimestamp(issueDto2.getCreatedAt())
        .build();
    githubRepo2.setLatestRecordedIssueUrl("http://github.com/user/repo/issue2");
  }

  private void createSubscriptions() {
    subscription1 = Subscription.builder()
        .email("generic1@email.com")
        .githubRepo(githubRepo1)
        .lastCheckedTimestamp(currentTime)
        .build();
    subscription2 = Subscription.builder()
        .email("generic2@email.com")
        .githubRepo(githubRepo2)
        .lastCheckedTimestamp(currentTime)
        .build();
  }

  private void stubSubscriptionRepositoryMock() {
    when(subscriptionRepositoryMock.findAll()).thenReturn(asList(subscription1, subscription2));
  }

  private void verifyEmailSentOnlyForSubscription1() {
    verify(subscriptionRepositoryMock, times(1)).findAll();
    verify(mailerMock, times(1)).createAndSendMessage("generic1@email.com",
        "A new issue has been opened on a project you're interested in. "
            + "Find it here: http://github.com/user/repo/issue1");
  }

  private void assertSubscriptionsUpdatedAndVerifySaved() {
    verify(subscriptionRepositoryMock, times(2)).save(savedSubscriptionsCaptor.capture());
    List<Subscription> savedSubscriptions = savedSubscriptionsCaptor.getAllValues();
    assertTrue(savedSubscriptions.get(0).getLastCheckedTimestamp().isAfter(currentTime));
    assertTrue(savedSubscriptions.get(1).getLastCheckedTimestamp().isAfter(currentTime));
  }

}
