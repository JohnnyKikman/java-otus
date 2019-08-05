package ru.otus.service;

import ru.otus.exception.AmountNotFullyDisposableException;
import ru.otus.exception.InsufficientFundsException;
import ru.otus.storage.Storage;
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

    private final Storage storage;

    public AtmServiceImpl(Storage storage) {
        this.storage = storage;
    }

    /**
     * {@inheritDoc}
     */
    public void cashIn(Map<Banknote, Integer> banknotes) {
        final Map<Banknote, Integer> amounts = storage.getAmounts();
        banknotes.forEach(((banknote, amount) ->
                amounts.put(banknote, amounts.get(banknote) + amount)));
    }

    /**
     * {@inheritDoc}
     */
    public Map<Banknote, Integer> cashOut(int remainingAmount) {
        final int total = getTotal();
        if (total < remainingAmount) {
            throw new InsufficientFundsException(total);
        }

        final Map<Banknote, Integer> amounts = storage.getAmounts();
        final List<Banknote> sortedBanknotes = storage.getAmounts().keySet().stream()
                .sorted(Comparator.comparing(Banknote::getAmount).reversed())
                .collect(Collectors.toList());
        final Map<Banknote, Integer> returnedBanknotes = new HashMap<>();
        for (Banknote banknote : sortedBanknotes) {
            int amount = banknote.getAmount();
            int availableBanknotes = amounts.get(banknote);
            if (remainingAmount >= amount) {
                final int extractedBanknotes = remainingAmount / amount;
                remainingAmount -= extractedBanknotes * amount;
                returnedBanknotes.put(banknote, extractedBanknotes);
                amounts.put(banknote, availableBanknotes - extractedBanknotes);
            }
        }

        if (remainingAmount > 0) {
            throw new AmountNotFullyDisposableException(remainingAmount);
        }
        return returnedBanknotes;
    }

    /**
     * {@inheritDoc}
     */
    public int getTotal() {
        return storage.getAmounts().entrySet().stream()
                .map(entry -> entry.getKey().getAmount() * entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);
    }

}
