package ru.otus;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class TestRunnerTest {

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

    @Test
    void shouldFailForTestClassWithErrorInBeforeAll() {
        assertThrows(RuntimeException.class, () -> TestRunner.run(TestedClassErrorBeforeAll.class));
    }

}
