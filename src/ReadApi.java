import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ReadApi extends Thread {
    private String year;
    private String API_URL;
    private int k;
    private Map<String, Integer> commonWords = new HashMap<String, Integer>();
    private Map<String, Integer> averageWordsInYear = new HashMap<String, Integer>();

    public ReadApi(String year, int k) {
        this.year = year;
        this.k = k;
        API_URL = "http://numbersapi.com/" + year + "/year";
    }

    public Map<String, Integer> getCommonWords() {
        return commonWords;
    }

    public Map<String, Integer> getAverageWordsInYear() {
        return averageWordsInYear;
    }

    @Override
    public void run() {
        for(int i=0;i<k;i++) {
            countWords(loadDataFromUrl());
        }

        calcAverageWordsInYear();

//        System.out.println(commonWords);
//        System.out.println(averageWordsInYear.get(year));
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

    private String[] getWordsInText(String text) {
        return Pattern.compile("\\b(?:\\w|-)+\\b")
                .matcher(text)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
    }

    private void addWordsCounterInYear(int counter) {
        if(averageWordsInYear.containsKey(year)) {
            int sum = averageWordsInYear.get(year) + counter;
            averageWordsInYear.put(year, sum);
        } else {
            averageWordsInYear.put(year, counter);
        }
    }

    private void calcAverageWordsInYear() {
        int average = averageWordsInYear.get(year) / k;

        averageWordsInYear.put(year, average);
    }

    private void countWords(String text) {
        String[] words = getWordsInText(text);

        for(String word : words) {
            word = word.toLowerCase();

            if(word.length()<4) {
                if(commonWords.containsKey(word)) {
                    int sum = commonWords.get(word) + 1;
                    commonWords.put(word, sum);
                } else {
                    commonWords.put(word, 1);
                }
            }
        }

        // Προσθήκη του πλήθους λέξεων στο έτος
        addWordsCounterInYear(words.length);
    }
}
