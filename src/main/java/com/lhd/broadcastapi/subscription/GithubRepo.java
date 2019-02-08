package com.lhd.broadcastapi.subscription;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

@Entity
@Table(name = "github_repo")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class GithubRepo {

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "latest_issue_timestamp", nullable = false)
  @Setter
  private Instant latestRecordedIssueTimestamp;

  @Column(name = "latest_issue_url")
  @Setter
  private String latestRecordedIssueUrl;

  @Builder
  private GithubRepo(String id, Instant latestRecordedIssueTimestamp) {
    Validate.notEmpty(id);
    Validate.notNull(latestRecordedIssueTimestamp);

    this.id = id;
    this.latestRecordedIssueTimestamp = latestRecordedIssueTimestamp;
  }

}
