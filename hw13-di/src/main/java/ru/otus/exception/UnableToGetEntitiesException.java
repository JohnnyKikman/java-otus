package ru.otus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UnableToGetEntitiesException extends RuntimeException {

    public UnableToGetEntitiesException(Class<?> entityClass) {
        super("Невозможно получить список сущностей типа " + entityClass.getSimpleName());
    }

}
