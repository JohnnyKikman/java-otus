package ru.otus.message;

import lombok.Getter;
import ru.otus.model.User;

import java.util.Collection;

@Getter
public class UserListResponse extends Message {

    private final Collection<User> users;

    public UserListResponse(String destination, Collection<User> users) {
        this.users = users;
        this.setDestination(destination);
    }
}
