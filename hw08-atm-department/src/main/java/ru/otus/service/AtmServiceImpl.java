package ru.otus.service;

import ru.otus.exception.AmountNotFullyDisposableException;
import ru.otus.exception.InsufficientFundsException;
import ru.otus.service.internal.AtmState;
import ru.otus.storage.Storage;
import ru.otus.storage.StorageImpl;
import ru.otus.value.Banknote;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Стандартная имплементация банкомата.
 */
public class AtmServiceImpl implements AtmService {

    private Storage storage;
    private final AtmState initialState;

    private static final int INITIAL_BANKNOTES = 1;
    private static final AtmState DEFAULT_STATE = new AtmState(
            Arrays.stream(Banknote.values()).collect(toMap(identity(), any -> INITIAL_BANKNOTES))
    );

    public AtmServiceImpl() {
        this.initialState = DEFAULT_STATE;
        this.storage = new StorageImpl(initialState);
    }

    public AtmServiceImpl(AtmState state) {
        this.initialState = state;
        this.storage = new StorageImpl(state);
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
        storage = new StorageImpl(initialState);
    }
}
