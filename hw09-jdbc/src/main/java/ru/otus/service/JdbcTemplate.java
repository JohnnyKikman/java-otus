package ru.otus.service;

import ru.otus.annotation.Id;

/**
 * Интерфейс для облекченного взаимодейтсвия с БД через JDBC.
 */
public interface JdbcTemplate {

    /**
     * Сохранение объекта в базе данных.
     *
     * @param objectData сохраняемый объект
     */
    void create(Object objectData);

    /**
     * Обновление существующего объекта в базе данных. Существующий объект ищется по полю, отмеченному аннотацией
     * {@link Id}.
     *
     * @param objectData обновляемый объект
     */
    void update(Object objectData);

    /**
     * Поиск объекта класса {@literal clazz} в базе данных по идентификатору, т.е. значению поля, отмеченного
     * аннотацией {@link Id}.
     *
     * @param id значение поля, отмеченного аннотацией {@link Id}, по котрому ищется объект
     * @param targetClass тип искомой сущности
     * @return найденный объект, либо null
     */
    <T> T load(long id, Class<T> targetClass);

}
