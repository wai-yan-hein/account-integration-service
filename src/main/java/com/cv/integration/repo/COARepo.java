package com.cv.integration.repo;

import com.cv.integration.entity.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface COARepo extends JpaRepository<ChartOfAccount, String> {
    @Query("select o from ChartOfAccount o where o.coaLevel = 3 and o.key.compCode = :compCode")
    List<ChartOfAccount> getLV3(@Param("compCode") String compCode);
    @Query("select o from ChartOfAccount o where o.deptCode is not null")
    List<ChartOfAccount> getCOADepartment();
    @Query("select o from ChartOfAccount o where o.curCode is not null")
    List<ChartOfAccount> getCOACurrency();
}
