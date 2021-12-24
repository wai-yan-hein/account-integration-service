package com.cv.integration.entity;

import lombok.Getter;
import lombok.NonNull;
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
@Table(name = "trader")
public class Trader implements java.io.Serializable {
    @Id
    @Column(name = "code")
    private String traderCode;
    @NonNull
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "trader_name")
    private String traderName;
    @Column(name = "account_code")
    private String accountCode;
    @NonNull
    @Column(name = "app_short_name")
    private String appName;
    @NonNull
    @Column(name = "active")
    private Boolean active;
    @NonNull
    @Column(name = "discriminator")
    private String discriminator;
    @NonNull
    @Column(name = "mac_id")
    private Integer macId;
    @NonNull
    @Column(name = "comp_code")
    private String compCode;

    public Trader() {
    }
}
