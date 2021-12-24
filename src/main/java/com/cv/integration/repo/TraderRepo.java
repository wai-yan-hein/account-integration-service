package com.cv.integration.repo;

import com.cv.integration.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraderRepo extends JpaRepository<Trader, String> {

}
