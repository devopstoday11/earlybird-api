package com.lhd.broadcastapi.subscription;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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
