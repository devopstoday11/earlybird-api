package com.lhd.earlybirdapi.util;

import com.lhd.earlybirdapi.githubrepo.GithubRepoService;
import com.lhd.earlybirdapi.subscription.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
class ScheduledEmailer {

  private static final int TEN_SECONDS = 10000;
  private static final int ONE_MINUTE = 60000;

  private GithubRepoService githubRepoService;
  private SubscriptionService subscriptionService;

  private ScheduledEmailer(
      GithubRepoService githubRepoService,
      SubscriptionService subscriptionService) {
    this.githubRepoService = githubRepoService;
    this.subscriptionService = subscriptionService;
  }

  @Scheduled(fixedDelay = TEN_SECONDS)
  void sendEmailNotificationsForNewIssues() {
    githubRepoService.updateAllGithubReposLatestIssueTimestampsAndUrls();
    subscriptionService.sendEmailsForSubscriptionsWithNewIssues();
  }

}
