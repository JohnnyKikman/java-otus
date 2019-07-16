package ru.otus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method marked with this annotation gets logged into stdout before invocation.
 * <p/>
 * Format: 'Executed method: {@literal methodName}, arguments: {@literal list of arguments}'.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {
}
