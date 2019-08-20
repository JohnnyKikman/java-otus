package ru.otus.strategy;

import ru.otus.value.Banknote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class RandomBanknotesSortingStrategy implements BanknotesSortingStrategy {
    @Override
    public Collection<Banknote> sort(Collection<Banknote> unsorted) {
        final List<Banknote> shuffled = new ArrayList<>(unsorted);
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
