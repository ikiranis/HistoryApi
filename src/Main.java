import java.util.*;

public class Main {
    private static final int maxThreads = 3;    // Μέγιστος αριθμός threads, σε δυνάμεις του 2
    private static ReadApi[] processes;         // Τα processes που θα τρέξουν
    private static Random random = new Random();

    private static Map<String, Integer> commonWords;        // Οι λέξεις που βρέθηκαν με τη συχνότητα τους
    private static Map<String, Integer> averageWordsInYear;     // Μέσος όρος λέξεων ανα έτος
    private static ArrayList<String> years = new ArrayList<>();     // Η λίστα με τα έτη, στα οποία θα γίνουν οι κλήσεις

    private static int k;   // Το πλήθος των κλήσεων
    private static int n;   // Το πλήθος των ετών

    /**
     * Εκκίνηση όλων των threads, περνώντας τις αντίστοιχες παραμέτρους δεδομένων σε κάθε ένα
     *
     * @param year
     */
    private static void startThreads(String year) {
        for(int i=0; i<processes.length; i++) {
            int batchSize = k / processes.length; // Κλήσεις που θα γίνουν ανα thread

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

    /**
     * Προσθήκη ενός επιμέρους hashmap στο γενικό
     *
     * @param newData
     * @param data
     */
    private static void addNewProcessData(Map<String, Integer> newData, Map<String, Integer> data) {
        // Διαβάζει το κάθε στοιχείο του νέου hashmap
        newData.entrySet()
                .forEach(x -> {
                    // Αν το key υπάρχει ήδη στο γενικό hashmap
                    if(data.containsKey(x.getKey())) {
                        // Υπολογισμός της νέας τιμής, προσθέτοντας την νεά στην παλιά
                        int newValue = data.get(x.getKey()) + x.getValue();

                        data.put(x.getKey(), newValue);
                    } else { // Αν το key δεν υπάρχει προσθέτουμε νέα εγγραφή
                        data.put(x.getKey(), x.getValue());
                    }
                });
    }

    /**
     * Προσθήκη των δεδομένων κάθε thread στα γενικά, για το συγκεκριμένο έτος
     *
     * @param year
     */
    private static void addNewDataFromProcesses(String year) {
        // Αρχικοποίηση hashmap
        commonWords = new HashMap<>();

        // Μετράει συνολικά τους μέσους όρους από όλα τα threads και στο τέλος βρίσκει τον μέσο όρο από αυτά
        int sum = 0;

        // Διάβασμα των δεδομένων του κάθε thread
        for (ReadApi process: processes) {
            addNewProcessData(process.getCommonWords(), commonWords);
            sum += process.getAverageWordsInYear();
        }

        // Μέσος όρος λέξεων από όλες τις κλήσεις και προσθήκη του στο συγκεκριμένο έτος
        averageWordsInYear.put(year, (sum / processes.length));
    }

    /**
     * Δημιουργία της λίστας με τα έτη, για τα οποία θα γίνουν οι κλήσεις
     */
    private static void generateYears() {
        for(int i=0;i<n;i++) {
            int year;

            // Εύρεση τυχαίου έτους, με αποφυγή της εισαγωγής διπλού έτους
            while (true) {
                year = random.nextInt(2023 - 1900) + 1900; // Τυχαίο έτος, στο εύρος 1900-2022

                // Αν δεν υπάρχει ήδη το έτος, το προσθέτει
                if(!years.contains(String.valueOf(year))) {
                    years.add(String.valueOf(year));
                    break;
                }
            }
        }

        System.out.println("\nΘα γίνουν κλήσεις για τα έτη: " + years);
    }

    /**
     * Εκτύπωση συχνότητας της κάθε λέξης στο έτος
     */
    private static void printWordsInYear() {
        System.out.println("\nΣυχνότητα λέξεων στο έτος");
        System.out.println("--------------------------");
        for(Map.Entry<String, Integer> word : commonWords.entrySet()) {
            System.out.printf("Η λέξη \"%s\", βρέθηκε %s φορές\n", word.getKey(), word.getValue());
        }
    }

    /**
     * Εκτύπωση του έτους με τον μεγαλύτερο μέσο όρο λέξεων
     */
    private static void printYearWithBiggerAverage() {
        System.out.println("-------------------------------------------------------------");

        // Κάνει ταξινόμηση και παίρνει το μεγαλύτερο
        averageWordsInYear.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(1)
                .forEachOrdered(year ->
                        System.out.printf("To έτος \"%s\", έχει τον μεγαλύτερο μέσο όρο λέξεων με %s\n", year.getKey(), year.getValue()));
    }

    /**
     * Εισαγωγή δεδομένων από τον χρήστη
     */
    private static void inputData() {
        Scanner scanner = new Scanner(System.in);

        // Μέγιστος αριθμός threads που θα φτάσουν να εκτελεστούν
        int maxThreadsNumber = (int) Math.pow(2, maxThreads);

        // Εισαγωγή αριθμού k, με αμυντικό προγραμματισμό, για να μην δοθεί k μικρότερο του maxThreadsNumber
        do {
            System.out.print("\nΔώσε αριθμό κλήσεων (k): ");
            k = scanner.nextInt();

            if(k < 8) {
                System.out.println("Ο αριθμός κλήσεων πρέπει να είναι μεγαλύτερος ή ίσος από τα " + maxThreadsNumber + " threads");
            }
        } while (k < maxThreadsNumber);

        System.out.print("\nΔώσε αριθμό ετών (n): ");
        n = scanner.nextInt();
    }

    public static void main(String[] args) {
        inputData();

        generateYears();

        // Δοκιμή επεξεργασίας με διαφορετικό πλήθος threads
        for (int i=0; i<=maxThreads; i++) {
            int threadsNumber = (int) Math.pow(2, i);  // Πλήθος threads σε δυνάμεις του 2

            // Αρχικοποίηση του array των threads με την κλάση HammingCalculator
            processes = new ReadApi[threadsNumber];

            // Αρχικοποίηση του hashmap με ταν μέσο όρο λέξεων, ανά έτος
            averageWordsInYear = new HashMap<>();

            // Κάνει κλήσεις για κάθε ένα έτος
            for(String year : years) {
                // Αρχικοποίηση του χρόνου που αρχίζει η επεξεργασία
                long start = System.currentTimeMillis();

                System.out.println("\n==================================================================");
                System.out.println("Επεξεργασία " + k + " κλήσεων, για το έτος " + year + ", με "
                        + threadsNumber
                        + ((threadsNumber>1) ? " threads" : " thread")
                        + "\n");

                startThreads(year);

                waitThreads();

                // Τερματισμός του χρόνου επεξεργασίας
                long end = System.currentTimeMillis();

                addNewDataFromProcesses(year);

                printWordsInYear();

                System.out.println("\nΧρονική διάρκεια επεξεργασίας: " + (end - start) + "msec");
            }

            printYearWithBiggerAverage();
        }

    }

}