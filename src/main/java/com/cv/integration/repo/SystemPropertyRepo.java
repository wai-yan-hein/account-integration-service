package com.cv.integration.repo;

import com.cv.integration.entity.SystemProperty;
import com.cv.integration.entity.SystemPropertyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SystemPropertyRepo extends JpaRepository<SystemProperty, SystemPropertyKey> {
    @Query("select o from SystemProperty o where o.key.compCode = :compCode")
    List<SystemProperty> findByCompCode(@Param("compCode") String compCode);
}
