package com.cv.integration.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "gl")
public class Gl implements java.io.Serializable {
    @Id
    @Column(name = "gl_code", unique = true, nullable = false)
    @NonNull
    private String glCode;
    @NonNull
    @Temporal(TemporalType.DATE)
    @Column(name = "gl_date")
    private Date glDate;
    @Column(name = "description")
    private String description;
    @Column(name = "source_ac_id")
    private String srcAccCode;
    @Column(name = "account_id")
    private String accCode;
    @NonNull
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "dr_amt")
    private Double drAmt;
    @Column(name = "cr_amt")
    private Double crAmt;
    @Column(name = "reference")
    private String reference;
    @NonNull
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "voucher_no")
    private String vouNo;
    @Column(name = "trader_code")
    private String traderCode;
    @NonNull
    @Column(name = "comp_code")
    private String compCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "user_code")
    private String createdBy;
    @NonNull
    @Column(name = "tran_source")
    private String tranSource;
    @Column(name = "remark")
    private String remark;
    @NonNull
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "ref_no")
    private String refNo;
    @Transient
    private boolean deleted;
    @Transient
    private boolean cash = false;

    public Gl() {
    }
}
