package com.lhd.broadcastapi.subscription;

import lombok.Data;

@Data
public class SubscriptionRequestDto {

  private String email;
  private String owner;
  private String repoName;

}