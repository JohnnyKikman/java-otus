package ru.otus.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.otus.annotation.Id;
import ru.otus.util.ClassData;
import ru.otus.util.SqlUtils;
import ru.otus.value.Operation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static ru.otus.value.Operation.INSERT;
import static ru.otus.value.Operation.SELECT;
import static ru.otus.value.Operation.UPDATE;

@RequiredArgsConstructor
public class JdbcTemplateImpl implements JdbcTemplate {

    private static final String NO_ID_FIELD = "В переданном объекте типа %s нет поля, отмеченного аннотацией @Id";
    private static final String CANNOT_INSTANTIATE_CLASS =
            "Невозможно создать класс %s с помощью конструктора без параметров";

    private final Connection connection;
    private final Map<Class, ClassData> classDataCache = new HashMap<>();

    /**
     * {@inheritDoc}.
     */
    @Override
    public void create(Object objectData) {
        final ClassData<?> classData = getClassData(objectData.getClass(), INSERT);
        try {
            new DbExecutorImpl().executeUpdate(
                    classData.getSqls().get(INSERT),
                    connection,
                    statement -> assignFields(objectData, classData, statement, false)
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
        final ClassData<?> classData = getClassData(objectData.getClass(), UPDATE);
        try {
            new DbExecutorImpl().executeUpdate(
                    classData.getSqls().get(UPDATE),
                    connection,
                    statement -> assignFields(objectData, classData, statement, true)
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
        final ClassData<T> classData = getClassData(targetClass, SELECT);
        final T result;
        try {
            result = classData.getDefaultConstructor().newInstance();
            new DbExecutorImpl().executeQuery(
                    classData.getSqls().get(SELECT),
                    connection,
                    statement -> assignId(statement, 1, id),
                    resultSet -> {
                        try {
                            final Collection<Field> fields = classData.getFields();
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
            if (classData.getId().get(result) == null) {
                return null;
            }
        } catch (SQLException | ReflectiveOperationException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return result;
    }

    /**
     * Функция получения SQL-запроса для заданного класса {@link Class} и операции {@link Operation}.
     */
    @SuppressWarnings("unchecked")
    private <T> ClassData<T> getClassData(Class<T> clazz, Operation operation) {
        ClassData<T> classData = classDataCache.get(clazz);
        if (classData != null) {
            final Map<Operation, String> sqls = classData.getSqls();
            if (!sqls.containsKey(operation)) {
                sqls.put(operation, SqlUtils.buildSqlTemplate(clazz, operation, classData.getId()));
            }
            return classData;
        }

        final Field[] classFields = clazz.getDeclaredFields();
        final Optional<Field> id = Arrays.stream(classFields)
                .filter(field -> field.getAnnotationsByType(Id.class).length > 0)
                .findFirst();
        if (id.isEmpty()) {
            throw new IllegalArgumentException(format(NO_ID_FIELD, clazz.getSimpleName()));
        }
        final Constructor<T> defaultConstructor;
        try {
            defaultConstructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(format(CANNOT_INSTANTIATE_CLASS, clazz.getSimpleName()));
        }
        classData = new ClassData<>(id.get(), asList(classFields), defaultConstructor);
        classData.getSqls().put(operation, SqlUtils.buildSqlTemplate(clazz, operation, classData.getId()));
        classDataCache.put(clazz, classData);

        return classData;
    }

    /**
     * Почередно присываивает полям в {@link PreparedStatement} значения из объекта.
     */
    @SneakyThrows
    private void assignFields(Object object, ClassData<?> classData, PreparedStatement preparedStatement,
                              boolean withId) {
        final Collection<Field> fields = classData.getFields();
        int i = 1;
        for (Field field : fields) {
            field.setAccessible(true);
            preparedStatement.setObject(i++, field.get(object));
        }
        if (withId) {
            assignId(preparedStatement, i, (long) classData.getId().get(object));
        }
    }

    /**
     * Устанавливает в {@link PreparedStatement} идентификатор сущности в заданный {@literal index} параметр.
     */
    @SneakyThrows
    private void assignId(PreparedStatement preparedStatement, int index, long id) {
        preparedStatement.setLong(index, id);
    }
}
