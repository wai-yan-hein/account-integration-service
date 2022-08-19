package com.cv.integration.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "chart_of_account")
public class ChartOfAccount {
    @Id
    @Column(name = "coa_code")
    private String coaCode;
    @Column(name = "coa_name_eng")
    private String coaNameEng;
    @Column(name = "active")
    private boolean active;
    @Temporal(TemporalType.DATE)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "coa_parent")
    private String coaParent;
    @Column(name = "coa_option")
    private String option;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "coa_level")
    private Integer coaLevel;
    @Column(name = "mig_code")
    private String migCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "marked")
    private boolean marked;
    @Column(name = "dept_code")
    private String deptCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChartOfAccount that = (ChartOfAccount) o;
        return coaCode != null && Objects.equals(coaCode, that.coaCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
