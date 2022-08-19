package com.cv.integration.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "v_coa_lv3")
public class VCOA {
    @Id
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "coa_name_eng")
    private String caoNameEng;
    @Column(name = "cur_code")
    private String curCode;
}
