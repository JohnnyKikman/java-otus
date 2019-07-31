package ru.otus.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.exception.UnsupportedBanknotesException;

/**
 * Номиналы банкнот.
 */
@Getter
@RequiredArgsConstructor
public enum Banknote {

    TEN(10),
    FIFTY(50),
    ONE_HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500),
    ONE_THOUSAND(1000),
    TWO_THOUSAND(2000),
    FIVE_THOUSAND(5000);

    private final int amount;

    /**
     * Получение константы {@link Banknote} по числовому значению суммы.
     */
    public static Banknote of(int value) {
        for (Banknote banknote : values()) {
            if (banknote.getAmount() == value) {
                return banknote;
            }
        }
        throw new UnsupportedBanknotesException(value);
    }

}
