package ru.otus.service;

import lombok.Getter;
import ru.otus.value.Banknote;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Реализация хранилища, которая:
 * - содержит все возможные в системе виды купюр (т.е. все значения {@link Banknote})
 * - выдает купюры, идя от максимального номинала к минимальному
 */
@Getter
public class MaxToMinRetrievingStorage extends Storage {

    private static final int INITIAL_BANKNOTES = 1;
    private static final Properties PROPS = new Properties();

    private final Map<Banknote, Integer> amounts;

    public MaxToMinRetrievingStorage() {
        amounts = Arrays.stream(Banknote.values()).collect(toMap(identity(), any -> INITIAL_BANKNOTES));
    }

    @Override
    Map<Banknote, Integer> calculateBanknotes(int remainingAmount) {
        final List<Banknote> sortedBanknotes = Arrays.stream(Banknote.values())
                .sorted(Comparator.comparing(Banknote::getAmount).reversed())
                .collect(Collectors.toList());

        final Map<Banknote, Integer> extractedBanknotes = new HashMap<>();
        for (Banknote banknote : sortedBanknotes) {
            int amount = banknote.getAmount();
            int availableBanknotes = amounts.get(banknote);
            if (remainingAmount >= amount && remainingAmount <= availableBanknotes * amount) {
                final int extractedCount = remainingAmount / amount;
                extractedBanknotes.put(banknote, extractedCount);
                remainingAmount -= extractedCount * amount;
            }
        }

        return extractedBanknotes;
    }
}
