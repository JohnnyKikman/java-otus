package ru.otus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.exception.AmountNotFullyDisposableException;
import ru.otus.exception.InsufficientFundsException;
import ru.otus.service.AtmService;
import ru.otus.service.AtmServiceImpl;
import ru.otus.service.internal.StorageState;
import ru.otus.value.Banknote;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.MockitoAnnotations.initMocks;
import static ru.otus.value.Banknote.FIFTY;
import static ru.otus.value.Banknote.ONE_HUNDRED;
import static ru.otus.value.Banknote.ONE_THOUSAND;
import static ru.otus.value.Banknote.TEN;
import static ru.otus.value.Banknote.TWO_HUNDRED;

class AtmServiceTest {

    private AtmService atmService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        final Map<Banknote, Integer> initialAmounts = new HashMap<>();
        initialAmounts.put(ONE_THOUSAND, 2);
        initialAmounts.put(TWO_HUNDRED, 1);
        initialAmounts.put(ONE_HUNDRED, 2);
        initialAmounts.put(FIFTY, 1);
        initialAmounts.put(TEN, 5);
        atmService = new AtmServiceImpl(new StorageState(initialAmounts));
    }

    @Test
    void shouldPassCashOutWithSufficientFunds() {
        assertThat(atmService.cashOut(1250)).containsOnly(
                new AbstractMap.SimpleEntry<>(ONE_THOUSAND, 1),
                new AbstractMap.SimpleEntry<>(TWO_HUNDRED, 1),
                new AbstractMap.SimpleEntry<>(FIFTY, 1)
        );
    }

    @Test
    void shouldFailCashOutWithInsufficientFunds() {
        atmService = new AtmServiceImpl(new StorageState(Collections.emptyMap()));

        assertThatThrownBy(() -> atmService.cashOut(Integer.MAX_VALUE))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("Недостаточно средств. Доступная сумма: 0");
    }

    @Test
    void shouldFailCashOutWithAmountNotFullyDisposable() {
        assertThatThrownBy(() -> atmService.cashOut(111))
                .isInstanceOf(AmountNotFullyDisposableException.class)
                .hasMessage("Невозможно полностью выдать необходимую сумму. Остаток: 1");

        // проверка восстановления состояния
        assertThat(atmService.getTotal()).isEqualTo(2500);
    }

}
