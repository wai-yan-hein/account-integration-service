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
@Table(name = "seq_table")
public class SeqTable implements java.io.Serializable {
    @EmbeddedId
    private SeqKey seqKey;
    @Column(name = "seq_no")
    private Integer seqNo;

}
