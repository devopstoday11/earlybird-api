package com.lhd.earlybirdapi.githubrepo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhd.earlybirdapi.subscription.SubscriptionRequestDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
      githubRepo.setLatestRecordedIssueTimestamp(latestIssue.getCreatedAt());
      githubRepo.setLatestRecordedIssueUrl(latestIssue.getHtmlUrl());
    }
  }

  public GithubRepo findGithubRepo(SubscriptionRequestDto subscriptionRequest) {
    String githubRepoId = subscriptionRequest.getRepoOwner() + "/" + subscriptionRequest.getRepoName();

    Optional<GithubRepo> optionalRepo = githubRepoRepository.findById(githubRepoId);
    GithubRepo githubRepo;

    // existence here does NOT mean existence of the repository in Github
    if (optionalRepo.isPresent()) {
      githubRepo = optionalRepo.get();
    } else {
      if(githubRepoExistsInApi(githubRepoId)) {
        githubRepo = createAndSaveNewGithubRepo(githubRepoId);
      } else {
        githubRepo = null;
      }
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

  private Optional<IssueDto> findLatestIssue(String githubRepoId) {
    IssueDto[] issues = postForGithubRepoIssues(githubRepoId);
    return Optional.of(issues[0]);
  }

  private IssueDto[] postForGithubRepoIssues(String githubRepoId) {
    String URL = "/repos/" + githubRepoId + "/issues";
    IssueDto[] issues;

    // TODO: each of the methods that could throw IOExceptions should handle it themselves
    // we could remove the try/catch here in that case, and create more specific custom exceptions
    try {
      ResponseEntity<String> responseEntity = restTemplate.getForEntity(URL, String.class);
      issues = new ObjectMapper().readValue(responseEntity.getBody(), IssueDto[].class);
    } catch (IOException | RestClientException e) {
      throw new GithubApiRequestException(e.getMessage());
    }

    return issues;
  }
  private boolean githubRepoExistsInApi(String githubRepoId) {
    String URL = "/repos/" + githubRepoId;
    ResponseEntity<String> responseEntity;
    try {
      responseEntity = restTemplate.getForEntity(URL, String.class);
    } catch (HttpClientErrorException e) {
      return false;
    }
    return responseEntity.getStatusCode().is2xxSuccessful();
  }

}
