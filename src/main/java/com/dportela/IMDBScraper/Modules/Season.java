package com.dportela.IMDBScraper.Modules;

import com.dportela.IMDBScraper.Exceptions.ErrorProcessingEpisodeException;
import com.dportela.IMDBScraper.Exceptions.ErrorProcessingSeasonException;
import lombok.Getter;
import lombok.ToString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @ToString
public class Season {
    final static private String SEASONS_URL = "https://www.imdb.com/title/%s/episodes?season=%d";
    private int number;
    private List<Episode> episodes;

    public Season() {
        this.number = -1;
        this.episodes = new ArrayList<>();
    }

    public void scrap(String imdbId, int requested_season) throws ErrorProcessingSeasonException {
        try {
            Document doc;

            // Fetch season html
            doc = Jsoup.connect(buildSeasonString(imdbId, requested_season))
                    .header("Accept-Language", "en")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
                    .maxBodySize(0).timeout(0)
                    .get();

            // Check which season was fetched
            int fetched_season = Integer.parseInt(doc.getElementById("bySeason")
                                    .selectFirst("option[selected=selected]").attr("value"));

            // Sometimes the season's episode list does not correspond with the url -> Do not proceed!
            if (requested_season != fetched_season) {
                throw new Exception("Requested series number " + requested_season + " but fetched number " + fetched_season);
            }

            number = fetched_season;

            // Map each html episode entry to an episode class, filter by validation and collect as map
            episodes = doc.getElementsByClass("eplist").first().children()
                    .parallelStream()
                    .map( episode_soup -> {
                        return scrapEpisode(episode_soup);
                    })
                    .filter(Episode::isValid)
                    .collect(Collectors.toList());
        } catch (IOException e) { //Jsoup exception
            throw new ErrorProcessingSeasonException("Could not fetch season number " + requested_season);
        } catch (Exception e) {
            throw new ErrorProcessingSeasonException("Error parsing season " + requested_season + "." + e.getMessage());
        }

    }

    private Episode scrapEpisode(Element episode_soup) throws ErrorProcessingEpisodeException{
        Episode episode = new Episode();
        episode.scrapData(episode_soup);

        return episode;
    }
    protected boolean isValid() {
        return number > 0 && !episodes.isEmpty();
    }

    private String buildSeasonString(String imdbID, int season_number) {
        return String.format(SEASONS_URL, imdbID, season_number);
    }

    public int getNumber() {
        return this.number;
    }
}
