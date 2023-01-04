package com.cv.integration.config;

import com.cv.integration.common.Util1;
import com.cv.integration.entity.ChartOfAccount;
import com.cv.integration.repo.COARepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
@Slf4j
@PropertySource("file:config/application.properties")
public class AppConfig {
    @Autowired
    private COARepo coaRepo;
    @Autowired
    private Environment environment;

    @Bean
    public void loadSysProp() {
        log.info("loadSysProp.");
        List<ChartOfAccount> list = coaRepo.getCOACurrency();
        for (ChartOfAccount coa : list) {
            Util1.hmSysProp.put(coa.getCurCode(), coa.getKey().getCoaCode());
        }
        Util1.setCusAcc(environment.getRequiredProperty("customer.account"));
        Util1.setSupAcc(environment.getRequiredProperty("supplier.account"));
        Util1.setMultiCur(Util1.getBoolean(environment.getRequiredProperty("multi.currency")));
    }
}
