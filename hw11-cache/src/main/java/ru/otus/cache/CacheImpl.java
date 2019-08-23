package ru.otus.cache;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

public class CacheImpl<K, V> implements Cache<K, V> {

    private static final int TIME_THRESHOLD_MS = 5;
    private static final String PUT_ACTION = "put";
    private static final String GET_ACTION = "get";
    private static final String REMOVE_ACTION = "remove";

    private final int maxElements;
    private final long lifeTimeMs;
    private final long idleTimeMs;
    private final boolean isEternal;

    private int hit = 0;
    private int miss = 0;

    private final Timer timer = new Timer();
    private final Map<K, SoftReference<CacheElement<V>>> elements = new LinkedHashMap<>();
    private final Collection<CacheListener<K, V>> listeners = new ArrayList<>();

    CacheImpl(int maxElements, long lifeTimeMs, long idleTimeMs, boolean isEternal) {
        this.maxElements = maxElements;
        this.lifeTimeMs = lifeTimeMs > 0 ? lifeTimeMs : 0;
        this.idleTimeMs = idleTimeMs > 0 ? idleTimeMs : 0;
        this.isEternal = lifeTimeMs == 0 && idleTimeMs == 0 || isEternal;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void put(K key, V value) {
        if (elements.size() == maxElements) {
            final K leastUsed = elements.entrySet().stream()
                    .min(Comparator.comparingLong(
                            e -> e.getValue().get() != null ? e.getValue().get().getAccessedAt() : -1
                    )).map(Map.Entry::getKey)
                    .orElse(elements.keySet().iterator().next());
            elements.remove(leastUsed);
        }

        elements.put(key, new SoftReference<>(new CacheElement<>(value)));
        listeners.forEach(listener -> listener.notify(key, value, PUT_ACTION));

        if (!isEternal) {
            if (lifeTimeMs != 0) {
                final TimerTask lifeTimerTask = getTimerTask(key,
                        lifeElement -> lifeElement.getCreatedAt() + lifeTimeMs);
                timer.schedule(lifeTimerTask, lifeTimeMs);
            }
            if (idleTimeMs != 0) {
                final TimerTask idleTimerTask = getTimerTask(key,
                        idleElement -> idleElement.getAccessedAt() + lifeTimeMs);
                timer.schedule(idleTimerTask, idleTimeMs);
            }
        }
    }

    @Override
    public void remove(K key) {
        final SoftReference<CacheElement<V>> elementReference = elements.get(key);
        if (elementReference != null) {
            final CacheElement<V> element = elementReference.get();
            if (element != null) {
                elementReference.clear();
                listeners.forEach(listener -> listener.notify(key, element.getValue(), REMOVE_ACTION));
            }
            elements.remove(key);
        }
    }

    @Override
    public V get(K key) {
        final SoftReference<CacheElement<V>> elementReference = elements.get(key);
        if (elementReference == null) {
            miss++;
            return null;
        }

        final CacheElement<V> element = elementReference.get();
        if (element == null) {
            miss++;
            elements.remove(key);
            return null;
        } else {
            hit++;
            element.access();
            final V value = element.getValue();
            listeners.forEach(listener -> listener.notify(key, value, GET_ACTION));
            return value;
        }
    }

    @Override
    public int getHitCount() {
        return hit;
    }

    @Override
    public int getMissCount() {
        return miss;
    }

    @Override
    public void dispose() {
        timer.cancel();
    }

    @Override
    public void addListener(CacheListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(CacheListener<K, V> listener) {
        listeners.remove(listener);
    }

    private TimerTask getTimerTask(final K key, Function<CacheElement<V>, Long> timeFunction) {
        return new TimerTask() {
            @Override
            public void run() {
                final CacheElement<V> element = elements.get(key).get();
                if (element == null || isBefore(timeFunction.apply(element), System.currentTimeMillis())) {
                    elements.remove(key);
                    this.cancel();
                }
            }
        };
    }

    private boolean isBefore(long t1, long t2) {
        return t1 < t2 + TIME_THRESHOLD_MS;
    }
}
