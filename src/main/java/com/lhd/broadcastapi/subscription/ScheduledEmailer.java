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
  private SubscriptionRepository subscriptionRepository;
  private SubscriptionService subscriptionService;
  private GithubRepoRepository githubRepoRepository;

  ScheduledEmailer(SubscriptionService subscriptionService,
      SubscriptionRepository subscriptionRepository,
      GithubRepoRepository githubRepoRepository) {
    this.subscriptionService = subscriptionService;
    this.subscriptionRepository = subscriptionRepository;
    this.githubRepoRepository = githubRepoRepository;
  }

  @Scheduled(fixedDelay = TEN_SECONDS)
  public void sendEmailNotifications() {
    updateGithubRepoLatestIssueTimestampsAndUrls();

    Instant newLastCheckedTimestamp = Instant.now();
    for (Subscription subscription : subscriptionRepository.findAll()) {
      sendEmailIfNewIssueExists(subscription);
      subscription.setLastCheckedTimestamp(newLastCheckedTimestamp);
      subscriptionRepository.save(subscription);
    }
  }

  private void updateGithubRepoLatestIssueTimestampsAndUrls() {
    List<GithubRepo> githubRepos = githubRepoRepository.findAll();
    for (GithubRepo githubRepo : githubRepos) {
      IssueDto latestIssue = subscriptionService.findLatestIssue(githubRepo.getId());
      githubRepo.setLatestIssueTimestamp(Instant.parse(latestIssue.getCreated_at()));
      githubRepo.setLatestIssueUrl(latestIssue.getHtml_url());
    }
    githubRepoRepository.saveAll(githubRepos);
  }

  private void sendEmailIfNewIssueExists(Subscription subscription) {
    Instant lastCheckedTimestamp = subscription.getLastCheckedTimestamp();
    GithubRepo githubRepo = subscription.getGithubRepo();
    Instant latestIssueTimestamp = githubRepo.getLatestIssueTimestamp();
    if (latestIssueTimestamp.isAfter(lastCheckedTimestamp)) {
      mailer.send(subscription.getEmail(), "A new issue has been opened on a project you're "
          + "interested in. Find it here: " + githubRepo.getLatestIssueUrl());
    } else {
      System.out.println("No more recent issues found for: " + githubRepo.getId());
    }
  }

}
