package ru.otus.service;

import lombok.extern.slf4j.Slf4j;
import ru.otus.broker.MessageSystem;
import ru.otus.message.AddUserRequest;
import ru.otus.message.GetUsersRequest;
import ru.otus.message.Message;
import ru.otus.message.SingleUserResponse;
import ru.otus.message.UserListResponse;
import ru.otus.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class DatabaseService implements Receiver {

    private final DbService<User> dbService;
    private final MessageSystem messageSystem;

    private final Map<Class<? extends Message>, Consumer<Message>> handlers;

    public DatabaseService(DbService<User> dbService, MessageSystem messageSystem) {
        this.dbService = dbService;
        this.messageSystem = messageSystem;

        handlers = new HashMap<>();
        handlers.put(AddUserRequest.class, this::addUser);
        handlers.put(GetUsersRequest.class, this::getUsers);
    }

    @Override
    public Map<Class<? extends Message>, Consumer<Message>> getHandlers() {
        return handlers;
    }

    private void addUser(Message message) {
        final AddUserRequest addUserRequest = (AddUserRequest) message;
        log.info("Received message: {}", message);
        final User user = addUserRequest.getUser();
        dbService.create(user);
        messageSystem.send(new SingleUserResponse(user));
    }

    private void getUsers(Message message) {
        log.info("Received message: {}", message);
        final Collection<User> users = dbService.loadAll();
        messageSystem.send(new UserListResponse(users));
    }
}
