package com.lhd.broadcastapi.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class IssueDto {

  @JsonProperty("created_at")
  private String createdAt;

  @JsonProperty("html_url")
  private String htmlUrl;

}
