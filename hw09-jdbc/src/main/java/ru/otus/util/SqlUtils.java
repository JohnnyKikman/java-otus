package ru.otus.util;

import lombok.NoArgsConstructor;
import ru.otus.value.Operation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

@NoArgsConstructor
public final class SqlUtils {

    private static final String UNKNOWN_SQL_OPERATION = "Неизвестная операция SQL: %s";

    private static String formatParameters(Field[] fields) {
        return Arrays.stream(fields).map(Field::getName).collect(Collectors.joining(","));
    }

    public static String buildSqlTemplate(Class<?> clazz, Operation operation, Field idField) {
        final String tableName = clazz.getSimpleName();
        final Field[] fields = clazz.getDeclaredFields();
        switch (operation) {
            case INSERT:
                return "insert into " + tableName + " ("
                        + SqlUtils.formatParameters(fields) + ") values ("
                        + Arrays.stream(fields).map(field -> "?").collect(Collectors.joining(","))
                        + ")";
            case SELECT:
                return "select "
                        + SqlUtils.formatParameters(fields)
                        + " from " + tableName + " where " + idField.getName() + " = ?";
            case UPDATE:
                return "update " + tableName + " set "
                        + Arrays.stream(fields).map(field -> field.getName() + " = ?")
                        .collect(Collectors.joining(","))
                        + " where " + idField.getName() + " = ?";
            default:
                throw new IllegalStateException(format(UNKNOWN_SQL_OPERATION, operation));
        }
    }

}
