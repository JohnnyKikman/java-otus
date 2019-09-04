package ru.otus.controller;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.exception.UnableToGetEntitiesException;
import ru.otus.exception.UnableToSaveEntityException;
import ru.otus.model.User;
import ru.otus.service.DbService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final DbService<User> userService;

    @PostMapping("/user")
    public void saveUser(@RequestBody User user) {
        try {
            userService.create(user);
        } catch (Exception e) {
            throw new UnableToSaveEntityException(user);
        }
    }

    @GetMapping("/user")
    public String getUsers() {
        try {
            return new Gson().toJson(userService.loadAll());
        } catch (Exception e) {
            throw new UnableToGetEntitiesException(User.class);
        }
    }

}
