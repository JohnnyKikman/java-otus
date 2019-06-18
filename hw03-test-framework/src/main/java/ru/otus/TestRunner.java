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
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static ru.otus.utils.ReflectionUtils.getDefaultConstructor;
import static ru.otus.utils.ReflectionUtils.instantiateClass;
import static ru.otus.utils.ReflectionUtils.invokeMethod;

@Slf4j
public class TestRunner {

    private static final String TEST_METHOD_ERROR = "Error running test method {}:";
    private static final String BEFORE_EACH_ERROR = "Error preparing test method %s";
    private static final String AFTER_ALL_ERROR = "Cannot invoke @AfterAll method %s";
    private static final String BEFORE_ALL_ERROR = "Cannot invoke @BeforeAll method %s";
    private static final String AFTER_EACH_ERROR = "Error shutting down test method %s";

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
        final Constructor<T> testClassConstructor = getDefaultConstructor(testClass);

        final List<Method> beforeAllMethods = new ArrayList<>();
        final List<Method> afterAllMethods = new ArrayList<>();
        final List<Method> beforeEachMethods = new ArrayList<>();
        final List<Method> afterEachMethods = new ArrayList<>();
        final List<Method> testMethods = new ArrayList<>();

        stream(testClass.getMethods()).forEach(method -> analyzeAnnotations(method, beforeAllMethods,
                afterAllMethods, beforeEachMethods, afterEachMethods, testMethods));

        try {
            beforeAllMethods.forEach(method -> invokeMethod(
                    method, null, format(BEFORE_ALL_ERROR, method.getName()), true
            ));
            testMethods.forEach(method -> {
                final String methodName = method.getName();
                log.info("Running test method {}", methodName);

                final T testClassInstance = instantiateClass(testClassConstructor);
                try {
                    beforeEachMethods.forEach(beforeMethod-> invokeMethod(
                            beforeMethod, testClassInstance, format(BEFORE_EACH_ERROR, methodName), false
                    ));
                    method.invoke(testClassInstance);
                } catch (Exception e) {
                    log.error(TEST_METHOD_ERROR, methodName, e);
                    return;
                } finally {
                    afterEachMethods.forEach(afterMethod -> invokeMethod(
                            afterMethod, testClassInstance, format(AFTER_EACH_ERROR, methodName), false
                    ));
                }

                log.info("Test method {} completed", methodName);
            });
        } finally {
            afterAllMethods.forEach(method -> invokeMethod(
                    method, null, format(AFTER_ALL_ERROR, method.getName()), true
            ));
        }
    }

    private static void analyzeAnnotations(Method method,
                                           Collection<Method> beforeAllMethods,
                                           Collection<Method> afterAllMethods,
                                           Collection<Method> beforeEachMethods,
                                           Collection<Method> afterEachMethods,
                                           Collection<Method> testMethods) {
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
    }

}
