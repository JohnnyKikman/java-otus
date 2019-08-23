package ru.otus.strategy;

import ru.otus.value.Banknote;

import java.util.Collection;

/**
 * Strategy object.
 */
public interface BanknotesSortingStrategy {

    Collection<Banknote> sort(Collection<Banknote> unsorted);

}
