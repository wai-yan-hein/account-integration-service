package com.cv.integration.repo;

import com.cv.integration.entity.Gl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GlRepo extends JpaRepository<Gl, String> {
    @Transactional
    @Modifying
    @Query("delete from Gl o where o.refNo=:refNo and o.tranSource=:tranSource")
    void deleteGl(@Param("refNo") String refNo, @Param("tranSource") String tranSource);
}
