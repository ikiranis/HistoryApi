import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    private static final int maxThreads = 2;    // Μέγιστος αριθμός threads, σε δυνάμεις του 2
    private static ReadApi[] processes;
    private static Random random = new Random();

    private static Map<String, Integer> commonWords;
    private static Map<String, Integer> averageWordsInYear;

    /**
     * Εκκίνηση όλων των threads, περνώντας τις αντίστοιχες παραμέτρους δεδομένων σε κάθε ένα
     *
     * @param year
     * @param k
     */
    private static void startThreads(String year, int k) {
        int processesLength = processes.length;

        if(k % processes.length != 0) {
            processesLength = processes.length - 1;
        }

        for(int i=0; i<processesLength; i++) {
            processes[i] = new ReadApi(year, k / processes.length);
            processes[i].start();
        }

        if(processesLength != processes.length) {
            processes[processesLength] = new ReadApi(year, (k / processes.length) + 1);
            processes[processesLength].start();
        }
    }

    /**
     * Αναμονή από το thread της main, για να τερματίσουν όλα τα threads
     */
    private static void waitThreads() {
        for (ReadApi process: processes) {
            try {
                process.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addNewProcessData(Map<String, Integer> newData, Map<String, Integer> data) {
        newData.entrySet()
                .forEach(x -> {
                    if(data.containsKey(x.getKey())) {
                        int newValue = data.get(x.getKey()) + x.getValue();

                        data.put(x.getKey(), newValue);
                    } else {
                        data.put(x.getKey(), x.getValue());
                    }
                });
    }

    private static void addNewDataFromProcesses() {
        // Αρχικοποίηση hashmaps
        commonWords = new HashMap<>();
        averageWordsInYear = new HashMap<>();

        for (ReadApi process: processes) {
            addNewProcessData(process.getCommonWords(), commonWords);
        }
    }

    public static void main(String[] args) {
        int max = (int) Math.pow(2, maxThreads) * 10;
        int min = (int) Math.pow(2, maxThreads) * 2;
        int k = random.nextInt(max + 1 - min) + min;

        // Δοκιμή επεξεργασίας με διαφορετικό πλήθος threads
        for (int i=0; i<=maxThreads; i++) {
            int threadsNumber = (int) Math.pow(2, i);  // Πλήθος threads σε δυνάμεις του 2

            // Αρχικοποίηση του array των threads με την κλάση HammingCalculator
            processes = new ReadApi[threadsNumber];

            System.out.println("\n==================================================================");
            System.out.println("Επεξεργασία " + k + " κλήσεων, με "
                    + threadsNumber
                    + ((threadsNumber>1) ? " threads" : " thread")
                    + "\n");

            // Αρχικοποίηση του χρόνου που αρχίζει η επεξεργασία
            long start = System.currentTimeMillis();

            startThreads("1942", k);

            waitThreads();

            // Τερματισμός του χρόνου επεξεργασίας
            long end = System.currentTimeMillis();

            addNewDataFromProcesses();

            System.out.println(commonWords);

            System.out.println("\nΧρονική διάρκεια επεξεργασίας: " + (end - start) + "msec");
        }


    }

}