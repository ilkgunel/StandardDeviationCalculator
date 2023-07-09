package com.ilkaygunel.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class StandardDeviationCalculationThread implements Runnable {

    public static Map<Long, Double> deviationCalculationResultMap = new HashMap<>();
    public static Map<Long, Double> sumOfIncomingNumbers = new HashMap<>();

    short[] numbersArray;
    int initialNumbersCount;

    public StandardDeviationCalculationThread(short[] numbers, int initialNumbersCount) {
        numbersArray = numbers;
        this.initialNumbersCount = initialNumbersCount;
    }

    @Override
    public void run() {

        double sumOfPowArray = 0;

        ReentrantLock lock = new ReentrantLock();
        if (lock.tryLock()) {
            try {
                double sumOfArray = 0.0;

                System.out.println(Thread.currentThread().getId() + " - Calculating Sum Of Incoming Numbers");

                for (short s : numbersArray) {
                    sumOfArray = sumOfArray + s;
                }

                System.out.println(Thread.currentThread().getId() + " - Sum Of Incoming Numbers:" + sumOfArray);

                synchronized (sumOfIncomingNumbers) {
                    sumOfIncomingNumbers.put(Thread.currentThread().getId(), sumOfArray);
                }


                System.out.println(Thread.currentThread().getId() + " - Waking Up From Wait");

                final double[] sumOfSums = new double[1];
                sumOfIncomingNumbers.entrySet().forEach(entry -> {
                    sumOfSums[0] = sumOfSums[0] + entry.getValue();
                });
                double average = (sumOfSums[0] / initialNumbersCount);

                System.out.println(Thread.currentThread().getId() + " - Average Of Incoming Numbers:" + average);

                int[] powArray = new int[numbersArray.length];

                for (int i = 0; i < numbersArray.length; i++) {
                    double substraction = ((double) numbersArray[i]) - average;
                    powArray[i] = (int) Math.pow(substraction, 2);
                }

                for (int i : powArray) {
                    sumOfPowArray = (sumOfPowArray + i);
                }

                System.out.println(Thread.currentThread().getId() + " - Sum Of Pow Array:" + sumOfPowArray);
                synchronized (deviationCalculationResultMap) {
                    deviationCalculationResultMap.put(Thread.currentThread().getId(), sumOfPowArray);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
