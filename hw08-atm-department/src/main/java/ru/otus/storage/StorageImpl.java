package ru.otus.storage;

import ru.otus.exception.NotEnoughBanknotesException;
import ru.otus.service.internal.AtmState;
import ru.otus.value.Banknote;

import java.util.Collection;
import java.util.Map;

public class StorageImpl implements Storage {

    private final Map<Banknote, Integer> amounts;

    public StorageImpl(AtmState state) {
        amounts = state.getInitialAmounts();
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
