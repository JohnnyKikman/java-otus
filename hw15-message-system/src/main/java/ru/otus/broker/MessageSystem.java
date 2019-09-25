package ru.otus.broker;

import ru.otus.message.Message;
import ru.otus.service.Receiver;
import ru.otus.value.Destination;

public interface MessageSystem {

    void start();

    void send(Message message);

    void registerReceiver(Destination key, Receiver receiver);

}
