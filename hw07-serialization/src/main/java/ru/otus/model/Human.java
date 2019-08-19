package ru.otus.model;

import ru.otus.model.value.Feature;
import ru.otus.model.value.Trait;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import static ru.otus.model.value.Feature.CAN_CODE_ON_JAVA;
import static ru.otus.model.value.Feature.IS_SENTIENT;

@SuppressWarnings("unused")
public class Human extends Creature {

    private Collection<Human> children;

    private double height;

    private BigDecimal weight;

    private Map<Trait, String> traits;

    public Human(Collection<Human> children, double height, BigDecimal weight, Map<Trait, String> traits) {
        super("Homo sapiens", 2, true, new Feature[]{IS_SENTIENT, CAN_CODE_ON_JAVA});
        this.children = children;
        this.height = height;
        this.weight = weight;
        this.traits = traits;
    }

}
