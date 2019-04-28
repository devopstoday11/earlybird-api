package com.lhd.earlybirdapi.githubrepo;

import com.lhd.earlybirdapi.subscription.SubscriptionRequestDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubRepoService {

  @Value("${oauth.token}")
  private String authToken;

  private GithubRepoRepository githubRepoRepository;

  private RestTemplate restTemplate;

  public GithubRepoService(GithubRepoRepository githubRepoRepository, RestTemplate restTemplate) {
    this.githubRepoRepository = githubRepoRepository;
    this.restTemplate = restTemplate;
  }

  public void updateAllGithubReposLatestIssueTimestampsAndUrls() {
    List<GithubRepo> githubRepos = githubRepoRepository.findAll();
    for (GithubRepo githubRepo : githubRepos) {
      updateWithLatestIssue(githubRepo);
    }
    githubRepoRepository.saveAll(githubRepos);
  }

  private void updateWithLatestIssue(GithubRepo githubRepo) {
    Optional<IssueDto> optionalIssueDto = findLatestIssue(githubRepo.getId());
    if (optionalIssueDto.isPresent()) {
      IssueDto latestIssue = optionalIssueDto.get();
      if (latestIssue.getCreatedAt().isAfter(githubRepo.getLatestRecordedIssueTimestamp())) {
        githubRepo.setLatestRecordedIssueTimestamp(latestIssue.getCreatedAt());
        githubRepo.setLatestRecordedIssueUrl(latestIssue.getHtmlUrl());
      }
    }
  }

  private Optional<IssueDto> findLatestIssue(String githubRepoId) {
    IssueDto[] issues = postForGithubRepoIssues(githubRepoId);
    if (issues.length == 0) {
      return Optional.empty();
    }
    return Optional.of(issues[0]);
  }

  private IssueDto[] postForGithubRepoIssues(String githubRepoId) {
    String url = "/repos/" + githubRepoId + "/issues";
    IssueDto[] issues;

    try {
      issues = restTemplate
          .getForEntity(url, IssueDto[].class)
          .getBody();
    } catch (RestClientException e) {
      throw new GithubApiRequestException(e.getMessage());
    }

    return issues;
  }

  public GithubRepo findGithubRepo(SubscriptionRequestDto subscriptionRequest) {
    String githubRepoId = subscriptionRequest.getRepoOwner() + "/" + subscriptionRequest.getRepoName();
    Optional<GithubRepo> optionalRepo = githubRepoRepository.findById(githubRepoId);
    GithubRepo githubRepo;

    // existence here does NOT mean existence of the repository in Github
    if (optionalRepo.isPresent()) {
      githubRepo = optionalRepo.get();
    } else {
      githubRepo = createAndSaveNewGithubRepo(githubRepoId);
    }

    return githubRepo;
  }

  private GithubRepo createAndSaveNewGithubRepo(String githubRepoId) {
    Instant latestIssueCreatedAtTimestampInstant = findLatestIssueCreatedAtTimestamp(githubRepoId);
    GithubRepo githubRepo = GithubRepo.builder()
        .id(githubRepoId)
        .latestRecordedIssueTimestamp(latestIssueCreatedAtTimestampInstant)
        .build();
    githubRepoRepository.save(githubRepo);
    return githubRepo;
  }

  private Instant findLatestIssueCreatedAtTimestamp(String githubRepoId) {
    Optional<IssueDto> optionalIssueDto = findLatestIssue(githubRepoId);
    if (optionalIssueDto.isPresent()) {
      IssueDto latestIssueDto = optionalIssueDto.get();
      return latestIssueDto.getCreatedAt();
    } else {
      return Instant.EPOCH;
    }
  }

}
