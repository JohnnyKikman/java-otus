package ru.otus;

import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;
import static ru.otus.TestRunner.run;

@Slf4j
public class MainClass {

    public static void main(String[] args) {
        final String testClassName = getClassName(args);
        runTestsInClass(testClassName);
    }

    /**
     * Method for running tests in class by its name.
     *
     * @param testClassName name of the class containing test
     */
    private static void runTestsInClass(String testClassName) {
        try {
            final Class<?> testClass = Class.forName(testClassName);
            run(testClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(format("Invalid class name %s, unable to test", testClassName));
        }
    }

    /**
     * Method for retrieving test class name from input array of strings.
     *
     * @param args array of strings, first of which is picked as a class name
     * @return extracted class name
     */
    private static String getClassName(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("No class names provided, unable to test");
        } else if (args.length > 1) {
            log.warn("More than one class names provided, only first will be used");
        }
        return args[0];
    }

}
