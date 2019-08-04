package ru.otus;

import com.sun.management.GarbageCollectionNotificationInfo;
import org.apache.commons.lang3.ArrayUtils;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;
import static java.lang.management.ManagementFactory.getGarbageCollectorMXBeans;
import static com.sun.management.GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;

/**
 * Used settings:
 * -Xms1024m
 * -Xmx1024m
 * -Xlog:gc=debug:file=hw05-gc/logs/gc-%p.log
 * -verbose:gc
 * -XX:+HeapDumpOnOutOfMemoryError
 * -XX:HeapDumpPath=hw05-gc/logs/dump
 *
 * Order of heap sizes:
 * 1. 512m
 * 2. 1024m
 * 3. 2048m
 *
 * Order of GCs:
 * 1. -XX:+UseSerialGC
 * 2. -XX:+UseParallelGC
 * 3. -XX:+UseG1GC
 * 4. -XX:+UnlockExperimentalVMOptions -XX:+UseZGC
 */
public class MainClass {

    private static final String MINOR = "minor";
    private static final String MAJOR = "major";
    private static final int ITERATION_SIZE = 20_000;
    private static final long LOGGING_INTERVAL_MS = 60_000;

    private static long minorCount = 0;
    private static long majorCount = 0;
    private static long minorDuration = 0;
    private static long majorDuration = 0;

    private static long totalMinorCount = 0;
    private static long totalMajorCount = 0;
    private static long totalMinorDuration = 0;
    private static long totalMajorDuration = 0;

    public static void main(String[] args) {
        System.out.println("Application started, pid: " + getRuntimeMXBean().getName());
        enableGcMonitoring();
        final long startTimeMillis = currentTimeMillis();

        final Timer logger = new Timer();
        logger.scheduleAtFixedRate(loggingTask(), LOGGING_INTERVAL_MS, LOGGING_INTERVAL_MS);

        try {
            runToOutOfMemory();
        } finally {
            logFinal();
            logger.cancel();
            final long runningTimeSeconds = (currentTimeMillis() - startTimeMillis) / 1_000;
            System.out.println("Application stopped after running for " + runningTimeSeconds + " s.");
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private static void runToOutOfMemory() {
        final Random random = new Random();
        String[] outer = new String[0];
        while (true) {
            final String[] inner = new String[ITERATION_SIZE];
            for (int i = 0; i < ITERATION_SIZE; i++) {
                inner[i] = String.valueOf(random.nextInt());
            }
            for (int i = 0; i < ITERATION_SIZE; i += 2) {
                inner[i] = null;
            }
            outer = ArrayUtils.addAll(outer, inner);
        }
    }

    private static void enableGcMonitoring() {
        getGarbageCollectorMXBeans().forEach(gcBean -> {
            final NotificationEmitter emitter = (NotificationEmitter) gcBean;
            final NotificationListener listener = listener();
            emitter.addNotificationListener(listener, null, null);
        });
    }

    private static NotificationListener listener() {
        return (notification, handback) -> {
            if (notification.getType().equals(GARBAGE_COLLECTION_NOTIFICATION)) {
                final GarbageCollectionNotificationInfo gcInfo = GarbageCollectionNotificationInfo
                        .from((CompositeData) notification.getUserData());
                final String action = gcInfo.getGcAction();
                final long duration = gcInfo.getGcInfo().getDuration();

                if (action.contains(MINOR)) {
                    minorCount++;
                    totalMinorCount++;
                    minorDuration += duration;
                    totalMinorDuration += duration;
                } else if (action.contains(MAJOR)) {
                    majorCount++;
                    totalMajorCount++;
                    majorDuration += duration;
                    totalMajorDuration += duration;
                }
            }
        };
    }

    private static TimerTask loggingTask() {
        return new TimerTask() {
            @Override
            public void run() {
                System.out.println(
                        "GC data: "
                                + minorCount + " minor collections in " + minorDuration + " ms, "
                                + majorCount + " major collections in " + majorDuration + " ms."
                );

                minorCount = 0;
                majorCount = 0;
                minorDuration = 0;
                majorDuration = 0;
            }
        };
    }

    private static void logFinal() {
                System.out.println(
                        "Enging GC data: "
                                + totalMinorCount + " minor collections in " + totalMinorDuration + " ms, "
                                + totalMajorCount + " major collections in " + totalMajorDuration + " ms, " +
                                + (totalMajorCount + totalMinorCount) + " total collections in "
                                + (totalMinorDuration + totalMajorDuration) + " ms"
                );
    }

}
