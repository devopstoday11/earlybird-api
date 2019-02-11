package com.lhd.earlybirdapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BroadcastApplication {

  public static void main(String[] args) {
    SpringApplication.run(BroadcastApplication.class, args);
  }

}
