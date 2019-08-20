package ru.otus.strategy;

import ru.otus.value.Banknote;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class DescendingBanknotesSortingStrategy implements BanknotesSortingStrategy {
    @Override
    public Collection<Banknote> sort(Collection<Banknote> unsorted) {
        return unsorted.stream()
                .sorted(Comparator.comparing(Banknote::getAmount).reversed())
                .collect(Collectors.toList());
    }
}
