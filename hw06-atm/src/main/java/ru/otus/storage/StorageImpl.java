package ru.otus.storage;

import ru.otus.exception.NotEnoughBanknotesException;
import ru.otus.value.Banknote;

import java.util.Arrays;
import java.util.Collection;
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
    public int getBanknotes(Banknote banknote) {
        return amounts.get(banknote);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void fetchBanknotes(Banknote banknote, int amount) {
        final int currentAmount = amounts.get(banknote);
        if (currentAmount - amount < 0) {
            throw new NotEnoughBanknotesException(banknote, amount, currentAmount);
        }
        amounts.put(banknote, currentAmount - amount);
    }


    /**
     * {@inheritDoc}.
     */
    @Override
    public void putBanknotes(Banknote banknote, int amount) {
        amounts.put(banknote, amounts.get(banknote) + amount);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<Banknote> getAvailableBanknotes() {
        return amounts.keySet();
    }
}
