package ru.otus.service;

import ru.otus.message.Message;

import java.util.Map;
import java.util.function.Consumer;

import static java.lang.String.format;

public interface Receiver {

    Map<Class<? extends Message>, Consumer<Message>> getHandlers();

    default void receive(Message message) {
        final Class<? extends Message> messageType = message.getClass();
        final Consumer<Message> handler = getHandlers().get(messageType);
        if (handler == null) {
            throw new IllegalArgumentException(format("Команда %s не поддерживается",
                    messageType.getSimpleName()));
        }
        handler.accept(message);
    }

}
