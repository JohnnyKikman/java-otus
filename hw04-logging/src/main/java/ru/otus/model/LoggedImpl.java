package ru.otus.model;

import ru.otus.annotation.Log;

import static java.lang.String.format;

public class LoggedImpl implements Logged {

    @Log
    @Override
    public void action() {
        System.out.println("Action!");
    }

    @Log
    @Override
    public void parametrizedAction(String first, Object second, int third) {
        System.out.println(format("Action (with parameters)!, first - %s, second - %s, third - %d",
                first, second, third));
    }

    @Log
    @Override
    public <T> void genericAction(T unknown) {
        System.out.println(format("What is this action: %s?", unknown));
    }

    @Override
    public void unloggedAction(String whatever) {
        System.out.println(format("Whatever %s is - it's not in the log", whatever));
    }
}
