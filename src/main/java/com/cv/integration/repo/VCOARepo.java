package com.cv.integration.repo;

import com.cv.integration.entity.VCOA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VCOARepo extends JpaRepository<VCOA, String> {
    @Query("select o from VCOA o where o.curCode is not null and o.caoNameEng like 'D%'")
    List<VCOA> getCurrencyCOA();
}
