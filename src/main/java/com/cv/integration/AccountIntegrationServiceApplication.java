package com.cv.integration;

import com.cv.integration.common.Tray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.awt.*;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class AccountIntegrationServiceApplication {
    public static void main(String[] args) throws AWTException {
        new Tray().startup();
        SpringApplication.run(AccountIntegrationServiceApplication.class, args);
    }
}
