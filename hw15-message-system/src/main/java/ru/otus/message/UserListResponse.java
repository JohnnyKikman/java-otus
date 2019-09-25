package ru.otus.message;

import lombok.Getter;
import ru.otus.model.User;

import java.util.Collection;

import static ru.otus.value.Destination.FRONTEND_SERVICE;

@Getter
public class UserListResponse extends Message {

    private final Collection<User> users;

    public UserListResponse(Collection<User> users) {
        this.users = users;
        this.setDestination(FRONTEND_SERVICE);
    }
}
