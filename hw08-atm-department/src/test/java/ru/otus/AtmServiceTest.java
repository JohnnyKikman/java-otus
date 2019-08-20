package ru.otus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.otus.exception.AmountNotFullyDisposableException;
import ru.otus.exception.InsufficientFundsException;
import ru.otus.service.AtmService;
import ru.otus.service.AtmServiceImpl;
import ru.otus.storage.Storage;
import ru.otus.value.Banknote;

import java.util.AbstractMap;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static ru.otus.value.Banknote.FIFTY;
import static ru.otus.value.Banknote.ONE_HUNDRED;
import static ru.otus.value.Banknote.ONE_THOUSAND;
import static ru.otus.value.Banknote.TEN;
import static ru.otus.value.Banknote.TWO_HUNDRED;

class AtmServiceTest {

    @Mock
    private Storage storage;

    private AtmService atmService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        atmService = new AtmServiceImpl();

        when(storage.getAvailableBanknotes()).thenReturn(Set.of(ONE_THOUSAND, TWO_HUNDRED, ONE_HUNDRED, FIFTY, TEN));
        when(storage.getBanknotes(ONE_THOUSAND)).thenReturn(2);
        when(storage.getBanknotes(TWO_HUNDRED)).thenReturn(1);
        when(storage.getBanknotes(ONE_HUNDRED)).thenReturn(2);
        when(storage.getBanknotes(FIFTY)).thenReturn(1);
        when(storage.getBanknotes(TEN)).thenReturn(5);
    }

    @AfterEach
    void tearDown() {
        reset(storage);
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
        when(storage.getBanknotes(any(Banknote.class))).thenReturn(0);

        assertThatThrownBy(() -> atmService.cashOut(Integer.MAX_VALUE))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("Недостаточно средств. Доступная сумма: 0");
    }

    @Test
    void shouldFailCashOutWithAmountNotFullyDisposable() {
        assertThatThrownBy(() -> atmService.cashOut(111))
                .isInstanceOf(AmountNotFullyDisposableException.class)
                .hasMessage("Невозможно полностью выдать необходимую сумму. Остаток: 1");
    }

}
