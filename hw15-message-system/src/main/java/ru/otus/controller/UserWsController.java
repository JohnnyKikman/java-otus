package ru.otus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import ru.otus.broker.MessageSystem;
import ru.otus.message.AddUserRequest;
import ru.otus.message.GetUsersRequest;
import ru.otus.model.User;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserWsController {

    private final ObjectMapper objectMapper;
    private final MessageSystem messageSystem;

    @SneakyThrows
    @MessageMapping("/save")
    public void saveUser(String message) {
        log.info("Received message to save user: {}", message);
        final User user = objectMapper.readValue(message, User.class);
        messageSystem.send(new AddUserRequest(user));
    }

    @MessageMapping("/get")
    public void getUsers() {
        log.info("Received message to get users");
        messageSystem.send(new GetUsersRequest());
    }

}
