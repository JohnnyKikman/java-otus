package ru.otus.util;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import ru.otus.annotation.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ReflectionUtils {

    private static final String NO_ID_FIELD_IN_CLASS =
            "В переданном объекте типа %s нет поля, отмеченного аннотацией @Id";
    private static final String CANNOT_INSTANTIATE_CLASS =
            "Невозможно создать класс %s с помощью конструктора без параметров";

    @SneakyThrows
    public static Long getId(Object object) {
        final Class<?> clazz = object.getClass();
        final Optional<Field> id = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.getAnnotationsByType(Id.class).length > 0)
                .findFirst();
        if (id.isEmpty()) {
            throw new IllegalArgumentException(format(NO_ID_FIELD_IN_CLASS, clazz));
        }
        id.get().setAccessible(true);
        return (Long) id.get().get(object);
    }

    @SneakyThrows
    static String getIdFieldName(Class<?> clazz) {
        final Optional<Field> id = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.getAnnotationsByType(Id.class).length > 0)
                .findFirst();
        if (id.isEmpty()) {
            throw new IllegalArgumentException(format(NO_ID_FIELD_IN_CLASS, clazz));
        }
        return id.get().getName();
    }

    public static <T> T newInstance(Class<T> targetClass) {
        try {
            final Constructor<T> constructor = targetClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(format(CANNOT_INSTANTIATE_CLASS, targetClass.getSimpleName()));
        }
    }

}
