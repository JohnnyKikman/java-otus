package ru.otus.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionUtils {

    private static final String NO_DEFAULT_CONSTRUCTOR = "Cannot instantiate class {}: no default constructor";

    /**
     * Method that returns default constructor of a class, or throws {@link RuntimeException} if no such constructor
     * is present.
     */
    public static <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
        try {
            final Constructor<T> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            log.error(NO_DEFAULT_CONSTRUCTOR, clazz.getSimpleName());
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that instantiates a class from its default constructor, or throws {@link RuntimeException} if provided
     * constructor is not a default one.
     */
    public static <T> T instantiateClass(Constructor<T> constructor) {
        try {
            return constructor.newInstance();
        } catch (Exception e) {
            log.error(NO_DEFAULT_CONSTRUCTOR, constructor.getDeclaringClass().getSimpleName());
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that invokes a passed in method on an object instance and, if an error occurs, logs error
     * with specified message and either wraps thrown exception into {@link RuntimeException} or does nothing.
     */
    public static <T> void invokeMethod(Method method, T instance, String errorMessage, boolean rethrowException) {
        try {
            method.invoke(instance);
        } catch (Exception e) {
            log.error(errorMessage);
            if (rethrowException) {
                throw new RuntimeException(e);
            }
        }
    }

}
