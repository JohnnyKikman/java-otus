package ru.otus.cache;

public interface Cache<K, V> {

    void put(K key, V value);

    void remove(K key);

    V get(K key);

    int getHitCount();

    int getMissCount();

    void dispose();

    void addListener(CacheListener<K, V> listener);

    void removeListener(CacheListener<K, V> listener);
}
