package com.lhd.earlybirdapi.githubrepo;

import com.fasterxml.jackson.databind.ObjectMapper;
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
      if (optionalIssueDto.isPresent()) {
        IssueDto latestIssue = optionalIssueDto.get();
        githubRepo.setLatestRecordedIssueTimestamp(Instant.parse(latestIssue.getCreatedAt()));
        githubRepo.setLatestRecordedIssueUrl(latestIssue.getHtmlUrl());
      }
    }
    githubRepoRepository.saveAll(githubRepos);
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
      String latestIssueCreatedAtTimestampString = latestIssueDto.getCreatedAt();
      return Instant.parse(latestIssueCreatedAtTimestampString);
    } else {
      return Instant.EPOCH;
    }
  }

  Optional<IssueDto> findLatestIssue(String githubRepoId) {
    IssueDto[] issues = postForGithubRepoIssues(githubRepoId);
    return Optional.of(issues[0]);
  }

  // TODO: debug why this request wouldn't work with RestTemplate or the like
  // this really should be an http request, not a cURL executed with ProcessBuilder
  private IssueDto[] postForGithubRepoIssues(String githubRepoId) {
    IssueDto[] issues;

    // TODO: each of the methods that could throw IOExceptions should handle it themselves
    // we could remove the try/catch here in that case, and create more specific custom exceptions
    try {
      Process curlRequestProcess = executeCurlRequestAndRetrieveProcess(githubRepoId);
      BufferedReader bufferedReader = getBufferedReaderFromProcess(curlRequestProcess);
      String serializedResponse = getSerializedResponse(bufferedReader);
      issues = new ObjectMapper().readValue(serializedResponse, IssueDto[].class);
    } catch (IOException e) {
      throw new GithubApiRequestException(e.getMessage());
    }

    return issues;
  }

  private Process executeCurlRequestAndRetrieveProcess(String githubRepoId) throws IOException {
    List<String> curlCommandAndArgs = createCurlCommandAndArgs(githubRepoId);
    ProcessBuilder pb = new ProcessBuilder(curlCommandAndArgs);
    return pb.start();
  }

  private List<String> createCurlCommandAndArgs(String githubRepoId) {
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

  private BufferedReader getBufferedReaderFromProcess(Process p) {
    InputStream is = p.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    return new BufferedReader(isr);
  }

  private String getSerializedResponse(BufferedReader br) throws IOException {
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null) {
      response.append(line);
    }
    return response.toString();
  }

}
