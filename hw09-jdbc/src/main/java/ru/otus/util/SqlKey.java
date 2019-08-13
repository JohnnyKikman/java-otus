package ru.otus.util;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.otus.value.Operation;

@EqualsAndHashCode
@RequiredArgsConstructor
public class SqlKey {

    private final Class<?> clazz;
    private final Operation operation;

}
