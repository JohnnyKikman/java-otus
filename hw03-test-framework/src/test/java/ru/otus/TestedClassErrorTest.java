package ru.otus;

import lombok.extern.slf4j.Slf4j;
import ru.otus.annotation.AfterAll;
import ru.otus.annotation.AfterEach;
import ru.otus.annotation.BeforeAll;
import ru.otus.annotation.BeforeEach;
import ru.otus.annotation.Test;

@Slf4j
public class TestedClassErrorTest {

    @BeforeAll
    public static void setUpClass() {
        log.info("@BeforeAll");
    }

    @BeforeEach
    public void setUp() {
        log.info("@BeforeEach");
    }

    @AfterEach
    public void tearDown() {
        log.info("@AfterEach");
    }

    @AfterAll
    public static void tearDownClass() {
        log.info("@AfterAll");
    }

    @Test
    public void firstTest() {
        throw new RuntimeException();
    }

    @Test
    public void secondTest() {
        log.info("Test 2");
    }

}
