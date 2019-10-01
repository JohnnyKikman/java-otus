package ru.otus.message;

public class GetUsersRequest extends Message {

    public GetUsersRequest(String destination) {
        this.setDestination(destination);
    }

}
