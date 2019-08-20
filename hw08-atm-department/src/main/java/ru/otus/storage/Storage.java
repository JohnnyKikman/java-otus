package ru.otus.storage;

import ru.otus.command.Command;
import ru.otus.service.internal.StorageState;
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

    /**
     * Получить текущее состояние хранилища.
     *
     * @return текущее состояние {@link StorageState}
     */
    StorageState getCurrentState();

    /**
     * Восстановление состояние хранилища из переданного.
     *
     * @param state состояние хранилища {@link StorageState}
     */
    void restore(StorageState state);

    /**
     * Регистрация команды на выполнение.
     *
     * @param command команда {@link Command}
     */
    void register(Command command);

    /**
     * Выполнение зарегистрированных команд.
     */
    void execute();

}
