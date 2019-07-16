package ru.otus.handler;

import ru.otus.annotation.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class LoggingInvocationHandler implements InvocationHandler {

    private static final String LOG_HEADER = "Executed method: ";
    private static final String ARGUMENT_LOG_HEADER = ", arguments: ";
    private static final String PARAMS_DELIMITER = ", ";

    private final Object instance;
    private final Map<String, String> methodLogs;

    public LoggingInvocationHandler(Object instance) {
        this.instance = instance;
        methodLogs = new HashMap<>();
        stream(instance.getClass().getMethods()).forEach(method -> {
            if (method.getAnnotation(Log.class) != null) {
                methodLogs.put(methodNameKey(method), methodLogString(method));
            }
        });
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String methodLoggingString = methodLogs.get(methodNameKey(method));
        if (methodLoggingString != null) {
            System.out.println(format(methodLoggingString, args));
        }
        return method.invoke(instance, args);
    }

    /**
     * Make a template for logging method (using its name & number of parameters).
     */
    private String methodLogString(Method method) {
        final StringBuilder formattedLog = new StringBuilder(LOG_HEADER);
        formattedLog.append(method.getName());

        final int parameterCount = method.getParameterCount();
        if (parameterCount != 0) {
            formattedLog.append(ARGUMENT_LOG_HEADER);
            formattedLog.append(IntStream.range(0, parameterCount).mapToObj(i -> "%s")
                    .collect(joining(PARAMS_DELIMITER)));
        }

        return formattedLog.toString();
    }

    /**
     * Make key from method signature (name & ordered list of parameter type names).
     */
    private String methodNameKey(Method method) {
        return method.getName() + ":" + stream(method.getParameterTypes()).map(Class::getName)
                .collect(joining(PARAMS_DELIMITER));
    }

}
