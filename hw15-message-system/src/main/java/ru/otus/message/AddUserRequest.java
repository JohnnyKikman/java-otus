package ru.otus.message;

import lombok.Getter;
import ru.otus.model.User;

import static ru.otus.value.Destination.DB_SERVICE;

@Getter
public class AddUserRequest extends Message {

    private final User user;

    public AddUserRequest(User user) {
        this.user = user;
        this.setDestination(DB_SERVICE);
    }

}
