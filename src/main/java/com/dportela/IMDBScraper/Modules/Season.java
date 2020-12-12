package com.dportela.IMDBScraper.Modules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Season {
    final static private String SEASONS_URL = "https://www.imdb.com/title/%s/episodes?season=%d";
    private int number;
    private Map<Integer, Episode> episodes;

    public Season() {
        this.number = -1;
        this.episodes = new ConcurrentHashMap<>();
    }

    public void scrap(String imdbId, int requested_season){
        Document doc;
        try {
            doc = Jsoup.connect(buildSeasonString(imdbId, requested_season))
                    .header("Accept-Language", "en")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
                    .get();

            int fetched_season = Integer.parseInt(doc.getElementById("bySeason")
                                    .selectFirst("option[selected=selected]").attr("value"));

            // Sometimes the season's episode list does not correspond with the url -> Do not proceed!
            if (requested_season != fetched_season) {
                return;
            }

            number = fetched_season;

            episodes = doc.getElementsByClass("eplist").first().children()
                    .parallelStream()
                    .map( episode_soup -> {
                        Episode episode = new Episode();
                        episode.scrapData(episode_soup);
                        return episode;
                    })
                    .filter(Episode::isValid)
                    .collect(Collectors.toConcurrentMap(
                            Episode::getNumber,
                            episode -> episode
                    ));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not process Season " + requested_season + " of " + imdbId);
        }

    }

    public boolean isValid() {
        return number > 0 && !episodes.isEmpty();
    }

    private String buildSeasonString(String imdbID, int season_number) {
        return String.format(SEASONS_URL, imdbID, season_number);
    }

    public int getNumber() {
        return this.number;
    }

    @Override
    public String toString() {
        return "Season{" +
                "number=" + number +
                ", episodes=" + episodes +
                '}';
    }
}
