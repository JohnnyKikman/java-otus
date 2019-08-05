package ru.otus.storage;

import ru.otus.value.Banknote;

import java.util.Map;

/**
 * Абстрактное хранилище, выдающее и принимающее банкноты.
 */
public interface Storage {

    /**
     * Получение данных о содержимом хранилища.
     *
     * @return список банкнот {@link Banknote} и их количеств
     */
    Map<Banknote, Integer> getAmounts();

}
