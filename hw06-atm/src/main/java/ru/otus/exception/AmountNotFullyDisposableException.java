package ru.otus.exception;

import static java.lang.String.format;

public class AmountNotFullyDisposableException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Невозможно полностью выдать необходимую сумму. Остаток: %d";

    public AmountNotFullyDisposableException(int remainingAmount) {
        super(format(MESSAGE_TEMPLATE, remainingAmount));
    }

}
