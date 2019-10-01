package ru.otus.broker;

import ru.otus.message.Message;
import ru.otus.service.Receiver;

public interface MessageSystem {

    void start();

    void send(Message message);

    void registerReceiver(String destination, Receiver receiver);

}
