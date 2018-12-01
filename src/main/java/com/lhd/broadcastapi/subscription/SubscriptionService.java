package com.lhd.broadcastapi.subscription;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

  private SubscriptionRepository subscriptionRepository;
  private GithubRepoRepository githubRepoRepository;

  public SubscriptionService(SubscriptionRepository subscriptionRepository,
      GithubRepoRepository githubRepoRepository) {
    this.subscriptionRepository = subscriptionRepository;
    this.githubRepoRepository = githubRepoRepository;
  }

  void saveSubscription(SubscriptionRequestDto subscriptionRequest) {
    GithubRepo githubRepo = findGithubRepo(subscriptionRequest);

    Subscription subscription = Subscription.builder()
        .email(subscriptionRequest.getEmail())
        .githubRepo(githubRepo)
        .lastCheckedTimestamp(Instant.now())
        .build();
    subscriptionRepository.save(subscription);
  }

  private GithubRepo findGithubRepo(SubscriptionRequestDto subscriptionRequest) {
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


  private static final String URI = "repos/{githubRepoId}/issues";

  private String findLatestIssueTimestamp(String githubRepoId) {

    try {
      ProcessBuilder pb = new ProcessBuilder(
          "curl",
          "-X",
          "GET",
          "https://api.github.com/repos/" + githubRepoId + "/issues");

      Process p = pb.start();

      InputStream is = p.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      StringBuilder responseStrBuilder = new StringBuilder();
      String line;

      while ((line = br.readLine()) != null) {
        System.out.println("read line from curl command: " + line);
        responseStrBuilder.append(line);
      }
      String s = responseStrBuilder.toString();

      IssueDto[] issues = new Gson().fromJson(s, IssueDto[].class);

      if (issues.length > 0) {
        return issues[0].getCreated_at();
      }

    } catch (IOException e) {
      // swallowing, sorry friend
    }

    return Instant.now().toString();
  }

}
