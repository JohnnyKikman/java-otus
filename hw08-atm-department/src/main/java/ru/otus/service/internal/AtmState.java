package ru.otus.service.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.value.Banknote;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class AtmState {

    private final Map<Banknote, Integer> initialAmounts;

}
