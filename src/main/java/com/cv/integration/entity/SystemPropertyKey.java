package com.cv.integration.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@ToString
@Embeddable
public class SystemPropertyKey implements java.io.Serializable {
    @Column(name = "prop_key")
    private String propKey;
    @Column(name = "comp_code")
    private String compCode;

    public SystemPropertyKey() {
    }

    public SystemPropertyKey(String propKey, String compCode) {
        this.propKey = propKey;
        this.compCode = compCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SystemPropertyKey that = (SystemPropertyKey) o;
        return Objects.equals(propKey, that.propKey)
                && Objects.equals(compCode, that.compCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propKey, compCode);
    }
}
