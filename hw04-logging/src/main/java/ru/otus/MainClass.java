package ru.otus;

import ru.otus.model.Logged;
import ru.otus.model.LoggedImpl;
import ru.otus.util.ProxiedLoggedFactory;

import static java.math.BigInteger.TWO;

public class MainClass {

    public static void main(String[] args) {
        // Check out stdout for testing

        // No logging
        callAllMethods(new LoggedImpl());

        System.out.println();
        System.out.println("----- LOGGING ON -----");
        System.out.println();

        // Proxied with logging
        callAllMethods(ProxiedLoggedFactory.getLogged());
    }

    private static void callAllMethods(Logged logged) {
        logged.action();
        logged.parametrizedAction("uno", TWO, 3);
        logged.genericAction("unknown");
        // not logged in any case
        logged.unloggedAction("...");
    }

}
