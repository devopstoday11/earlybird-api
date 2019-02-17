package com.lhd.earlybirdapi.subscription;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lhd.earlybirdapi.githubrepo.GithubRepo;
import com.lhd.earlybirdapi.githubrepo.GithubRepoRepository;
import com.lhd.earlybirdapi.githubrepo.GithubRepoService;
import com.lhd.earlybirdapi.githubrepo.IssueDto;
import com.lhd.earlybirdapi.util.Mailer;
import com.lhd.earlybirdapi.util.ScheduledEmailer;
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
public class ScheduledEmailerTest {

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

  @Mock
  private GithubRepoService githubRepoServiceMock;

  @Captor
  ArgumentCaptor<List<GithubRepo>> savedGithubReposCaptor;

  @Mock
  private GithubRepoRepository githubRepoRepositoryMock;

  @Captor
  ArgumentCaptor<Subscription> savedSubscriptionsCaptor;

  @Mock
  private SubscriptionRepository subscriptionRepositoryMock;

  @Mock
  private SubscriptionService subscriptionServiceMock;

  @InjectMocks
  private ScheduledEmailer scheduledEmailerMock;


  @Test
  public void sendEmailNotificationsForNewIssues() {
    createTestDoublesAndStubMockedServices();

    scheduledEmailerMock.sendEmailNotificationsForNewIssues();

    assertGithubReposUpdatedWithLatestIssueUrlAndTimestampAndVerifySaved();
    verifyEmailSentOnlyForSubscription1();
    assertSubscriptionsUpdatedAndVerifySaved();
  }

  private void createTestDoublesAndStubMockedServices() {
    createIssueDtos();
    createGithubRepos();
    createSubscriptions();
    stubGithubRepoRepositoryMock();
    stubGithubRepoServiceMock();
    stubSubscriptionRepositoryMock();
  }

  private void createIssueDtos() {
    issueDto1 = new IssueDto();
    issueDto1.setCreatedAt(currentTimePlus5Seconds.toString());
    issueDto1.setHtmlUrl("http://github.com/user/repo/issue1");
    issueDto2 = new IssueDto();
    issueDto2.setCreatedAt(currentTimeMinus5Seconds.toString());
    issueDto2.setHtmlUrl("http://github.com/user/repo/issue2");
  }

  private void createGithubRepos() {
    githubRepo1 = GithubRepo.builder()
        .id("genericRepoId1")
        .latestRecordedIssueTimestamp(currentTime)
        .build();
    githubRepo2 = GithubRepo.builder()
        .id("genericRepoId2")
        .latestRecordedIssueTimestamp(currentTime)
        .build();
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

  private void stubGithubRepoRepositoryMock() {
    when(githubRepoRepositoryMock.findAll()).thenReturn(new ArrayList<>(asList(githubRepo1, githubRepo2)));
  }

  private void stubGithubRepoServiceMock() {
    when(githubRepoServiceMock.findLatestIssue("genericRepoId1")).thenReturn(issueDto1);
    when(githubRepoServiceMock.findLatestIssue("genericRepoId2")).thenReturn(issueDto2);
  }

  private void stubSubscriptionRepositoryMock() {
    when(subscriptionRepositoryMock.findAll()).thenReturn(asList(subscription1, subscription2));
  }

  private void assertGithubReposUpdatedWithLatestIssueUrlAndTimestampAndVerifySaved() {
    InOrder inOrder = inOrder(githubRepoRepositoryMock);
    inOrder.verify(githubRepoRepositoryMock, times(1)).findAll();
    inOrder.verify(githubRepoRepositoryMock, times(1)).saveAll(savedGithubReposCaptor.capture());
    List<GithubRepo> savedGithubRepos = savedGithubReposCaptor.getValue();
    assertEquals("genericRepoId1", savedGithubRepos.get(0).getId());
    assertEquals(currentTimePlus5Seconds, savedGithubRepos.get(0).getLatestRecordedIssueTimestamp());
    assertEquals("http://github.com/user/repo/issue1", savedGithubRepos.get(0).getLatestRecordedIssueUrl());
    assertEquals("genericRepoId2", savedGithubRepos.get(1).getId());
    assertEquals(currentTimeMinus5Seconds, savedGithubRepos.get(1).getLatestRecordedIssueTimestamp());
    assertEquals("http://github.com/user/repo/issue2", savedGithubRepos.get(1).getLatestRecordedIssueUrl());
    verify(githubRepoServiceMock, times(1)).findLatestIssue("genericRepoId1");
    verify(githubRepoServiceMock, times(1)).findLatestIssue("genericRepoId2");
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
