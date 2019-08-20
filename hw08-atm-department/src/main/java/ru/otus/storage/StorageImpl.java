package ru.otus.storage;

import ru.otus.exception.NotEnoughBanknotesException;
import ru.otus.service.internal.StorageState;
import ru.otus.value.Banknote;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class StorageImpl implements Storage {

    private Map<Banknote, Integer> amounts;

    private static final int INITIAL_BANKNOTES = 1;
    private static final Map<Banknote, Integer> DEFAULT_STATE =
            Arrays.stream(Banknote.values()).collect(toMap(identity(), any -> INITIAL_BANKNOTES));

    public StorageImpl() {
        amounts = DEFAULT_STATE;
    }

    public StorageImpl(StorageState state) {
        amounts = state.getAmounts();
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public StorageState getCurrentState() {
        return new StorageState(amounts);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void restore(StorageState state) {
        this.amounts = state.getAmounts();
    }
}
