package ru.otus.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.otus.annotation.Id;

import java.math.BigDecimal;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    private Long no;

    private String type;

    private BigDecimal rest;

}
