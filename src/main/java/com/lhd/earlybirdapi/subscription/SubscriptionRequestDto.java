package com.lhd.earlybirdapi.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubscriptionRequestDto {

  private String email;
  private String repoOwner;
  private String repoName;

}
