package ru.otus.message;

import lombok.Getter;
import ru.otus.model.User;

@Getter
public class AddUserRequest extends Message {

    private final User user;

    public AddUserRequest(String destination, User user) {
        this.user = user;
        this.setDestination(destination);
    }

}
