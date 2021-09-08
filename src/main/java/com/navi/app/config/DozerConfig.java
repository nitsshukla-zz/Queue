package com.navi.app.config;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DozerConfig {
  @Bean
  public DozerBeanMapper getDozerBeanMapper() {
    DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
    dozerBeanMapper.addMapping(DozerConfig.class.getClassLoader().getResourceAsStream("dozer_mapping.xml"));
    return dozerBeanMapper;
  }

}
