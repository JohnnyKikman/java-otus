package ru.otus.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

import static java.lang.String.format;

public class DbExecutorImpl implements DbExecutor {

    private static final String UPDATED_INFO_TEMPLATE = "Выполнено обновление БД операцией: %s.\nОбновлено строк: %d";
    private static final String SELECTED_INFO_TEMPLATE = "Выполнено получение записей из БД операцией: %s";

    /**
     * {@inheritDoc}.
     */
    @Override
    public void executeUpdate(String sql, Connection connection, Consumer<PreparedStatement> fieldSetter) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            fieldSetter.accept(preparedStatement);
            final int updatedRows = preparedStatement.executeUpdate();
            System.out.println(format(UPDATED_INFO_TEMPLATE, sql, updatedRows));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void executeQuery(String sql, Connection connection, Consumer<PreparedStatement> fieldSetter,
                             Consumer<ResultSet> objectMapper) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            fieldSetter.accept(preparedStatement);
            final ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(format(SELECTED_INFO_TEMPLATE, sql));
            if (resultSet.next()) {
                objectMapper.accept(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
