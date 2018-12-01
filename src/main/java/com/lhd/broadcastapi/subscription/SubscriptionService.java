package com.lhd.broadcastapi.subscription;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SubscriptionService {

  private RestTemplate restTemplate;
  private SubscriptionRepository subscriptionRepository;
  private GithubRepoRepository githubRepoRepository;

  public SubscriptionService(RestTemplate restTemplate,
      SubscriptionRepository subscriptionRepository,
      GithubRepoRepository githubRepoRepository) {
    this.restTemplate = restTemplate;
    this.subscriptionRepository = subscriptionRepository;
    this.githubRepoRepository = githubRepoRepository;
  }

  void saveSubscription(SubscriptionRequest subscriptionRequest) {
    GithubRepo githubRepo = findGithubRepo(subscriptionRequest);

    Subscription subscription = Subscription.builder()
        .email(subscriptionRequest.getEmail())
        .githubRepo(githubRepo)
        .lastCheckedTimestamp(Instant.now())
        .build();
    subscriptionRepository.save(subscription);
  }

  private GithubRepo findGithubRepo(SubscriptionRequest subscriptionRequest) {
    String githubRepoId = subscriptionRequest.getOwner() + "/" + subscriptionRequest.getRepoName();
    Optional<GithubRepo> optionalRepo = githubRepoRepository.findById(githubRepoId);
    GithubRepo githubRepo;

    if (optionalRepo.isPresent()) {
      githubRepo = optionalRepo.get();
    } else {
      githubRepo = GithubRepo.builder()
          .id(githubRepoId)
          .latestIssueTimestamp(findLatestIssueTimestamp(githubRepoId))
          .build();
      githubRepoRepository.save(githubRepo);
    }

    return githubRepo;
  }

  // TODO: read latest issue timestamp from GithubAPI
  private Instant findLatestIssueTimestamp(String githubRepoId) {
    return Instant.now();
  }

}
