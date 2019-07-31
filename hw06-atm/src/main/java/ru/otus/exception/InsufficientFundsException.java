package ru.otus.exception;

import static java.lang.String.format;

public class InsufficientFundsException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Недостаточно средств. Доступная сумма: %d";

    public InsufficientFundsException(int available) {
        super(format(MESSAGE_TEMPLATE, available));
    }

}
