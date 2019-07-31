package ru.otus.service;

import ru.otus.exception.InsufficientFundsException;
import ru.otus.exception.UnsupportedBanknotesException;
import ru.otus.value.Banknote;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

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
    public void cashIn(Map<Integer, Integer> amounts) {
        final Set<Integer> availableSlots = storage.getAvailableSlots().stream().map(Banknote::getAmount)
                .collect(toSet());
        final Set<Integer> givenBanknotes = amounts.keySet();
        if (!availableSlots.containsAll(givenBanknotes)) {
            givenBanknotes.removeAll(availableSlots);
            throw new UnsupportedBanknotesException(givenBanknotes);
        }

        storage.accept(amounts.entrySet().stream()
                .collect(toMap(entry -> Banknote.of(entry.getKey()), Map.Entry::getValue))
        );
    }

    /**
     * {@inheritDoc}
     */
    public Map<Integer, Integer> cashOut(int requiredAmount) {
        final int total = getTotal();
        if (total < requiredAmount) {
            throw new InsufficientFundsException(total);
        }

        return storage.retrieve(requiredAmount).entrySet().stream()
                .collect(toMap(entry -> entry.getKey().getAmount(), Map.Entry::getValue));
    }

    /**
     * {@inheritDoc}
     */
    public int getTotal() {
        return storage.getTotalAmount();
    }

}
