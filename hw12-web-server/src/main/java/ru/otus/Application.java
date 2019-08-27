package ru.otus;

import ru.otus.server.UserServer;

public class Application {

    public static void main(String[] args) {
        new UserServer().start();
    }

}
