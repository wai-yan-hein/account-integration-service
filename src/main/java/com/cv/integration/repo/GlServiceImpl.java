package com.cv.integration.repo;

import com.cv.integration.common.Util1;
import com.cv.integration.entity.Gl;
import com.cv.integration.entity.SeqKey;
import com.cv.integration.entity.SeqTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class GlServiceImpl implements GlService {
    @Autowired
    private GlRepo glRepo;
    @Autowired
    private SeqTableRepo seqTableRepo;

    @Override
    public Gl save(Gl gl) {
        gl.setGlCode(getGlCode(gl.getCompCode(), gl.getMacId()));
        return glRepo.save(gl);
    }

    private String getGlCode(String compCode, Integer macId) {
        int seqNo = 1;
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        SeqKey key = new SeqKey();
        key.setCompCode(compCode);
        key.setPeriod(period);
        key.setSeqOption("GL");
        key.setMacId(macId);
        Optional<SeqTable> seq = seqTableRepo.findById(key);
        if (seq.isPresent()) {
            seqNo = seq.get().getSeqNo();
        }
        SeqTable seqTable = new SeqTable();
        seqTable.setSeqKey(key);
        seqTable.setSeqNo(seqNo + 1);
        seqTableRepo.save(seqTable);
        return String.format("%0" + 3 + "d", macId) + period + String.format("%0" + 8 + "d", seqNo);
    }
}
