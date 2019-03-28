package sample;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;

class Service {

    Locale locCountry;
    final String country;
    Currency curr;
    String currComparison;

    public Service(String country) throws Exception {
        this.country = country;
        boolean found = false;

        int index = 0;
        Locale[] lcl = Locale.getAvailableLocales();
        for (int i = 0; i < lcl.length; i++) {
            if (lcl[i].getDisplayCountry(Locale.ENGLISH).equals(country)) {
                index = i;
                found = true;
            }
        }
        if (!found) {
            throw new Exception("Haven't found given country");
        }

        locCountry = lcl[index];
        curr = Currency.getInstance(locCountry);
        System.out.println(locCountry.getDisplayCountry(Locale.ENGLISH) + " " + curr.getDisplayName());
    }

    public String getWeather(String miasto) {
        String link = "http://api.openweathermap.org/data/2.5/weather?q=" + miasto + "," + locCountry.getISO3Country() +
                "&appid=0c0a608bb739e0d24c9e02dee048b0c6" + "&units=metric";

        return stringFromLink(link);
    }

    public Double getRateFor(String currencyCode) {

        currComparison = currencyCode;
        String link = "https://api.exchangeratesapi.io/latest?base=" + curr.getSymbol(Locale.ENGLISH) + "&symbols=" + currencyCode;

        String response = stringFromLink(link);
        //Parsing response to JSONObject and then parsing to rate
        if("0.0".equals(response))
            return 0.0;
        String rate;
        double rateDouble = 0;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jObject = (JSONObject) jsonParser.parse(response);
            rate = jObject.values().toArray()[1].toString();
            int beginRate = rate.indexOf(":");
            int endRate = rate.indexOf("}");
            rateDouble = Double.parseDouble(rate.substring(beginRate + 1, endRate));
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
//            in.close();

        //print result
        System.out.println(response);
        System.out.println(rateDouble);
        return rateDouble;
    }

    private String stringFromLink(String link) {
        StringBuilder response = new StringBuilder();
        try {
            URL urlLink = new URL(link);
            HttpURLConnection urlConnection = (HttpURLConnection) urlLink.openConnection();
            urlConnection.setRequestMethod("GET");
            String USER_AGENT = "Mozilla/5.0";
            urlConnection.setRequestProperty("USER_AGENT", USER_AGENT);

            int responseCode = urlConnection.getResponseCode();
            if(responseCode==400)
                return "0.0";
            System.out.println("\nSending 'GET' request to URL : " + urlLink);
            System.out.println("Response Code : " + responseCode);

            //Reading response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

//            System.out.println(response.toString());
        }catch (FileNotFoundException fnfe){return "Brak takiego miasta";

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    Double getNBPRate() {

        if (curr.getDisplayName().equals(Currency.getInstance("PLN").getDisplayName()))
            return 1.0;

        String html = "http://www.nbp.pl/kursy/kursya.html";
        String htmlb = "http://www.nbp.pl/kursy/kursyb.html";
        Double tt;
        if ((tt = findNBPRates(html)) != null)
            return tt;
        else if ((tt = findNBPRates(htmlb)) != null)
            return tt;
        else return 0.0;
    }

    private Double findNBPRates(String link) {
        try {
            Document doc = Jsoup.connect(link).get();
            Elements tableElements = doc.select("table");
            System.out.println(tableElements.size());

            Elements tableRowElements = tableElements.get(3).select(":not(thead) tr");

            boolean found = false;
            for (int i = 0; i < tableRowElements.size() && !found; i++) {
                Element row = tableRowElements.get(i);
                Elements rowItems = row.select("td");

                if (rowItems.size() > 0 && rowItems.get(1).text().contains(curr.getCurrencyCode())) {
                    found = true;
                    for (Element rowItem : rowItems) System.out.println(rowItem.text());

                    String val = rowItems.get(2).text().replace(",", ".");
                    return Double.parseDouble(val);

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}