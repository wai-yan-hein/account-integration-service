package com.cv.integration.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "coa_opening")
public class COAOpening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coa_op_id", unique = true, nullable = false)
    private Integer opId;
    @Temporal(TemporalType.DATE)
    @Column(name = "op_date")
    private Date opDate;
    @Column(name = "source_acc_id")
    private String sourceAccId;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "cr_amt")
    private Double crAmt;
    @Column(name = "dr_amt")
    private Double drAmt;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "comp_code")
    private String compCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "dept_code")
    private String depCode;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "tran_source")
    private String tranSource;
}
