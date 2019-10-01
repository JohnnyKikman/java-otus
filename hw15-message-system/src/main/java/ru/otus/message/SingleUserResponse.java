package ru.otus.message;

import lombok.Getter;
import ru.otus.model.User;

@Getter
public class SingleUserResponse extends Message {

    private final User user;

    public SingleUserResponse(String destination, User user) {
        this.user = user;
        this.setDestination(destination);
    }

}
