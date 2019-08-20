package ru.otus.exception;

import ru.otus.value.Banknote;

import static java.lang.String.format;

public class NoBanknoteException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Недоступна ячейка с банкнотами номинала %d";

    public NoBanknoteException(Banknote banknote) {
        super(format(MESSAGE_TEMPLATE, banknote.getAmount()));
    }

}
