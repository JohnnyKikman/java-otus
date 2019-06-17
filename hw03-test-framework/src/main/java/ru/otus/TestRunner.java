package ru.otus;

import lombok.extern.slf4j.Slf4j;
import ru.otus.annotation.AfterAll;
import ru.otus.annotation.AfterEach;
import ru.otus.annotation.BeforeAll;
import ru.otus.annotation.BeforeEach;
import ru.otus.annotation.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.stream;

@Slf4j
public class TestRunner {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("No class names provided, unable to test");
        } else if (args.length > 1) {
            log.warn("More than one class names provided, only first will be used");
        }

        final String testClassName = args[0];
        try {
            final Class<?> testClass = Class.forName(testClassName);
            run(testClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(format("Invalid class name %s, unable to test", testClassName));
        }
    }

    /**
     * Method to run classes that have methods marked with annotations from ru.otus.annotation.* annotations.
     * <p/>
     * Methods marked with {@link BeforeAll} are run first. Then each {@link Test}-marked method is called on distinct
     * class entities, with {@link BeforeEach} called before and {@link AfterEach} called after. Finally, all
     * {@link AfterAll}-marked methods are run.
     * <p/>
     * Note that:
     * <ul>
     *     <li>{@link BeforeAll} and {@link AfterAll} methods must be static.</li>
     *     <li>Multiple annotations could be placed on method, but only one is picked with priority in the
     *     following order: {@link BeforeAll}, {@link AfterAll}, {@link BeforeEach}, {@link AfterEach}, {@link Test}.
     *     </li>
     * </ul>
     *
     * @param testClass class containing {@link Test}-marked tests
     */
    public static <T> void run(Class<T> testClass) {
        final String className = testClass.getSimpleName();
        final Constructor<T> testClassConstructor;
        try {
            testClassConstructor = testClass.getConstructor();
            testClassConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            log.error("Cannot instantiate class {}: no default constructor", className);
            return;
        }

        final List<Method> beforeAllMethods = new ArrayList<>();
        final List<Method> afterAllMethods = new ArrayList<>();
        final List<Method> beforeEachMethods = new ArrayList<>();
        final List<Method> afterEachMethods = new ArrayList<>();
        final List<Method> testMethods = new ArrayList<>();

        stream(testClass.getMethods()).forEach(method -> {
            if (method.getAnnotation(BeforeAll.class) != null) {
                beforeAllMethods.add(method);
            } else if (method.getAnnotation(AfterAll.class) != null) {
                afterAllMethods.add(method);
            } else if (method.getAnnotation(BeforeEach.class) != null) {
                beforeEachMethods.add(method);
            } else if (method.getAnnotation(AfterEach.class) != null) {
                afterEachMethods.add(method);
            } else if (method.getAnnotation(Test.class) != null) {
                testMethods.add(method);
            }
        });

        beforeAllMethods.forEach(method -> {
            try {
                method.invoke(null);
            } catch (Exception e) {
                log.error("Cannot invoke @BeforeAll method {}", method.getName());
                throw new RuntimeException(e);
            }
        });
        testMethods.forEach(method -> {
            final String methodName = method.getName();
            log.info("Running test method {}", methodName);

            final T testClassInstance;
            try {
                testClassInstance = testClassConstructor.newInstance();
            } catch (Exception e) {
                log.error("Cannot instantiate class {}: no default constructor", className);
                throw new RuntimeException(e);
            }

            try {
                for (Method beforeMethod : beforeEachMethods) {
                    try {
                        beforeMethod.invoke(testClassInstance);
                    } catch (Exception e) {
                        log.error("Error preparing test method {}:", methodName, e);
                        return;
                    }
                }
                method.invoke(testClassInstance);
            } catch (Exception e) {
                log.error("Error running test method {}:", methodName, e);
                return;
            } finally {
                for (Method afterMethod : afterEachMethods) {
                    try {
                        afterMethod.invoke(testClassInstance);
                    } catch (Exception e) {
                        log.error("Error shutting down test method {}:", methodName, e);
                        break;
                    }
                }
            }

            log.info("Test method {} completed", methodName);
        });
        afterAllMethods.forEach(method -> {
            try {
                method.invoke(null);
            } catch (Exception e) {
                log.error("Cannot invoke @AfterAll method {}", method.getName());
                throw new RuntimeException(e);
            }
        });
    }

}
