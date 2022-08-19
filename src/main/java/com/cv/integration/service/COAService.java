package com.cv.integration.service;

import com.cv.integration.entity.ChartOfAccount;

import java.util.List;

public interface COAService {
    ChartOfAccount save(ChartOfAccount coa);

    List<ChartOfAccount> getCOADepartment();
}
