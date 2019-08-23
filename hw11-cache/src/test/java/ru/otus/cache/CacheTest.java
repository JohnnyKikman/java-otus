package ru.otus.cache;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Cache")
class CacheTest implements WithAssertions {

    private Cache<Integer, String> cache;

    @Test
    @DisplayName("Should remove elements on max size for eternal cache")
    void shouldRemoveElementsOnMaxSizeForEternalCache() {
        cache = new CacheImpl<>(5, 0, 0, true);

        for (int i = 0; i < 10; i++) {
            cache.put(i, "String " + i);
        }

        for (int i = 0; i < 5; i++) {
            assertThat(cache.get(i)).isNull();
        }

        for (int i = 5; i < 10; i++) {
            assertThat(cache.get(i)).isEqualTo("String " + i);
        }

        assertEquals(5, cache.getHitCount());
        assertEquals(5, cache.getMissCount());
    }

    @Test
    @DisplayName("Should remove elements on life timeout")
    void shouldRemoveElementsOnLifeTimeout() throws InterruptedException {
        cache = new CacheImpl<>(5, 2000, 0, false);

        for (int i = 0; i < 5; i++) {
            cache.put(i, "String " + i);
        }

        Thread.sleep(3000);

        for (int i = 0; i < 5; i++) {
            assertThat(cache.get(i)).isNull();
        }
    }

    @Test
    @DisplayName("Should remove elements on idle timeout")
    void shouldRemoveElementsOnIdleTimeout() throws InterruptedException {
        cache = new CacheImpl<>(5, 0, 5000, false);

        for (int i = 0; i < 5; i++) {
            cache.put(i, "String " + i);
        }

        Thread.sleep(3000);

        for (int i = 0; i < 5; i++) {
            assertThat(cache.get(i)).isNotNull();
        }

        Thread.sleep(6000);

        for (int i = 0; i < 5; i++) {
            assertThat(cache.get(i)).isNull();
        }
    }

}
