package ru.otus.message;

import lombok.Getter;
import ru.otus.model.User;

import static ru.otus.value.Destination.FRONTEND_SERVICE;

@Getter
public class SingleUserResponse extends Message {

    private final User user;

    public SingleUserResponse(User user) {
        this.user = user;
        this.setDestination(FRONTEND_SERVICE);
    }

}
