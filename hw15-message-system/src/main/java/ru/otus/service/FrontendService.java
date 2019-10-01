package ru.otus.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.otus.message.Message;
import ru.otus.message.ResponseToFrontend;
import ru.otus.message.SingleUserResponse;
import ru.otus.message.UserListResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;

@Slf4j
public class FrontendService implements Receiver {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate template;
    private final Map<Class<? extends Message>, Consumer<Message>> handlers;

    public FrontendService(ObjectMapper objectMapper, SimpMessagingTemplate template) {
        this.objectMapper = objectMapper;
        this.template = template;
        this.handlers = new HashMap<>();
        handlers.put(SingleUserResponse.class, this::sendSingleUser);
        handlers.put(UserListResponse.class, this::sendUserList);
        log.info("Registered handlers for commands: {}", handlers.keySet());
    }

    @Override
    public Map<Class<? extends Message>, Consumer<Message>> getHandlers() {
        return handlers;
    }

    @SneakyThrows
    private void sendSingleUser(Message message) {
        final SingleUserResponse singleUserResponse = (SingleUserResponse) message;
        log.info("Received message: {}", message);
        final ResponseToFrontend response = new ResponseToFrontend(false, singletonList(singleUserResponse.getUser()));
        template.convertAndSend("/topic/users", objectMapper.writeValueAsString(response));
    }

    @SneakyThrows
    private void sendUserList(Message message) {
        final UserListResponse userListResponse = (UserListResponse) message;
        log.info("Received message: {}", message);
        final ResponseToFrontend response = new ResponseToFrontend(true, userListResponse.getUsers());
        template.convertAndSend("/topic/users", objectMapper.writeValueAsString(response));
    }
}
