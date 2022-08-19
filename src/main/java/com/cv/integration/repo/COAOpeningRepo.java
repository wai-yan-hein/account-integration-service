package com.cv.integration.repo;

import com.cv.integration.entity.COAOpening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface COAOpeningRepo extends JpaRepository<COAOpening,Integer> {
    @Transactional
    @Modifying
    @Query("delete from COAOpening o where o.traderCode=:traderCode")
    void deleteOpening(@Param("traderCode") String traderCode);
}
