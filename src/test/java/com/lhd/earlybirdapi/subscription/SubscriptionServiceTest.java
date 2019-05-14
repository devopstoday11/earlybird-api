package com.lhd.earlybirdapi.subscription;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lhd.earlybirdapi.githubrepo.GithubRepo;
import com.lhd.earlybirdapi.githubrepo.GithubRepoService;
import com.lhd.earlybirdapi.githubrepo.IssueDto;
import com.lhd.earlybirdapi.util.Mailer;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceTest {

  private Instant currentTime = Instant.now();
  private Instant currentTimePlus5Seconds = currentTime.plusSeconds(5L);
  private Instant currentTimeMinus5Seconds = currentTime.minusSeconds(5L);

  @Mock
  private Mailer mailerMock;

  @Captor
  ArgumentCaptor<Subscription> savedSubscriptionsCaptor;

  @Mock
  private SubscriptionRepository subscriptionRepositoryMock;

  @Mock
  private GithubRepoService githubRepoServiceMock;

  @InjectMocks
  private SubscriptionService subscriptionService;


  @Test
  public void givenNewIssue_whenSendEmailsForSubscriptionsWithNewIssues_thenSendEmail() {
    IssueDto issueDto = new IssueDto();
    issueDto.setCreatedAt(currentTimePlus5Seconds);
    issueDto.setHtmlUrl("http://github.com/user/repo/issue");
    GithubRepo githubRepo = GithubRepo.builder()
        .id("genericRepoId")
        .latestRecordedIssueTimestamp(issueDto.getCreatedAt())
        .build();
    githubRepo.setLatestRecordedIssueUrl(issueDto.getHtmlUrl());
    Subscription subscription = Subscription.builder()
        .email("generic@email.com")
        .githubRepo(githubRepo)
        .lastCheckedTimestamp(currentTime)
        .build();
    when(subscriptionRepositoryMock.findAll()).thenReturn(Collections.singletonList(subscription));

    subscriptionService.sendEmailsForSubscriptionsWithNewIssues();

    verify(subscriptionRepositoryMock, times(1)).findAll();
    verify(mailerMock, times(1)).createAndSendMessage(subscription.getEmail(),
        "A new issue has been opened on a project you're interested in. "
            + "Find it here: " + githubRepo.getLatestRecordedIssueUrl());
  }


  @Test
  public void givenNoNewIssues_whenSendEmailsForSubscriptionsWithNewIssues_then() {
    IssueDto issueDto = new IssueDto();
    issueDto.setCreatedAt(currentTimeMinus5Seconds);
    issueDto.setHtmlUrl("http://github.com/user/repo/issue");
    GithubRepo githubRepo = GithubRepo.builder()
        .id("genericRepoId")
        .latestRecordedIssueTimestamp(issueDto.getCreatedAt())
        .build();
    githubRepo.setLatestRecordedIssueUrl(issueDto.getHtmlUrl());
    Subscription subscription = Subscription.builder()
        .email("generic@email.com")
        .githubRepo(githubRepo)
        .lastCheckedTimestamp(currentTime)
        .build();
    when(subscriptionRepositoryMock.findAll()).thenReturn(Collections.singletonList(subscription));

    subscriptionService.sendEmailsForSubscriptionsWithNewIssues();

    verify(subscriptionRepositoryMock, times(1)).save(savedSubscriptionsCaptor.capture());
    List<Subscription> savedSubscriptions = savedSubscriptionsCaptor.getAllValues();
    assertTrue(savedSubscriptions.get(0).getLastCheckedTimestamp().isAfter(currentTime));
  }


  @Test
  public void givenSubscriptionRequest_whenSaveSubscription_thenFindRepoAndSaveSubscription() {
    SubscriptionRequestDto subscriptionRequestDto = new SubscriptionRequestDto(
        "someone@email.com",
        "some-owner",
        "some-name");
    GithubRepo githubRepo = GithubRepo.builder()
        .id("genericRepoId")
        .latestRecordedIssueTimestamp(currentTime)
        .build();
    when(githubRepoServiceMock.findGithubRepo(subscriptionRequestDto)).thenReturn(githubRepo);

    subscriptionService.saveSubscription(subscriptionRequestDto);

    verify(githubRepoServiceMock, times(1)).findGithubRepo(subscriptionRequestDto);
    verify(subscriptionRepositoryMock, times(1)).save(any(Subscription.class));
  }

}
