package com.cv.integration.service;

import com.cv.integration.entity.ChartOfAccount;
import com.cv.integration.entity.SeqKey;
import com.cv.integration.entity.SeqTable;
import com.cv.integration.repo.COARepo;
import com.cv.integration.repo.SeqTableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class COAServiceImpl implements COAService {
    @Autowired
    private SeqTableRepo seqTableRepo;
    @Autowired
    private COARepo coaRepo;

    @Override
    public ChartOfAccount save(ChartOfAccount coa) {
        if (!Objects.isNull(coa.getKey().getCoaCode())) {
            coa.getKey().setCoaCode(getCOACode(coa.getMacId(), coa.getKey().getCompCode()));
        }
        coaRepo.save(coa);
        return coa;
    }

    @Override
    public List<ChartOfAccount> getCOADepartment() {
        return coaRepo.getCOADepartment();
    }

    private String getCOACode(Integer macId, String compCode) {
        int seqNo = 1;
        SeqKey key = new SeqKey();
        key.setCompCode(compCode);
        key.setPeriod("-");
        key.setSeqOption("COA");
        key.setMacId(macId);
        Optional<SeqTable> seq = seqTableRepo.findById(key);
        if (seq.isPresent()) {
            seqNo = seq.get().getSeqNo();
        }
        SeqTable seqTable = new SeqTable();
        seqTable.setSeqKey(key);
        seqTable.setSeqNo(seqNo + 1);
        seqTableRepo.save(seqTable);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
