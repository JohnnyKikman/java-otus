package ru.otus.service;

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
    void cashIn(Map<Integer, Integer> banknotes);

    /**
     * Получение средств из банкомата.
     *
     * @param requiredAmount необходимый к выдаче объем средств
     * @return выдаваемые купюры и их количества
     */
    Map<Integer, Integer> cashOut(int requiredAmount);

    /**
     * Получение суммы средств, находящихся в банкомате.
     *
     * @return сумма средств в банкомате
     */
    int getTotal();

}
