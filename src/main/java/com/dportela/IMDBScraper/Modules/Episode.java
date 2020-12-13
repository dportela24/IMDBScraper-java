package com.dportela.IMDBScraper.Modules;

import lombok.Getter;
import lombok.ToString;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter @ToString
public class Episode {
    private int number;
    private String imdbID;
    private String name;
    private String airdate;
    private double ratingValue;
    private int ratingCount;
    private String summary;

    public Episode() {
        number = -1;
    }

    public void scrapData(Element episode_soup) {
        // Fetch rating value and count elements
        Element ratingValue_ele = episode_soup.getElementsByClass("ipl-rating-star__rating").first();
        Element ratingCount_ele = episode_soup.getElementsByClass("ipl-rating-star__total-votes").first();

        // If no rating episode, episode already entry exists but there's no useful information -> Do not proceed
        if (ratingCount_ele == null || ratingValue_ele == null) {
            return;
        }

        // Fetch imdbID
        String episode_url = episode_soup.selectFirst("a[itemprop=name]").attr("href");
        Pattern id_pattern = Pattern.compile("tt\\d+");
        Matcher id_matcher = id_pattern.matcher(episode_url);
        imdbID = id_matcher.find() ? id_matcher.group() : "";

        // Fetch number
        number = Integer.parseInt(episode_soup.selectFirst("meta[itemprop=episodeNumber]").attr("content"));

        // Fetch name
        name = episode_soup.selectFirst("a[itemprop=name]").ownText();

        // Fetch rating value
        ratingValue = Double.parseDouble(ratingValue_ele.ownText());

        // Fetch rating count
        ratingCount = Integer.parseInt(ratingCount_ele.ownText().substring(1, ratingCount_ele.ownText().length() - 1).replace(",", ""));

        // Fetch air date
        airdate = episode_soup.getElementsByClass("airdate").first().ownText();

        // Fetch summary
        summary = episode_soup.getElementsByClass("item_description").first().ownText();
    }

    protected boolean isValid() {
        return number != -1;
    }

    public int getNumber() {
        return this.number;
    }
}
