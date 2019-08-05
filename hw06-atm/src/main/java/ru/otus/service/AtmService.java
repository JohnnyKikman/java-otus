package ru.otus.service;

import ru.otus.value.Banknote;

import java.util.Map;

/**
 * Интерфейс для работы с банкоматом.
 */
public interface AtmService {

    /**
     * Внесение средств в банкомат.
     *
     * @param banknotes Номиналы вносимых купюр и их количество.
     */
    void cashIn(Map<Banknote, Integer> banknotes);

    /**
     * Получение средств из банкомата.
     *
     * @param requiredAmount необходимый к выдаче объем средств
     * @return выдаваемые купюры и их количества
     */
    Map<Banknote, Integer> cashOut(int requiredAmount);

    /**
     * Получение суммы средств, находящихся в банкомате.
     *
     * @return сумма средств в банкомате
     */
    int getTotal();

}
