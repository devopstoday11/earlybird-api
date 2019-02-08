package com.lhd.broadcastapi.subscription;

import com.lhd.broadcastapi.util.Mailer;
import java.time.Instant;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledEmailer {

  private static final int TEN_SECONDS = 10000;
  private static final int ONE_MINUTE = 60000;

  private Mailer mailer = new Mailer();
  private GithubRepoService githubRepoService;
  private GithubRepoRepository githubRepoRepository;
  private SubscriptionRepository subscriptionRepository;

  private ScheduledEmailer(
      GithubRepoService githubRepoService,
      GithubRepoRepository githubRepoRepository,
      SubscriptionRepository subscriptionRepository) {
    this.githubRepoService = githubRepoService;
    this.githubRepoRepository = githubRepoRepository;
    this.subscriptionRepository = subscriptionRepository;
  }

  @Scheduled(fixedDelay = TEN_SECONDS)
  public void sendEmailNotificationsForNewIssues() {
    updateAllGithubReposLatestIssueTimestampsAndUrls();
    Instant newLastCheckedTimestamp = Instant.now();
    for (Subscription subscription : subscriptionRepository.findAll()) {
      sendEmailIfNewIssueExists(subscription);
      updateLastCheckedTimestampAndSave(newLastCheckedTimestamp, subscription);
    }
  }

  private void updateAllGithubReposLatestIssueTimestampsAndUrls() {
    List<GithubRepo> githubRepos = githubRepoRepository.findAll();
    for (GithubRepo githubRepo : githubRepos) {
      IssueDto latestIssue = githubRepoService.findLatestIssue(githubRepo.getId());
      githubRepo.setLatestRecordedIssueTimestamp(Instant.parse(latestIssue.getCreated_at()));
      githubRepo.setLatestRecordedIssueUrl(latestIssue.getHtml_url());
    }
    githubRepoRepository.saveAll(githubRepos);
  }

  private void sendEmailIfNewIssueExists(Subscription subscription) {
    Instant lastCheckedTimestamp = subscription.getLastCheckedTimestamp();
    GithubRepo githubRepo = subscription.getGithubRepo();
    Instant latestIssueTimestamp = githubRepo.getLatestRecordedIssueTimestamp();
    if (latestIssueTimestamp.isAfter(lastCheckedTimestamp)) {
      mailer.send(subscription.getEmail(), "A new issue has been opened on a project you're "
          + "interested in. Find it here: " + githubRepo.getLatestRecordedIssueUrl());
    }
  }

  private void updateLastCheckedTimestampAndSave(Instant newLastCheckedTimestamp, Subscription subscription) {
    subscription.setLastCheckedTimestamp(newLastCheckedTimestamp);
    subscriptionRepository.save(subscription);
  }

}
