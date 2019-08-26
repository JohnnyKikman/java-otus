package ru.otus.cache;

import java.util.Optional;

public interface Cache<K, V> {

    void put(K key, V value);

    void remove(K key);

    Optional<V> get(K key);

    int getHitCount();

    int getMissCount();

    void dispose();

    void addListener(CacheListener<K, V> listener);

    void removeListener(CacheListener<K, V> listener);
}
