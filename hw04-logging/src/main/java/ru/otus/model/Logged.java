package ru.otus.model;

public interface Logged {

    void action();

    void parametrizedAction(String first, Object second, int third);

    <T> void genericAction(T unknown);

    void unloggedAction(String whatever);

}
