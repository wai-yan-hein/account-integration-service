package com.cv.integration.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Embeddable
public class SeqKey implements java.io.Serializable {
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "seq_option")
    private String seqOption;
    @Column(name = "period")
    private String period;
    @Column(name = "comp_code")
    private String compCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SeqKey seqKey = (SeqKey) o;
        return Objects.equals(macId, seqKey.macId)
                && Objects.equals(seqOption, seqKey.seqOption)
                && Objects.equals(period, seqKey.period)
                && Objects.equals(compCode, seqKey.compCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macId,
                seqOption,
                period,
                compCode);
    }
}
