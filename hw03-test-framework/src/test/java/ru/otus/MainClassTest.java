package ru.otus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainClassTest {

    // Positive cases

    @Test
    void shouldPassForCorrectTestClassByName() {
        MainClass.main(new String[]{"ru.otus.TestedClass"});
    }

    // Negative cases

    @Test
    void shouldFailForTestClassByNameWhenNoArgumentsProvided() {
        assertThrows(IllegalArgumentException.class, () -> MainClass.main(new String[0]));
    }

    @Test
    void shouldFailForTestClassWithIncorrectName() {
        assertThrows(IllegalArgumentException.class, () -> MainClass.main(new String[]{"ru.otus.DoesNotExist"}));
    }
}
