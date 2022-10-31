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
    private Map<String, Integer> wordsInYear = new HashMap<String, Integer>();

    public ReadApi(String year, int k) {
        this.year = year;
        this.k = k;
        API_URL = "http://numbersapi.com/" + year + "/year";
    }

    @Override
    public void run() {
        for(int i=0;i<k;i++) {
            countWords(loadDataFromUrl());
        }

//        System.out.println(commonWords);
//        System.out.println(wordsInYear.get(year));
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
        if(wordsInYear.containsKey(year)) {
            int sum = wordsInYear.get(year) + counter;
            wordsInYear.put(year, sum);
        } else {
            wordsInYear.put(year, counter);
        }
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

        addWordsCounterInYear(words.length);
    }
}
