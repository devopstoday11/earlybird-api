package com.lhd.earlybirdapi.subscription;

import lombok.Data;

@Data
public class SubscriptionRequestDto {

  private String email;
  private String repoOwner;
  private String repoName;

}