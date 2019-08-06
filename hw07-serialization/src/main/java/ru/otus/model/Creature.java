package ru.otus.model;

import lombok.AllArgsConstructor;
import lombok.Setter;
import ru.otus.model.value.Feature;

import static lombok.AccessLevel.PACKAGE;

@Setter
@SuppressWarnings("unused")
@AllArgsConstructor(access = PACKAGE)
public abstract class Creature {

    private String species;

    private int eyesCount;

    private boolean isReal;

    private Feature[] features;

}
