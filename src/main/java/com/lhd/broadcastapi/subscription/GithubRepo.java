package com.lhd.broadcastapi.subscription;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;

@Entity
@Table(name = "github_repo")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class GithubRepo {

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "latest_issue_timestamp")
  private String latestIssueTimestamp;

  @Builder
  private GithubRepo(String id, String latestIssueTimestamp) {
    Validate.notEmpty(id);
    Validate.notNull(latestIssueTimestamp);

    this.id = id;
    this.latestIssueTimestamp = latestIssueTimestamp;
  }

}
