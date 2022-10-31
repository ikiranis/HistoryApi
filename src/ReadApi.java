import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ReadApi extends Thread {
    private final String API_URL = "http://numbersapi.com/1942/year";

    @Override
    public void run() {
        System.out.printf(loadDataFromUrl());
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
}
