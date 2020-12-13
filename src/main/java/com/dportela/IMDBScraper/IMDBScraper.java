package com.dportela.IMDBScraper;

import com.dportela.IMDBScraper.Exceptions.SeriesIdNotFoundException;
import com.dportela.IMDBScraper.Exceptions.TVSeriesNotFoundException;
import com.dportela.IMDBScraper.Modules.TVSeries;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IMDBScraper {
    final static private String search_url = "https://www.imdb.com/find?q=%s";
    public IMDBScraper(){}

    public TVSeries scrap(String request) throws SeriesIdNotFoundException, TVSeriesNotFoundException {
        // Check if request is for id or name
        boolean isId = request.matches("tt\\d+");
        String series_id;

        // Get id directly from request or through the name
        if (isId) {
            series_id = request;
        } else {
           series_id = getIdFromName(request);
        }

        // Create tvSeries
        TVSeries tvSeries = new TVSeries(series_id);
        tvSeries.scrap();

        return tvSeries;
    }

    public String getIdFromName(String name) throws SeriesIdNotFoundException {
        // Create query conforming string
        name = name.replace(" ", "+");

        String imdbID = "";

        try {
            // Fetch html
            Document doc = Jsoup.connect(getSearchURL(name))
                    .header("Accept-Language", "en")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
                    .get();

            // Fetch first result match
            Element first_match = doc.getElementsByClass("result_text").first();
            String first_match_url = first_match.selectFirst("a").attr("href");

            // Extract id from match url
            Pattern id_pattern = Pattern.compile("tt\\d+");
            Matcher id_matcher = id_pattern.matcher(first_match_url);
            imdbID = id_matcher.find() ? id_matcher.group() : "";

        } catch (IOException e) { //JSoup error
            throw new SeriesIdNotFoundException("Could not perform search request");
        } catch (Exception e) {
            throw new SeriesIdNotFoundException();
        }

        return imdbID;
    }

    private String getSearchURL(String name) {
        return String.format(search_url, name);
    }
}
