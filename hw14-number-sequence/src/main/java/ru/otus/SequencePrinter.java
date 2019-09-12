package ru.otus;

import lombok.SneakyThrows;

class SequencePrinter {

    private static final int OVERFLOW_VALUE = 10;
    private final Object monitor = new Object();

    private int firstCounter;
    private int secondCounter;
    private boolean isFirstCounterStep = true;
    private boolean isFirstIncreasing = true;
    private boolean isSecondIncreasing = true;

    @SneakyThrows
    void printSequence() {
        final Thread firstThread = new Thread(this::print);
        final Thread secondThread = new Thread(this::print);

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();
    }

    private void print() {
        while (true) {
            synchronized (monitor) {
                if (firstCounter < 0 && secondCounter < 0) {
                    return;
                }

                if (isFirstCounterStep) {
                    System.out.print((isFirstIncreasing ? firstCounter++ : firstCounter--) + " ");
                    if (firstCounter == OVERFLOW_VALUE) {
                        isFirstIncreasing = false;
                    }
                } else {
                    System.out.print((isSecondIncreasing ? secondCounter++ : secondCounter--) + " ");
                    if (secondCounter == OVERFLOW_VALUE) {
                        isSecondIncreasing = false;
                    }
                }

                isFirstCounterStep = !isFirstCounterStep;
            }
        }
    }

}
