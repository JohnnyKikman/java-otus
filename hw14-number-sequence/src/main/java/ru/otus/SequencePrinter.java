package ru.otus;

import lombok.SneakyThrows;

import java.util.function.Supplier;

import static java.lang.String.format;

class SequencePrinter {

    private static final String PRINT_TEMPLATE = "[%s] > %d";
    private static final int OVERFLOW_VALUE = 10;
    private final Object monitor = new Object();

    private int firstCounter;
    private int secondCounter;
    private boolean isFirstCounterStep = true;
    private boolean isFirstIncreasing = true;
    private boolean isSecondIncreasing = true;
    private Supplier<Boolean> isPrintingOver = () -> firstCounter < 0 && secondCounter < 0;

    @SneakyThrows
    void printSequence() {
        final Thread firstThread = new Thread(this::print);
        final Thread secondThread = new Thread(this::print);

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();
    }

    @SneakyThrows
    private void print() {
        while (true) {
            synchronized (monitor) {
                if (isPrintingOver.get()) {
                    monitor.notifyAll();
                    return;
                }

                final String threadName = Thread.currentThread().getName();
                final int numberToPrint;
                if (isFirstCounterStep) {
                    numberToPrint = (isFirstIncreasing ? firstCounter++ : firstCounter--);
                    if (firstCounter == OVERFLOW_VALUE) {
                        isFirstIncreasing = false;
                    }
                } else {
                    numberToPrint = (isSecondIncreasing ? secondCounter++ : secondCounter--);
                    if (secondCounter == OVERFLOW_VALUE) {
                        isSecondIncreasing = false;
                    }
                }
                System.out.println(format(PRINT_TEMPLATE, threadName, numberToPrint));

                monitor.notifyAll();
                isFirstCounterStep = !isFirstCounterStep;
                monitor.wait();
            }
        }
    }

}
