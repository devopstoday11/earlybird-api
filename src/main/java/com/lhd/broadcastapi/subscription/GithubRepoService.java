package com.lhd.broadcastapi.subscription;

import com.google.gson.Gson;
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
class GithubRepoService {

  @Value("${oauth.token}")
  private String oAuthToken;

  private GithubRepoRepository githubRepoRepository;

  public GithubRepoService(GithubRepoRepository githubRepoRepository) {
    this.githubRepoRepository = githubRepoRepository;
  }

  GithubRepo findGithubRepo(SubscriptionRequestDto subscriptionRequest) {
    String githubRepoId = subscriptionRequest.getRepoOwner() + "/" + subscriptionRequest.getRepoName();
    Optional<GithubRepo> optionalRepo = githubRepoRepository.findById(githubRepoId);
    GithubRepo githubRepo;

    if (optionalRepo.isPresent()) {
      githubRepo = optionalRepo.get();
    } else {
      Instant latestIssueTimestamp = Instant.parse(findLatestIssue(githubRepoId).getCreated_at());
      githubRepo = GithubRepo.builder()
          .id(githubRepoId)
          .latestRecordedIssueTimestamp(latestIssueTimestamp)
          .build();
      githubRepoRepository.save(githubRepo);
    }

    return githubRepo;
  }

  IssueDto findLatestIssue(String githubRepoId) {
    // TODO: this thing needs a lot of work
    try {
      List<String> commandAndArgs = createCurlCommand(githubRepoId);
      ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
      Process p = pb.start();

      InputStream is = p.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      StringBuilder responseStrBuilder = new StringBuilder();
      String line;

      while ((line = br.readLine()) != null) {
        responseStrBuilder.append(line);
      }
      String s = responseStrBuilder.toString();
      System.out.println("RESPONSE: ");
      System.out.println(s);

      IssueDto[] issues = new Gson().fromJson(s, IssueDto[].class);

      if (issues.length > 0) {
        return issues[0];
      }

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

    IssueDto issueDto = new IssueDto();
    issueDto.setCreated_at(Instant.EPOCH.toString());
    return issueDto;
  }

  private List<String> createCurlCommand(String githubRepoId) {
    List<String> commandAndArgs = new ArrayList<>();
    commandAndArgs.add("curl");

    if (!oAuthToken.equals("${oauth.token}")) {
      commandAndArgs.add("-H");
      commandAndArgs.add("Authorization: token " + oAuthToken);
    }

    commandAndArgs.add("https://api.github.com/repos/" + githubRepoId + "/issues");
    return commandAndArgs;
  }

}
