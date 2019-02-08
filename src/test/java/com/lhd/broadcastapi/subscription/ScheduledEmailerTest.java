package com.lhd.broadcastapi.subscription;

import static org.mockito.Mockito.when;

import com.lhd.broadcastapi.util.Mailer;
import java.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledEmailerTest {

  private Instant generatedInstant = Instant.now();

  @Mock
  private GithubRepo githubRepoMock;

  @Mock
  private Subscription subscriptionMock;

  @Mock
  private Mailer mailerMock;

  @Mock
  private GithubRepoService githubRepoServiceMock;

  @Mock
  private GithubRepoRepository githubRepoRepositoryMock;

  @Mock
  private SubscriptionRepository subscriptionRepositoryMock;

  @InjectMocks
  private ScheduledEmailer scheduledEmailerMock;


  @Test
  private void sendEmailNotificationsForNewIssues() {
    stubGithubRepoMock();
    stubSubscriptionMock();

    // first thing to test
//    private void updateAllGithubReposLatestIssueTimestampsAndUrls() {
//      List<GithubRepo> githubRepos = githubRepoRepository.findAll();
//      for (GithubRepo githubRepo : githubRepos) {
//        IssueDto latestIssue = githubRepoService.findLatestIssue(githubRepo.getId());
//        githubRepo.setLatestRecordedIssueTimestamp(Instant.parse(latestIssue.getCreated_at()));
//        githubRepo.setLatestRecordedIssueUrl(latestIssue.getHtml_url());
//      }
//      githubRepoRepository.saveAll(githubRepos);
//    }
  }

  private void stubGithubRepoMock() {
    when(githubRepoMock.getId()).thenReturn("genericRepoId");
    when(githubRepoMock.getLatestRecordedIssueTimestamp()).thenReturn(generatedInstant);
  }

  private void stubSubscriptionMock() {
    when(subscriptionMock.getId()).thenReturn(1L);
    when(subscriptionMock.getEmail()).thenReturn("generic@email.com");
    when(subscriptionMock.getGithubRepo()).thenReturn(githubRepoMock);
  }


}
