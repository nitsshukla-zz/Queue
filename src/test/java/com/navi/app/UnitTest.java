package com.navi.app;

import com.navi.app.config.DozerConfig;
import com.navi.app.service.SubscriberServiceTest;
import org.dozer.DozerBeanMapper;

import static org.mockito.MockitoAnnotations.openMocks;

public class UnitTest {
  protected final DozerBeanMapper dozerBeanMapper;
  public UnitTest() {
    openMocks(this);
    dozerBeanMapper = new DozerBeanMapper();
    dozerBeanMapper.addMapping(DozerConfig.class.getClassLoader().getResourceAsStream("dozer_mapping.xml"));
  }
}
