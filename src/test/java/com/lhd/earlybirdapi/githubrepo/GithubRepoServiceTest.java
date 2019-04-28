package com.lhd.earlybirdapi.githubrepo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class GithubRepoServiceTest {

  private Instant currentTime = Instant.now();
  private Instant currentTimePlus5Seconds = currentTime.plusSeconds(5L);
  private Instant currentTimeMinus5Seconds = currentTime.minusSeconds(5L);

  @Captor
  ArgumentCaptor<List<GithubRepo>> savedGithubReposCaptor;

  @Mock
  private GithubRepoRepository githubRepoRepositoryMock;

  @Mock
  RestTemplate restTemplateMock;

  @InjectMocks
  private GithubRepoService githubRepoService;


  @Test
  public void givenRepoWithNewIssue_whenUpdateAllGithubReposLatestIssueTimestampsAndUrls_thenUpdate() {
    IssueDto issueDto = new IssueDto();
    issueDto.setCreatedAt(currentTimePlus5Seconds);
    issueDto.setHtmlUrl("http://github.com/user/repo/issue_updated");
    GithubRepo githubRepo = GithubRepo.builder()
        .id("genericRepoId")
        .latestRecordedIssueTimestamp(currentTime)
        .build();
    githubRepo.setLatestRecordedIssueUrl("http://github.com/user/repo/issue");
    IssueDto[] issues = new IssueDto[1];
    issues[0] = issueDto;
    ResponseEntity<IssueDto[]> responseEntity = new ResponseEntity<>(issues, HttpStatus.OK);
    when(restTemplateMock.getForEntity("/repos/" + githubRepo.getId() + "/issues", IssueDto[].class))
        .thenReturn(responseEntity);
    when(githubRepoRepositoryMock.findAll())
        .thenReturn(new ArrayList<>(Collections.singletonList(githubRepo)));

    githubRepoService.updateAllGithubReposLatestIssueTimestampsAndUrls();

    InOrder inOrder = inOrder(githubRepoRepositoryMock);
    inOrder.verify(githubRepoRepositoryMock, times(1)).findAll();
    inOrder.verify(githubRepoRepositoryMock, times(1)).saveAll(savedGithubReposCaptor.capture());
    List<GithubRepo> savedGithubRepos = savedGithubReposCaptor.getValue();
    assertEquals(githubRepo.getId(), savedGithubRepos.get(0).getId());
    assertEquals(issueDto.getCreatedAt(), savedGithubRepos.get(0).getLatestRecordedIssueTimestamp());
    assertEquals(issueDto.getHtmlUrl(), savedGithubRepos.get(0).getLatestRecordedIssueUrl());
  }


  @Test
  public void givenRepoWithNoNewIssues_whenUpdateAllGithubReposLatestIssueTimestampsAndUrls_thenDoNothing() {
    IssueDto issueDto = new IssueDto();
    issueDto.setCreatedAt(currentTimeMinus5Seconds);
    issueDto.setHtmlUrl("http://github.com/user/repo/issue_updated");
    GithubRepo githubRepo = GithubRepo.builder()
        .id("genericRepoId")
        .latestRecordedIssueTimestamp(currentTime)
        .build();
    githubRepo.setLatestRecordedIssueUrl("http://github.com/user/repo/issue");
    IssueDto[] issues = new IssueDto[1];
    issues[0] = issueDto;
    ResponseEntity<IssueDto[]> responseEntity = new ResponseEntity<>(issues, HttpStatus.OK);
    when(restTemplateMock.getForEntity("/repos/" + githubRepo.getId() + "/issues", IssueDto[].class))
        .thenReturn(responseEntity);
    when(githubRepoRepositoryMock.findAll())
        .thenReturn(new ArrayList<>(Collections.singletonList(githubRepo)));

    githubRepoService.updateAllGithubReposLatestIssueTimestampsAndUrls();

    InOrder inOrder = inOrder(githubRepoRepositoryMock);
    inOrder.verify(githubRepoRepositoryMock, times(1)).findAll();
    inOrder.verify(githubRepoRepositoryMock, times(1)).saveAll(savedGithubReposCaptor.capture());
    List<GithubRepo> savedGithubRepos = savedGithubReposCaptor.getValue();
    assertEquals(githubRepo.getId(), savedGithubRepos.get(0).getId());
    assertEquals(githubRepo.getLatestRecordedIssueTimestamp(),
        savedGithubRepos.get(0).getLatestRecordedIssueTimestamp());
    assertEquals(githubRepo.getLatestRecordedIssueUrl(), savedGithubRepos.get(0).getLatestRecordedIssueUrl());
  }


  @Test
  public void givenRepoWithNoIssues_whenUpdateAllGithubReposLatestIssueTimestampsAndUrls_thenDoNothing() {
    GithubRepo githubRepo = GithubRepo.builder()
        .id("genericRepoId3")
        .latestRecordedIssueTimestamp(currentTime)
        .build();
    githubRepo.setLatestRecordedIssueUrl("http://github.com/user/repo/issue");
    ResponseEntity<IssueDto[]> responseEntity = new ResponseEntity<>(new IssueDto[0], HttpStatus.OK);
    when(restTemplateMock.getForEntity("/repos/" + githubRepo.getId() + "/issues", IssueDto[].class))
        .thenReturn(responseEntity);
    when(githubRepoRepositoryMock.findAll())
        .thenReturn(new ArrayList<>(Collections.singletonList(githubRepo)));

    githubRepoService.updateAllGithubReposLatestIssueTimestampsAndUrls();

    InOrder inOrder = inOrder(githubRepoRepositoryMock);
    inOrder.verify(githubRepoRepositoryMock, times(1)).findAll();
    inOrder.verify(githubRepoRepositoryMock, times(1)).saveAll(savedGithubReposCaptor.capture());
    List<GithubRepo> savedGithubRepos = savedGithubReposCaptor.getValue();
    assertEquals(githubRepo.getId(), savedGithubRepos.get(0).getId());
    assertEquals(githubRepo.getLatestRecordedIssueTimestamp(),
        savedGithubRepos.get(0).getLatestRecordedIssueTimestamp());
    assertEquals(githubRepo.getLatestRecordedIssueUrl(), savedGithubRepos.get(0).getLatestRecordedIssueUrl());
  }

}
