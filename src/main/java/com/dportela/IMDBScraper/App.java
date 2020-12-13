package com.dportela.IMDBScraper;

import com.dportela.IMDBScraper.Modules.TVSeries;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // Create scraper
        try {
            IMDBScraper scraper = new IMDBScraper();
            TVSeries series = scraper.scrap("how i met your mother");
            System.out.println(series);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
