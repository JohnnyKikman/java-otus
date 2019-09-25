package ru.otus.message;

import lombok.Data;
import ru.otus.value.Destination;

@Data
public abstract class Message {

    private Destination destination;

}
