package com.lhd.earlybirdapi.githubrepo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhd.earlybirdapi.subscription.IssueDto;
import com.lhd.earlybirdapi.subscription.SubscriptionRequestDto;
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
public class GithubRepoService {

  @Value("${oauth.token}")
  private String authToken;

  private GithubRepoRepository githubRepoRepository;

  public GithubRepoService(GithubRepoRepository githubRepoRepository) {
    this.githubRepoRepository = githubRepoRepository;
  }

  public void updateAllGithubReposLatestIssueTimestampsAndUrls() {
    List<GithubRepo> githubRepos = githubRepoRepository.findAll();
    for (GithubRepo githubRepo : githubRepos) {
      Optional<IssueDto> optionalIssueDto = findLatestIssue(githubRepo.getId());
      IssueDto latestIssue = findLatestIssue(githubRepo.getId());
      githubRepo.setLatestRecordedIssueTimestamp(Instant.parse(latestIssue.getCreatedAt()));
      githubRepo.setLatestRecordedIssueUrl(latestIssue.getHtmlUrl());
    }
    githubRepoRepository.saveAll(githubRepos);
  }

  public GithubRepo findGithubRepo(SubscriptionRequestDto subscriptionRequest) {
    String githubRepoId = subscriptionRequest.getRepoOwner() + "/" + subscriptionRequest.getRepoName();
    Optional<GithubRepo> optionalRepo = githubRepoRepository.findById(githubRepoId);
    GithubRepo githubRepo;

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
      String latestIssueCreatedAtTimestampString = latestIssueDto.getCreatedAt();
      return Instant.parse(latestIssueCreatedAtTimestampString);
    } else {
      return Instant.EPOCH;
    }
  }

  Optional<IssueDto> findLatestIssue(String githubRepoId) {
    // TODO: this thing needs a lot of work
    IssueDto[] issues = postForGithubRepoIssues(githubRepoId);
    return Optional.of(issues[0]);
  }

  private List<String> createCurlCommand(String githubRepoId) {
    List<String> commandAndArgs = new ArrayList<>();
    commandAndArgs.add("curl");
    addAuthTokenIfExistsTo(commandAndArgs);
    commandAndArgs.add("https://api.github.com/repos/" + githubRepoId + "/issues");
    return commandAndArgs;
  }

  private void addAuthTokenIfExistsTo(List<String> commandAndArgs) {
    if (!authToken.equals("${oauth.token}")) {
      commandAndArgs.add("-H");
      commandAndArgs.add("Authorization: token " + authToken);
    }
  }

  private IssueDto[] postForGithubRepoIssues(String githubRepoId) {
    List<String> commandAndArgs = createCurlCommand(githubRepoId);
    ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
    IssueDto[] issues;

    try {
      Process p = pb.start();

      BufferedReader br = getBufferedReaderFromProcess(p);
      StringBuilder response = new StringBuilder();

      String line;
      while ((line = br.readLine()) != null) {
        response.append(line);
      }
      String s = response.toString();
      System.out.println("RESPONSE: ");
      System.out.println(s);

      issues = new ObjectMapper().readValue(s, IssueDto[].class);
    } catch (IOException e) {
      // do something
    }

    return issues;
  }

  private BufferedReader getBufferedReaderFromProcess(Process p) {
    InputStream is = p.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    return new BufferedReader(isr);
  }

}
