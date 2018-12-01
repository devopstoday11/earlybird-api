package com.lhd.broadcastapi.subscription;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class IssueDto {

  private String created_at;
  private String url;

}
