import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ReadApi extends Thread {
    private String year;        // Το έτος για το οποίο θα γίνουν οι κλήσεις
    private String API_URL;     // Το url στο οποίο θα γίνει η κλήση
    private int k;              // Ο αριθμός των κλήσεων
    private int averageWordsInYear;     // Ο μέσος όρος λέξεων που βρέθηκαν, ανα κλήση
    private int totalWords = 0;     // Το πλήθος των λέξεων που βρέθηκαν

    private Map<String, Integer> commonWords = new HashMap<String, Integer>();  // Οι λέξεις που βρέθηκαν

    public ReadApi(String year, int k) {
        this.year = year;
        this.k = k;
        API_URL = "http://numbersapi.com/" + year + "/year";    // Δημιουργία του url, με βάση το έτος
    }

    public Map<String, Integer> getCommonWords() {
        return commonWords;
    }

    public int getAverageWordsInYear() {
        return averageWordsInYear;
    }

    /**
     * Εκκίνηση thread
     */
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " started. Επεξεργασία " + k + " κλήσεων");

        // Κάνει k κλήσεις και μετράει τις λέξεις για την κάθε μία
        for(int i=0;i<k;i++) {
            countWords(loadDataFromUrl());
        }

        // Υπολογισμός του μέσου όρου
        calcAverageWordsInYear();

        System.out.println(Thread.currentThread().getName() + " finished");
    }

    /**
     * This method loads the data from url
     * Source: 1η εργασία ΠΛΗ47 (2021-2022), Θέμα 3
     *
     * @return the data as String
     */
    private String loadDataFromUrl() {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(API_URL);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
                result.append(" ");
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    /**
     * Παίρνει σε array τις λέξεις που βρίσκονται στο κείμενο
     *
     * @param text
     * @return
     */
    private String[] getWordsInText(String text) {
        // Χρήση regex, για να σπάσει το κείμενο σε λέξεις
        return Pattern.compile("\\b(?:\\w|-)+\\b")
                .matcher(text)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
    }

    /**
     * Υπολογισμός μέσου όρου λέξεων στο έτος, για k κλήσεις
     */
    private void calcAverageWordsInYear() {
        averageWordsInYear = totalWords / k;
    }

    /**
     * Μέτρηση λέξεων (μέχρι τις χαρακτήρες) στο κείμενο και προσθήκη τους σε hashmap
     *
     * @param text
     */
    private void countWords(String text) {
        String[] words = getWordsInText(text);

        for(String word : words) {
            word = word.toLowerCase();

            if(word.length()<4) {
                // Αν υπάρχει ήδη η λέξη, αυξάνει τον μετρητή της
                if(commonWords.containsKey(word)) {
                    int sum = commonWords.get(word) + 1;
                    commonWords.put(word, sum);
                } else { // Αλλιώς της προσθέτει σαν καινούργια
                    commonWords.put(word, 1);
                }
            }
        }

        // Πρόσθεση του πλήθους λέξεων στο έτος
        totalWords += words.length;
    }
}
