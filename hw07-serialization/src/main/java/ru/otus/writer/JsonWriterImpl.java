package ru.otus.writer;

import lombok.SneakyThrows;

import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class JsonWriterImpl implements JsonWriter {

    private static final String DELIMITER = ",";
    private static final String STRING_FORMATTING = "\"%s\"";
    private static final String ARRAY_FORMATTING = "[%s]";
    private static final String FIELD_FORMATTING = "\"%s\":%s";

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toJson(Object object) {
        final Map<String, String> mapOfFields = new LinkedHashMap<>();
        extractFields(object.getClass(), object, mapOfFields);
        return formatObject(mapOfFields);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    @SneakyThrows
    public void toJson(Object object, Writer writer) {
        final String jsonString = toJson(object);
        writer.write(jsonString);
    }

    /**
     * Извлекает значения полей из объекта (сначала - объявленные в классе, которому принадлежит сущность; затем, по
     * порядку - поля суперклассов).
     */
    private void extractFields(Class<?> clazz, Object object, Map<String, String> mapOfFields) {
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> extractField(field, object, mapOfFields));
        final Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            extractFields(superclass, object, mapOfFields);
        }
    }

    /**
     * Извлекает значение поля из объекта, форматирует значение (если оно не null) и помещает его в {@link Map} для
     * последующей сериализации.
     */
    @SneakyThrows
    private void extractField(Field field, Object object, Map<String, String> mapOfFields) {
        field.setAccessible(true);
        final Object value = field.get(object);
        if (value != null) {
            mapOfFields.put(field.getName(), formatField(value));
        }
    }

    /**
     * Форматирует объект в значение формата JSON, в зависимости от типа.
     */
    @SuppressWarnings("unchecked")
    private String formatField(Object value) {
        if (value instanceof String || value instanceof Enum) {
            return format(STRING_FORMATTING, value);
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Iterable) {
            final Iterable iterable = (Iterable) value;
            return format(ARRAY_FORMATTING, StreamSupport.stream(iterable.spliterator(), false)
                    .map(this::formatField)
                    .collect(joining(DELIMITER)));
        } else if (value.getClass().isArray()) {
            final Object[] array = (Object[]) value;
            return format(ARRAY_FORMATTING, Arrays.stream(array)
                    .map(this::formatField)
                    .collect(joining(DELIMITER)));
        } else if (value instanceof Map) {
            final Map<String, String> formattedMap = ((Map<?, ?>) value).entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            entry -> formatField(entry.getValue())
                    ));
            return formatObject(formattedMap);
        } else {
            return toJson(value);
        }
    }

    /**
     * Преобразует объект (в виде {@link Map} полей и уже отформатированных значений) в строку формата JSON
     */
    private String formatObject(Map<String, String> mapOfFields) {
        return "{" + mapOfFields.entrySet().stream()
                .map(entry -> format(FIELD_FORMATTING, entry.getKey(), entry.getValue()))
                .collect(joining(DELIMITER))
                + "}";
    }

}
