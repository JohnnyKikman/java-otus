package ru.otus.service;

import javax.persistence.Id;

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
     * Поиск объекта класса {@literal clazz} в базе данных по идентификатору, т.е. значению поля, отмеченного
     * аннотацией {@link Id}.
     *
     * @param id значение поля, отмеченного аннотацией {@link Id}, по котрому ищется объект
     * @return найденный объект, либо null
     */
    T load(long id);

}
