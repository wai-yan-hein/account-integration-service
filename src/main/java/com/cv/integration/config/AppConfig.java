package com.cv.integration.config;

import com.cv.integration.common.Util1;
import com.cv.integration.entity.SystemProperty;
import com.cv.integration.entity.VCOA;
import com.cv.integration.repo.SystemPropertyRepo;
import com.cv.integration.repo.VCOARepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@Slf4j
@PropertySource("file:config/application.properties")
public class AppConfig {
    @Autowired
    private SystemPropertyRepo systemPropertyRepo;
    @Autowired
    private VCOARepo vcoaRepo;
    @Value("${account.compcode}")
    private String compCode;

    @Bean
    public void loadSysProp() {
        log.info("loadSysProp.");
        List<SystemProperty> all = systemPropertyRepo.findByCompCode(compCode);
        for (SystemProperty sp : all) {
            Util1.hmSysProp.put(sp.getKey().getPropKey(), sp.getPropValue());
        }
        List<VCOA> currencyCOA = vcoaRepo.getCurrencyCOA();
        for (VCOA coa : currencyCOA) {
            Util1.hmSysProp.put(coa.getCurCode(), coa.getCoaCode());
        }
    }
}
