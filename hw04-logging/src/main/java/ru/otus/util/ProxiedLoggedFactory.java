package ru.otus.util;

import lombok.NoArgsConstructor;
import ru.otus.handler.LoggingInvocationHandler;
import ru.otus.model.Logged;
import ru.otus.model.LoggedImpl;

import static java.lang.reflect.Proxy.newProxyInstance;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ProxiedLoggedFactory {

    /**
     * Make a proxy instance of {@link Logged} with logging switch on.
     */
    public static Logged getLogged() {
        final Logged instance = new LoggedImpl();
        return (Logged) newProxyInstance(
                ProxiedLoggedFactory.class.getClassLoader(),
                new Class[] { Logged.class },
                new LoggingInvocationHandler(instance)
        );
    }

}
