package ru.otus.service;

import ru.otus.storage.Storage;
import ru.otus.strategy.BanknotesSortingStrategy;
import ru.otus.value.Banknote;
import ru.otus.visitor.Visitor;

import java.util.Map;

/**
 * Интерфейс для работы с банкоматом.
 */
public interface AtmService {

    /**
     * Получение хранилища банкомата.
     */
    Storage getStorage();

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
     * Восстановление первоначального состояния банкомата.
     */
    void restoreInitialState();

    /**
     * Изменение стратегии, по которой будут сортироваться банкноты при выдаче.
     *
     * @param strategy стратегия сортировки банкнот {@link BanknotesSortingStrategy}
     */
    void setStrategy(BanknotesSortingStrategy strategy);

    /**
     * Получение объекта {@link Visitor}, вычисляющего значение типа int.
     *
     * @param visitor объект {@link Visitor}
     */
    int accept(Visitor visitor);

}
