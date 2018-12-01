package com.lhd.broadcastapi.config;

import com.lhd.broadcastapi.BroadcastApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    // why set to false? see: https://github.com/spring-projects/spring-boot/issues/2745
    setRegisterErrorPageFilter(false);
    return application.sources(BroadcastApplication.class);
  }

}
