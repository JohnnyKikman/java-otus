package ru.otus;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class TestRunnerTest {

    // Positive cases

    @Test
    void shouldPassForCorrectTestClassByName() {
        TestRunner.main(new String[]{"ru.otus.TestedClass"});
    }

    @Test
    void shouldPassForCorrectTestClass() {
        TestRunner.run(TestedClass.class);
    }

    @Test
    void shouldPassForTestClassWithErrorInTest() {
        TestRunner.run(TestedClassErrorTest.class);
    }

    @Test
    void shouldPassForTestClassWithErrorInBeforeEach() {
        TestRunner.run(TestedClassErrorBeforeEach.class);
    }

    // Negative cases

    @Test
    void shouldFailForTestClassByNameWhenNoArgumentsProvided() {
        assertThrows(IllegalArgumentException.class, () -> TestRunner.main(new String[0]));
    }

    @Test
    void shouldFailForTestClassWithIncorrectName() {
        assertThrows(IllegalArgumentException.class, () -> TestRunner.main(new String[]{"ru.otus.DoesNotExist"}));
    }

    @Test
    void shouldFailForTestClassWithErrorInBeforeAll() {
        assertThrows(RuntimeException.class, () -> TestRunner.run(TestedClassErrorBeforeAll.class));
    }

}
