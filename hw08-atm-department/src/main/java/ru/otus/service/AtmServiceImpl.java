package ru.otus.service;

import ru.otus.exception.AmountNotFullyDisposableException;
import ru.otus.exception.InsufficientFundsException;
import ru.otus.service.internal.StorageState;
import ru.otus.storage.Storage;
import ru.otus.storage.StorageImpl;
import ru.otus.value.Banknote;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Стандартная имплементация банкомата.
 */
public class AtmServiceImpl implements AtmService {

    private Storage storage;
    private final StorageState initialState;

    public AtmServiceImpl() {
        this.storage = new StorageImpl();
        this.initialState = storage.getCurrentState();
    }

    public AtmServiceImpl(StorageState state) {
        this.storage = new StorageImpl(state);
        this.initialState = storage.getCurrentState();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void cashIn(Map<Banknote, Integer> banknotes) {
        banknotes.forEach(storage::putBanknotes);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<Banknote, Integer> cashOut(int remainingAmount) {
        final int total = getTotal();
        if (total < remainingAmount) {
            throw new InsufficientFundsException(total);
        }

        final StorageState preCashOutState = storage.getCurrentState();
        final List<Banknote> sortedBanknotes = storage.getAvailableBanknotes().stream()
                .sorted(Comparator.comparing(Banknote::getAmount).reversed())
                .collect(Collectors.toList());
        final Map<Banknote, Integer> returnedBanknotes = new HashMap<>();
        for (Banknote banknote : sortedBanknotes) {
            int amount = banknote.getAmount();
            final int extractedBanknotes = remainingAmount / amount;
            if (remainingAmount >= amount && extractedBanknotes <= storage.getBanknotes(banknote)) {
                remainingAmount -= extractedBanknotes * amount;
                returnedBanknotes.put(banknote, extractedBanknotes);
                storage.fetchBanknotes(banknote, extractedBanknotes);
            }
        }

        if (remainingAmount > 0) {
            storage.restore(preCashOutState);
            throw new AmountNotFullyDisposableException(remainingAmount);
        }
        return returnedBanknotes;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getTotal() {
        return storage.getAvailableBanknotes().stream()
                .map(banknote -> banknote.getAmount() * storage.getBanknotes(banknote))
                .reduce(Integer::sum)
                .orElse(0);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void restoreInitialState() {
        storage.restore(initialState);
    }
}
