package ru.otus.storage;

import ru.otus.value.Banknote;

import java.util.Collection;

/**
 * Абстрактное хранилище, выдающее и принимающее банкноты.
 */
public interface Storage {

    /**
     * Получение данных о количестве банкнот определенного номинала из хранилища.
     *
     * @param banknote номинал банкноты {@link Banknote}
     * @return список банкнот {@link Banknote} и их количеств
     */
    int getBanknotes(Banknote banknote);

    /**
     * Извлечение заданного количества банкнот из хранилища.
     *
     * @param banknote номинал банкноты {@link Banknote}
     * @param amount извлекаемое количество банкнот
     */
    void fetchBanknotes(Banknote banknote, int amount);

    /**
     * Добавление заданного количества банкнот в хранилище.
     *
     * @param banknote номинал банкноты {@link Banknote}
     * @param amount извлекаемое количество банкнот
     */
    void putBanknotes(Banknote banknote, int amount);

    /**
     * Получение списка доступных в хранилище банкнот.
     *
     * @return коллекция доступных {@link Banknote}
     */
    Collection<Banknote> getAvailableBanknotes();

}
