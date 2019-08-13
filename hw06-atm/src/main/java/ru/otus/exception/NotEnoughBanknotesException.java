package ru.otus.exception;

import ru.otus.value.Banknote;

import static java.lang.String.format;

public class NotEnoughBanknotesException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Недостаточно банкнот номинала %s в хранилище. " +
            "Требуется банкнот: %d, доступно: %d";

    public NotEnoughBanknotesException(Banknote banknote, int required, int available) {
        super(format(MESSAGE_TEMPLATE, banknote, required, available));
    }
}
