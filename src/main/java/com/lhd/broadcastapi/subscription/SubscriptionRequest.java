package com.lhd.broadcastapi.subscription;

import lombok.Data;

@Data
public class SubscriptionRequest {

  private String email;
  private String owner;
  private String repoName;

}
