import java.util.*;

public class Main {
    private static final int maxThreads = 3;    // Μέγιστος αριθμός threads, σε δυνάμεις του 2
    private static ReadApi[] processes;
    private static Random random = new Random();

    private static Map<String, Integer> commonWords;
    private static Map<String, Integer> averageWordsInYear;
    private static ArrayList<String> years = new ArrayList<>();

    /**
     * Εκκίνηση όλων των threads, περνώντας τις αντίστοιχες παραμέτρους δεδομένων σε κάθε ένα
     *
     * @param year
     * @param k
     */
    private static void startThreads(String year, int k) {
        for(int i=0; i<processes.length; i++) {
            int batchSize = k / processes.length;

            // Στο τελευταίο thread αν έχουμε υπόλοιπο στο k/processes.length
            // (δηλαδή έχουμε μονό αριθμό κλήσεων) προστίθεται το υπόλοιπο (δηλαδή το 1)
            if(i == processes.length - 1) {
                batchSize = batchSize + (k % processes.length);
            }

            processes[i] = new ReadApi(year, batchSize);
            processes[i].start();
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

    private static void addNewDataFromProcesses(String year) {
        // Αρχικοποίηση hashmaps
        commonWords = new HashMap<>();

        int sum = 0;

        for (ReadApi process: processes) {
            addNewProcessData(process.getCommonWords(), commonWords);
            sum += process.getAverageWordsInYear();
        }

        averageWordsInYear.put(year, (sum / processes.length));
    }

    private static void generateYears() {
        int numberOfYears = random.nextInt(5) + 1;

        for(int i=0;i<numberOfYears;i++) {
            int year = random.nextInt(2023 - 1900) + 1900;

            if(!years.contains(String.valueOf(year))) {
                years.add(String.valueOf(year));
            }
        }

        System.out.println(years);
    }

    private static void printWordsInYear() {
        System.out.println("\nΣυχνότητα λέξεων στο έτος");
        System.out.println("--------------------------");
        for(Map.Entry<String, Integer> word : commonWords.entrySet()) {
            System.out.printf("Η λέξη \"%s\", βρέθηκε %s φορές\n", word.getKey(), word.getValue());
        }
    }

    private static void printYearWithBiggerAverage() {
        System.out.println("-------------------------------------------------------------");
        averageWordsInYear.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(1)
                .forEachOrdered(year ->
                        System.out.printf("To έτος \"%s\", έχει τον μεγαλύτερο μέσο όρο λέξεων με %s\n", year.getKey(), year.getValue()));
    }

    public static void main(String[] args) {
        int max = (int) Math.pow(2, maxThreads) * 10;
        int min = (int) Math.pow(2, maxThreads) * 2;
        int k = random.nextInt(max + 1 - min) + min;

        generateYears();

        // Δοκιμή επεξεργασίας με διαφορετικό πλήθος threads
        for (int i=0; i<=maxThreads; i++) {
            int threadsNumber = (int) Math.pow(2, i);  // Πλήθος threads σε δυνάμεις του 2

            // Αρχικοποίηση του array των threads με την κλάση HammingCalculator
            processes = new ReadApi[threadsNumber];

            averageWordsInYear = new HashMap<>();

            for(String year : years) {
                // Αρχικοποίηση του χρόνου που αρχίζει η επεξεργασία
                long start = System.currentTimeMillis();

                System.out.println("\n==================================================================");
                System.out.println("Επεξεργασία " + k + " κλήσεων, για το έτος " + year + ", με "
                        + threadsNumber
                        + ((threadsNumber>1) ? " threads" : " thread")
                        + "\n");

                startThreads(year, k);

                waitThreads();

                // Τερματισμός του χρόνου επεξεργασίας
                long end = System.currentTimeMillis();

                addNewDataFromProcesses(year);

                printWordsInYear();

                System.out.println("\nΧρονική διάρκεια επεξεργασίας: " + (end - start) + "msec");
            }

            System.out.println(averageWordsInYear);
            printYearWithBiggerAverage();

        }


    }

}