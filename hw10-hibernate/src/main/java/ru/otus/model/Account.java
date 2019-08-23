package ru.otus.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Account")
public class Account implements WithId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long no;

    private String type;

    private BigDecimal rest;

    @Override
    public Long getId() {
        return no;
    }
}
