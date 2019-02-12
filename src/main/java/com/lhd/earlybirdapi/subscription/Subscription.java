package com.lhd.earlybirdapi.subscription;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

@Entity
@Table(name = "subscription")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "last_checked_timestamp")
  @Setter
  private Instant lastCheckedTimestamp;

  @ManyToOne
  @JoinColumn(name = "github_repo_id", referencedColumnName = "id", updatable = false, nullable = false)
  private GithubRepo githubRepo;

  @Builder
  private Subscription(String email, GithubRepo githubRepo, Instant lastCheckedTimestamp) {
    Validate.notBlank(email);
    Validate.notNull(githubRepo);
    Validate.notNull(lastCheckedTimestamp);

    this.email = email;
    this.githubRepo = githubRepo;
    this.lastCheckedTimestamp = lastCheckedTimestamp;
  }

  boolean newIssueExists() {
    Instant latestIssueTimestamp = githubRepo.getLatestRecordedIssueTimestamp();
    return latestIssueTimestamp.isAfter(lastCheckedTimestamp);
  }

}
