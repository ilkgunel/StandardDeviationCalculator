import com.ilkaygunel.thread.StandardDeviationCalculationThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        short[] sayilar = {10, 8, 10, 8, 8, 4};
        System.out.println("Calculated Standard Deviation: " + main.standartSapmaHesapla(sayilar, (short) 6));
    }

    public synchronized double standartSapmaHesapla(short[] sayilar, short threadSayisi) {
        List<Thread> threadList = new ArrayList<>();
        int subArrayIndexCounter = 0;
        int startIndexCounter = 0;
        short[] leftNumbers = null;
        if ((sayilar.length % threadSayisi) == 0) { //The array can be distributed to threads equally.
            subArrayIndexCounter = sayilar.length / threadSayisi;
        } else {
            int leftNumberCount = (sayilar.length % threadSayisi);
            subArrayIndexCounter = (sayilar.length - leftNumberCount) / threadSayisi;
            leftNumbers = Arrays.copyOfRange(sayilar, sayilar.length - leftNumberCount, sayilar.length);
        }

        for (int i = 1; i <= threadSayisi; i++) {
            short[] subArray = Arrays.copyOfRange(sayilar, startIndexCounter, startIndexCounter + subArrayIndexCounter);

            if (i == threadSayisi && leftNumbers != null) {
                subArray = Arrays.copyOf(subArray, subArray.length + leftNumbers.length);
                System.arraycopy(leftNumbers, 0, subArray, subArray.length - leftNumbers.length, subArray.length - 1);

            }

            StandardDeviationCalculationThread standardDeviationCalculationThread = new StandardDeviationCalculationThread(subArray, sayilar.length);
            Thread thread = new Thread(standardDeviationCalculationThread);
            threadList.add(thread);
            startIndexCounter = startIndexCounter + subArrayIndexCounter;
        }

        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            for (Thread thread : threadList) {
                executorService.execute(thread);
            }
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        final double[] sumOfCalculatedSums = {0};
        StandardDeviationCalculationThread.deviationCalculationResultMap.entrySet().stream().forEach(entry -> {
            sumOfCalculatedSums[0] = sumOfCalculatedSums[0] + entry.getValue();
        });

        double divisonResult = (double) (sumOfCalculatedSums[0] / (sayilar.length - 1));

        return Math.sqrt(divisonResult);

    }
}