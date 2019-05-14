package com.lhd.earlybirdapi.subscription;

import com.lhd.earlybirdapi.githubrepo.GithubRepo;
import com.lhd.earlybirdapi.githubrepo.GithubRepoService;
import com.lhd.earlybirdapi.util.Mailer;
import java.time.Instant;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

  private SubscriptionRepository subscriptionRepository;
  private GithubRepoService githubRepoService;
  private Mailer mailer;

  public SubscriptionService(
      SubscriptionRepository subscriptionRepository,
      GithubRepoService githubRepoService,
      Mailer mailer) {
    this.subscriptionRepository = subscriptionRepository;
    this.githubRepoService = githubRepoService;
    this.mailer = mailer;
  }

  public void sendEmailsForSubscriptionsWithNewIssues() {
    for (Subscription subscription : subscriptionRepository.findAll()) {
      sendMessageForNewIssue(subscription);
    }
  }

  private void sendMessageForNewIssue(Subscription subscription) {
    Instant newLastCheckedTimestamp = Instant.now();
    if (subscription.newIssueExists()) {
      mailer.createAndSendMessage(subscription.getEmail(), "A new issue has been opened on a project you're "
          + "interested in. Find it here: " + subscription.getGithubRepo().getLatestRecordedIssueUrl());
    }
    updateLastCheckedTimestampAndSave(newLastCheckedTimestamp, subscription);
  }

  private void updateLastCheckedTimestampAndSave(Instant newLastCheckedTimestamp, Subscription subscription) {
    subscription.setLastCheckedTimestamp(newLastCheckedTimestamp);
    subscriptionRepository.save(subscription);
  }

  void saveSubscription(SubscriptionRequestDto subscriptionRequest) {
    GithubRepo githubRepo = githubRepoService.findGithubRepo(subscriptionRequest);
    Subscription subscription = Subscription.builder()
        .email(subscriptionRequest.getEmail())
        .githubRepo(githubRepo)
        .lastCheckedTimestamp(Instant.now())
        .build();
    subscriptionRepository.save(subscription);
  }

}
