package com.lhd.earlybirdapi.githubrepo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lhd.earlybirdapi.config.IssueDtoDeserializer;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = IssueDtoDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueDto {

  @JsonProperty("created_at")
  private Instant createdAt;

  @JsonProperty("html_url")
  private String htmlUrl;

}
