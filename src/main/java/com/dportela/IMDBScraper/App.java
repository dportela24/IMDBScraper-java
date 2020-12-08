package com.dportela.IMDBScraper;

import com.dportela.IMDBScraper.Modules.TVSeries;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Document doc;
        String url = "https://www.imdb.com/title/tt0460649"; // Himym
        //String url = "https://www.imdb.com/title/tt6468322"; // Cdp

        try {
            // Fetch html
            doc = Jsoup.connect(url)
                    .header("Accept-Language", "en")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
                    .get();

            // Check if valid request
            String type = doc.selectFirst("meta[property=og:type]").attr("content");
            if (type.isEmpty() || !type.equals("video.tv_show"))
            {
                throw new IOException("TV Series not found...");
            }

            // Create tvSeries
            TVSeries tvSeries = new TVSeries();
            tvSeries.scrapData(doc);

            System.out.println(tvSeries);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
