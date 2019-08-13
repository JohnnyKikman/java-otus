package ru.otus.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.otus.annotation.Id;
import ru.otus.util.ReflectionUtils;
import ru.otus.util.SqlKey;
import ru.otus.util.SqlUtils;
import ru.otus.value.Operation;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static ru.otus.value.Operation.INSERT;
import static ru.otus.value.Operation.SELECT;
import static ru.otus.value.Operation.UPDATE;

@RequiredArgsConstructor
public class JdbcTemplateImpl implements JdbcTemplate {

    private static final String NO_ID_FIELD = "В переданном объекте типа %s нет поля, отмеченного аннотацией @Id";

    private final Connection connection;
    private final Map<SqlKey, String> sqlCache = new HashMap<>();

    /**
     * {@inheritDoc}.
     */
    @Override
    public void create(Object objectData) {
        try {
            new DbExecutorImpl().executeUpdate(
                    getTemplate(objectData.getClass(), INSERT),
                    connection,
                    statement -> assignFields(objectData, statement, false)
            );
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void update(Object objectData) {
        try {
            new DbExecutorImpl().executeUpdate(
                    getTemplate(objectData.getClass(), UPDATE),
                    connection,
                    statement -> assignFields(objectData, statement, true)
            );
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public <T> T load(long id, Class<T> targetClass) {
        final T result = ReflectionUtils.newInstance(targetClass);
        try {
            new DbExecutorImpl().executeQuery(
                    getTemplate(targetClass, SELECT),
                    connection,
                    id,
                    resultSet -> {
                        try {
                            final Field[] fields = targetClass.getDeclaredFields();
                            int i = 1;
                            for (Field field : fields) {
                                field.setAccessible(true);
                                final Object fieldValue = resultSet.getObject(i++);
                                field.set(result, fieldValue);
                            }
                        } catch (SQLException | IllegalAccessException e) {
                            System.out.println(e.getMessage());
                        }
                    }
            );
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return ReflectionUtils.getId(result) != null ? result : null;
    }

    /**
     * Функция получения SQL-запроса для заданного класса {@link Class} и операции {@link Operation}.
     */
    private String getTemplate(Class<?> clazz, Operation operation) {
        final SqlKey key = new SqlKey(clazz, operation);
        String sql = sqlCache.get(key);
        if (sql != null) {
            return sql;
        }

        final Field[] classFields = clazz.getDeclaredFields();
        final boolean hasId = Arrays.stream(classFields)
                .anyMatch(field -> field.getAnnotationsByType(Id.class).length > 0);
        if (!hasId) {
            throw new IllegalArgumentException(format(NO_ID_FIELD, clazz.getSimpleName()));
        }

        sql = SqlUtils.buildSqlTemplate(clazz, operation);
        sqlCache.put(key, sql);

        return sql;
    }

    /**
     * Почередно присываивает полям в {@link PreparedStatement} значения из объекта.
     */
    @SneakyThrows
    private void assignFields(Object object, PreparedStatement preparedStatement, boolean withId) {
        final Field[] fields = object.getClass().getDeclaredFields();
        int i = 1;
        for (Field field : fields) {
            field.setAccessible(true);
            preparedStatement.setObject(i++, field.get(object));
        }
        if (withId) {
            preparedStatement.setLong(i, ReflectionUtils.getId(object));
        }
    }
}
