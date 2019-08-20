package ru.otus.service;

import lombok.Setter;
import ru.otus.command.FetchBanknotesCommand;
import ru.otus.command.PutBanknotesCommand;
import ru.otus.exception.AmountNotFullyDisposableException;
import ru.otus.exception.InsufficientFundsException;
import ru.otus.service.internal.StorageState;
import ru.otus.storage.Storage;
import ru.otus.storage.StorageImpl;
import ru.otus.strategy.BanknotesSortingStrategy;
import ru.otus.strategy.DescendingBanknotesSortingStrategy;
import ru.otus.value.Banknote;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Стандартная имплементация банкомата.
 */
public class AtmServiceImpl implements AtmService {

    private Storage storage;
    private final StorageState initialState;
    @Setter
    private BanknotesSortingStrategy strategy;

    public AtmServiceImpl() {
        this.storage = new StorageImpl();
        this.initialState = storage.getCurrentState();
        this.strategy = new DescendingBanknotesSortingStrategy();
    }

    public AtmServiceImpl(StorageState state) {
        this.storage = new StorageImpl(state);
        this.initialState = storage.getCurrentState();
        this.strategy = new DescendingBanknotesSortingStrategy();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void cashIn(Map<Banknote, Integer> banknotes) {
        banknotes.forEach((banknote, amount) -> storage.register(new PutBanknotesCommand(storage, banknote, amount)));
        storage.execute();
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
        final Collection<Banknote> sortedBanknotes = strategy.sort(storage.getAvailableBanknotes());
        final Map<Banknote, Integer> returnedBanknotes = new HashMap<>();
        for (Banknote banknote : sortedBanknotes) {
            int amount = banknote.getAmount();
            final int extractedBanknotes = remainingAmount / amount;
            if (remainingAmount >= amount && extractedBanknotes <= storage.getBanknotes(banknote)) {
                remainingAmount -= extractedBanknotes * amount;
                returnedBanknotes.put(banknote, extractedBanknotes);
                storage.register(new FetchBanknotesCommand(storage, banknote, extractedBanknotes));
            }
        }

        if (remainingAmount > 0) {
            storage.restore(preCashOutState);
            throw new AmountNotFullyDisposableException(remainingAmount);
        }
        storage.execute();
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
