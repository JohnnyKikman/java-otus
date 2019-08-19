package ru.otus.util;

import lombok.NoArgsConstructor;

import javax.json.Json;
import javax.json.JsonValue;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class NumberUtils {

    private static final String INVALID_NUMBER_TYPE = "Неизвестный тип числа: %s";

    /**
     * Преобразует число в корректный {@link JsonValue}.
     */
    public static JsonValue toJsonNumber(Number number) {
        if (number instanceof Long) {
            return Json.createValue((Long) number);
        } else if (number instanceof Integer) {
            return Json.createValue((Integer) number);
        } else if (number instanceof Short) {
            return Json.createValue((Short) number);
        } else if (number instanceof Byte) {
            return Json.createValue((Byte) number);
        } else if (number instanceof Double) {
            return Json.createValue((Double) number);
        } else if (number instanceof Float) {
            return Json.createValue((Float) number);
        } else if (number instanceof BigDecimal) {
            return Json.createValue((BigDecimal) number);
        } else if (number instanceof BigInteger) {
            return Json.createValue((BigInteger) number);
        }
        throw new IllegalArgumentException(format(INVALID_NUMBER_TYPE, number.getClass().getSimpleName()));
    }

}
