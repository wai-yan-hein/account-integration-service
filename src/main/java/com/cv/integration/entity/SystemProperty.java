package com.cv.integration.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "sys_prop")
public class SystemProperty implements java.io.Serializable {
    @EmbeddedId
    private SystemPropertyKey key;
    @Column(name = "prop_value")
    private String propValue;
}
