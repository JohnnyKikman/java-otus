package ru.otus.service;

import javax.persistence.Id;
import java.util.Collection;

/**
 * Интерфейс для облекченного взаимодейтсвия с БД через JDBC.
 */
public interface DbService<T> {

    /**
     * Сохранение объекта в базе данных.
     *
     * @param objectData сохраняемый объект
     */
    void create(T objectData);

    /**
     * Обновление существующего объекта в базе данных. Существующий объект ищется по полю, отмеченному аннотацией
     * {@link Id}.
     *
     * @param objectData обновляемый объект
     */
    void update(T objectData);

    /**
     * Поиск объекта класса {@literal T} в базе данных по идентификатору, т.е. значению поля, отмеченного
     * аннотацией {@link Id}.
     *
     * @param id значение поля, отмеченного аннотацией {@link Id}, по котрому ищется объект
     * @return найденный объект, либо null
     */
    T load(long id);

    /**
     * Поиск всех объектов класса {@literal T} в базе данных.
     *
     * @return список найденных объектов
     */
    Collection<T> loadAll();

}
