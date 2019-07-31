package ru.otus.service;

import ru.otus.exception.AmountNotFullyDisposableException;
import ru.otus.value.Banknote;

import java.util.Map;
import java.util.Set;

/**
 * Абстрактное хранилище, производящее операции с банкнотами.
 */
public abstract class Storage {

    /**
     * Принятие банкнот в хранилище.
     *
     * @param acceptedAmounts типы банкнот {@link Banknote} и соответствующие количества
     */
    void accept(Map<Banknote, Integer> acceptedAmounts) {
        final Map<Banknote, Integer> currentAmounts = getAmounts();
        acceptedAmounts.forEach((faceValue, amount) ->
                currentAmounts.put(faceValue, currentAmounts.get(faceValue) + amount));
    }

    /**
     * Получение банкнот из хранилища.
     *
     * @param requiredAmount необходимый для выдачи объем средств
     */
    Map<Banknote, Integer> retrieve(int requiredAmount) {
        final Map<Banknote, Integer> extractedBanknotes = calculateBanknotes(requiredAmount);
        final int extractedAmount = extractedBanknotes.entrySet().stream()
                .map(entry -> entry.getKey().getAmount() * entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);
        if (extractedAmount < requiredAmount) {
            throw new AmountNotFullyDisposableException(requiredAmount - extractedAmount);
        }

        extractedBanknotes.forEach((banknote, value) -> {
            final Map<Banknote, Integer> currentAmounts = getAmounts();
            currentAmounts.put(banknote, currentAmounts.get(banknote) - value);
        });

        return extractedBanknotes;
    }

    /**
     * Получение текущей суммы средств в хранилище.
     */
    int getTotalAmount() {
        return getAmounts().entrySet().stream()
                .map(entry -> entry.getKey().getAmount() * entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);
    }

    /**
     * Получение доступных к внесению в хранилище банкнот.
     */
    Set<Banknote> getAvailableSlots() {
        return getAmounts().keySet();
    }

    /**
     * Получение содержимого хранилища.
     */
    abstract Map<Banknote, Integer> getAmounts();

    /**
     * Расчет количества банкнот каждого номинала, необходимого к выдаче. Настраивается для каждого вида хранилища.
     *
     * @param requiredAmount необходимый для выдачи объем средств
     */
    abstract Map<Banknote, Integer> calculateBanknotes(int requiredAmount);

}
