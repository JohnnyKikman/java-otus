package ru.otus.writer;

import lombok.SneakyThrows;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

public class JsonWriterImpl implements JsonWriter {

    private static final Set<Class> VALUE_CLASSES = Set.of(String.class, Enum.class, Number.class, Boolean.class,
            Character.class, Iterable.class, Map.class);

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toJson(Object object) {
        if (object == null) {
            return JsonValue.NULL.toString();
        }

        if (isValue(object)) {
            return formatField(object).toString();
        }

        return toJsonObject(object).toString();
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
     * Преобразует объект Java в {@link JsonObject}, готовый к сериализации.
     */
    private JsonObject toJsonObject(Object object) {
        final Map<String, JsonValue> mapOfFields = new LinkedHashMap<>();
        extractFields(object.getClass(), object, mapOfFields);

        final JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        mapOfFields.forEach(jsonBuilder::add);
        return jsonBuilder.build();
    }

    /**
     * Извлекает значения полей из объекта (сначала - объявленные в классе, которому принадлежит сущность; затем, по
     * порядку - поля суперклассов).
     */
    private void extractFields(Class<?> clazz, Object object, Map<String, JsonValue> mapOfFields) {
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
    private void extractField(Field field, Object object, Map<String, JsonValue> mapOfFields) {
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
    private JsonValue formatField(Object value) {
        if (value instanceof String || value instanceof Enum) {
            return Json.createValue(value.toString());
        } else if (value instanceof Character) {
            return Json.createValue(String.valueOf((char) value));
        } else if (value instanceof Boolean) {
            return (boolean) value ? JsonValue.TRUE : JsonValue.FALSE;
        } else if (value instanceof Integer) {
            return Json.createValue((Integer) value);
        } else if (value instanceof Byte) {
            return Json.createValue((Byte) value);
        } else if (value instanceof Short) {
            return Json.createValue((Short) value);
        } else if (value instanceof Long) {
            return Json.createValue((Long) value);
        } else if (value instanceof Double) {
            return Json.createValue((Double) value);
        } else if (value instanceof Float) {
            return Json.createValue((Float) value);
        } else if (value instanceof BigDecimal) {
            return Json.createValue((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            return Json.createValue((BigInteger) value);
        } else if (value.getClass().isArray()) {
            final int length = Array.getLength(value);
            final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (int i = 0; i < length; i++) {
                arrayBuilder.add(formatField(Array.get(value, i)));
            }
            return arrayBuilder.build();
        } else if (value instanceof Iterable) {
            final Iterable iterable = (Iterable) value;
            final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            StreamSupport.stream(iterable.spliterator(), false)
                    .forEach(element -> arrayBuilder.add(formatField(element)));
            return arrayBuilder.build();
        } else if (value instanceof Map) {
            final JsonObjectBuilder mapBuilder = Json.createObjectBuilder();
            ((Map) value).forEach((key, mapValue) -> mapBuilder.add(key.toString(), formatField(mapValue)));
            return mapBuilder.build();
        } else {
            return Json.createObjectBuilder(toJsonObject(value)).build();
        }
    }

    /**
     * Проверяет, является ли переданный объект значением, которое необходимо сериализовать "само по себе".
     */
    private boolean isValue(Object object) {
        return VALUE_CLASSES.stream().anyMatch(clazz -> clazz.isInstance(object)) || object.getClass().isArray();
    }

}
