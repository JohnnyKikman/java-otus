package ru.otus.message;

import lombok.Data;
import ru.otus.model.User;

import java.util.Collection;

@Data
public class ResponseToFrontend {

    private final boolean isInitial;
    private final Collection<User> users;

}
