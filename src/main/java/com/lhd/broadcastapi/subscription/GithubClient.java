package com.lhd.broadcastapi.subscription;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GitHub;

public class GithubClient {

  public static void main(String[] args) {
    GitHubClient client = new GitHubClient();
    client.setCredentials("", "");



    IssueService issueService = new IssueService(client);

    try {
      List<Issue> issues = issueService.getIssues(
          "spring-projects",
          "spring-boot", new HashMap<>());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
