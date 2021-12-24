package com.cv.integration.repo;

import com.cv.integration.entity.SeqKey;
import com.cv.integration.entity.SeqTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeqTableRepo extends JpaRepository<SeqTable, SeqKey> {
}
