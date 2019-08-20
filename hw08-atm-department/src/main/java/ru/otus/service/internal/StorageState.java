package ru.otus.service.internal;

import lombok.Getter;
import ru.otus.value.Banknote;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Memento object.
 */
@Getter
public class StorageState {

    private final Map<Banknote, Integer> amounts;

    public StorageState(Map<Banknote, Integer> amounts) {
        this.amounts = amounts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
