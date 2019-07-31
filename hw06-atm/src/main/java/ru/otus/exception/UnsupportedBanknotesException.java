package ru.otus.exception;

import java.util.Collection;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

public class UnsupportedBanknotesException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE =
            "В переданной сумме содержатся банкноты неподдерживаемых номиналов: %s";

    public UnsupportedBanknotesException(int singleValue) {
        this(singletonList(singleValue));
    }

    public UnsupportedBanknotesException(Collection<Integer> incorrectValues) {
        super(format(MESSAGE_TEMPLATE, incorrectValues));
    }

}
