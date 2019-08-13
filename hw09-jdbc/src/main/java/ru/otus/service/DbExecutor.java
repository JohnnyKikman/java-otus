package ru.otus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

/**
 * Интерфейс, абстрагирующий операции обновления и получения данных из БД посредством JDBC.
 */
public interface DbExecutor {

    /**
     * Выполняет переданный в параметре {@literal sql} модифицирующий запрос (insert/update).
     *
     * @param sql тело запроса
     * @param connection {@link Connection}
     * @param fieldSetter {@link Consumer}, связывающий поля класса с параметрами запроса
     */
    void executeUpdate(String sql, Connection connection, Consumer<PreparedStatement> fieldSetter);

    /**
     * Выполняет переданный в параметре {@literal sql} запрос select и маппит результат на доменную сущность.
     *
     * @param sql тело запроса
     * @param connection {@link Connection}
     * @param id идентификатор, по которому производится поиск объекта в БД
     * @param objectMapper {@link Consumer}, приводящий результат запроса к доменной сущности
     */
    void executeQuery(String sql, Connection connection, long id, Consumer<ResultSet> objectMapper);

}
