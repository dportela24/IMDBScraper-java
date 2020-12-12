package com.dportela.IMDBScraper;

import com.dportela.IMDBScraper.Modules.TVSeries;
import org.jsoup.nodes.Document;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Document doc;

        // Create tvSeries
        TVSeries tvSeries = new TVSeries("tt11192306");
        tvSeries.scrap();

        System.out.println(tvSeries);
    }
}
