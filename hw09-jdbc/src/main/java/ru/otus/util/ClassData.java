package ru.otus.util;

import lombok.Getter;
import ru.otus.value.Operation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Getter
public class ClassData<T> {

    private final Field id;
    private final Collection<Field> fields;
    private final Constructor<T> defaultConstructor;
    private final Map<Operation, String> sqls;

    public ClassData(Field id, Collection<Field> fields, Constructor<T> defaultConstructor) {
        this.id = requireNonNull(id, "Id field must be present");
        this.fields = requireNonNull(fields, "Class field array must be present");
        this.defaultConstructor = requireNonNull(defaultConstructor, "Default constructor must be present");
        this.sqls = new HashMap<>();
    }

}
