package ru.otus.cache;

import lombok.Getter;

@Getter
class CacheElement<T> {

    private final T value;
    private final long createdAt;
    private long accessedAt;

    CacheElement(T value) {
        this.value = value;

        final long currentTime = getCurrentTime();
        this.createdAt = currentTime;
        this.accessedAt = currentTime;
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    void access() {
        this.accessedAt = getCurrentTime();
    }

}
