package com.lhd.earlybirdapi.subscription;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

  private SubscriptionRepository subscriptionRepository;
  private GithubRepoService githubRepoService;

  public SubscriptionService(
      SubscriptionRepository subscriptionRepository,
      GithubRepoService githubRepoService) {
    this.subscriptionRepository = subscriptionRepository;
    this.githubRepoService = githubRepoService;
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
