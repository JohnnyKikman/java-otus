package ru.otus.message;

import static ru.otus.value.Destination.DB_SERVICE;

public class GetUsersRequest extends Message {

    public GetUsersRequest() {
        this.setDestination(DB_SERVICE);
    }

}
