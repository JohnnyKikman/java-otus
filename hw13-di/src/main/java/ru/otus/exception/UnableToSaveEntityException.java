package ru.otus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UnableToSaveEntityException extends RuntimeException {

    public UnableToSaveEntityException(Object object) {
        super("Невозможно сохранить сущность: " + object);
    }

}
