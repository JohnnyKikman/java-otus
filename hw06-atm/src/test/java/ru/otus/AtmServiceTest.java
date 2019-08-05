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
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        atmService = new AtmServiceImpl(storage);

        final Map<Banknote, Integer> storageContents = new HashMap<>();
        storageContents.put(ONE_THOUSAND, 2);
        storageContents.put(TWO_HUNDRED, 1);
        storageContents.put(ONE_HUNDRED, 2);
        storageContents.put(FIFTY, 1);
        storageContents.put(TEN, 5);
        when(storage.getAmounts()).thenReturn(storageContents);
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
        when(storage.getAmounts()).thenReturn(emptyMap());

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
