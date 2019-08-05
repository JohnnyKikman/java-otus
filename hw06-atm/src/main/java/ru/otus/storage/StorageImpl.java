package ru.otus.storage;

import ru.otus.value.Banknote;

import java.util.Arrays;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class StorageImpl implements Storage {

    private static final int INITIAL_BANKNOTES = 1;

    private final Map<Banknote, Integer> amounts;

    public StorageImpl() {
        amounts = Arrays.stream(Banknote.values()).collect(toMap(identity(), any -> INITIAL_BANKNOTES));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<Banknote, Integer> getAmounts() {
        return amounts;
    }
}
