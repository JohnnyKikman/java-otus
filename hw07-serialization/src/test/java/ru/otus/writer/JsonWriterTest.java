package ru.otus.writer;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.model.Creature;
import ru.otus.model.Human;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static ru.otus.model.value.Trait.JAVA_PROGRAMMING_SKILL;
import static ru.otus.model.value.Trait.USUAL_MOOD;

class JsonWriterTest implements WithAssertions {

    private static final String GSON_FILE_PATH = "gson.json";
    private static final String DIY_FILE_PATH = "diy.json";

    private Gson gson;
    private JsonWriter jsonWriter;

    private Creature testObject;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        jsonWriter = new JsonWriterImpl();

        testObject = new Human(asList(
                new Human(emptyList(), 1.45, new BigDecimal("44.4"), Map.of(USUAL_MOOD, "Grumpy")),
                new Human(emptyList(), 1.38, new BigDecimal("35.7"), Collections.emptyMap())
        ), 1.85, new BigDecimal("70.1"), Map.of(
                JAVA_PROGRAMMING_SKILL, "Extremely good"
        ));
    }

    @AfterEach
    @SneakyThrows
    void tearDown() {
        Files.deleteIfExists(Path.of(GSON_FILE_PATH));
        Files.deleteIfExists(Path.of(DIY_FILE_PATH));
    }

    @Test
    void shouldPassSerializeToString() {
        assertThat(jsonWriter.toJson(testObject))
                .isEqualTo(gson.toJson(testObject));
    }

    @Test
    void shouldPassSerializeToStringWithNullValues() {
        testObject.setSpecies(null);
        assertThat(jsonWriter.toJson(testObject))
                .isEqualTo(gson.toJson(testObject));
    }

    @Test
    @SneakyThrows
    void shouldPassSerializeToFile() {
        final Writer gsonWriter = new BufferedWriter(new FileWriter(GSON_FILE_PATH));
        gson.toJson(testObject, gsonWriter);
        gsonWriter.close();

        final Writer diyWriter = new BufferedWriter(new FileWriter(DIY_FILE_PATH));
        jsonWriter.toJson(testObject, diyWriter);
        diyWriter.close();

        assertThat(Files.readString(Path.of(GSON_FILE_PATH)))
                .isEqualTo(Files.readString(Path.of(DIY_FILE_PATH)));
    }

}
