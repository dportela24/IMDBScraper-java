package com.dportela.IMDBScraper.Modules;

import com.dportela.IMDBScraper.Exceptions.TVSeriesNotFoundException;
import lombok.Getter;
import lombok.ToString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter @ToString
public class TVSeries {
    final static private String url = "https://www.imdb.com/title/%s";
    private String imdbID;
    private String name;
    private String originalName;
    private String summary;
    private String episodeDuration;
    private String runtime;
    private List<String> genres;
    private double ratingValue;
    private int ratingCount;
    private String poster;
    private int numberOfSeasons;
    private List<Season> seasons;

    public TVSeries(String imdbID) {
        this.imdbID = imdbID;
    }

    public void scrap() throws TVSeriesNotFoundException {
        try {
            // Fetch html
            Document doc = Jsoup.connect(getURL())
                    .header("Accept-Language", "en")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
                    .get();

            // Check if requested item is a tv series
            String type = doc.selectFirst("meta[property=og:type]").attr("content");
            if (type.isEmpty() || !type.equals("video.tv_show")) {
                throw new TVSeriesNotFoundException();
            }

            scrapSeriesData(doc);
            scrapSeasons();
        } catch (IOException e) { //JSoup exception
            throw new TVSeriesNotFoundException("Could not fetch series html");
        } catch (TVSeriesNotFoundException e) {
            throw new TVSeriesNotFoundException();
        }
    }

    private void scrapSeriesData(Document doc) {
        // Scrap imdbID
        imdbID = doc.selectFirst("meta[property=pageId]").attr("content");

        Element titleBlock = doc.getElementsByClass("title_block").first();

        // Scrap title
        name = titleBlock.selectFirst("h1").text();

        // Scrap original title
        Elements originalNameElements = titleBlock.getElementsByClass("originalTitle");
        originalName = originalNameElements.isEmpty() ? name : originalNameElements.first().text();

        // Scrap summary
        Elements summaryElements = doc.getElementsByClass("summary_text");
        summary = !summaryElements.isEmpty() ? summaryElements.first().text() : "";

        // Scrap episode duration
        Element episodeDurationElement = titleBlock.selectFirst("time");
        episodeDuration = episodeDurationElement != null ? episodeDurationElement.text() : "";

        // Scrap runtime
        Element runtimeElement = titleBlock.selectFirst("a[title=\"See more release dates\"]");
        String runtimeInfo = runtimeElement != null ? runtimeElement.text() : "";
        Pattern pattern = Pattern.compile("\\d+–?(\\d+)?");
        Matcher matcher = pattern.matcher(runtimeInfo);
        runtime = matcher.find() ? matcher.group() : "";

        // Scrap genres
        //genres = titleBlock.getElementsByClass("subtext").first().children()
        //        .stream()
        //        .filter(element -> element.hasAttr("href") && element.attr("href").contains("genres"))
        //        .map(Element::text)
        //        .collect(Collectors.toList());

        // Scrap rating value
        Element ratingValueElement = titleBlock.selectFirst("span[itemprop=ratingValue]");
        String ratingValueStr =  ratingValueElement != null ? ratingValueElement.text() : "";
        if (ratingValueStr.isEmpty()) {
            ratingValue = -1;
        } else {
            ratingValue = Double.parseDouble(ratingValueStr);

        }

        // Scrap rating count
        Element ratingCountElement = titleBlock.selectFirst("span[itemprop=ratingCount]");
        String ratingCountStr = ratingCountElement != null ?  ratingCountElement.text() : "";
        if (ratingCountStr.isEmpty()) {
            ratingCount = -1;
        } else {
            ratingCount = Integer.parseInt(ratingCountStr.replace(",", ""));
        }

        // Scrap poster
        Element posterElement = doc.getElementsByClass("poster").first();
        posterElement = posterElement != null ? posterElement.selectFirst("img") : null;
        poster = posterElement.attr("src");

        // Scrap number of seasons
        Element seasonsElement = doc.selectFirst(".seasons-and-year-nav");
        seasonsElement = seasonsElement != null ? seasonsElement.selectFirst("a") : null;
        numberOfSeasons = seasonsElement != null ?  Integer.parseInt(seasonsElement.text()) : 0;
    }

    private void scrapSeasons() {
        seasons = new ArrayList<>();

        // Create a Integer list with range [1, number of seasons]
        ArrayList<Integer> numbers_list = new ArrayList<>(numberOfSeasons);
        for(int i = 1; i <= numberOfSeasons; i++){
            numbers_list.add(i);
        }

        // Map each list entry to a season object, filter by validation and collect as a map
        seasons = numbers_list
                .parallelStream()
                .map( season_number -> {
                    Season season = new Season();
                    season.scrap(imdbID, season_number);
                    return season;
                })
                .filter(Season::isValid)
                .collect(Collectors.toList());

        // Newer seasons can already have a webpage but no info, resulting in invalid entries.
        // Readjust numberOfSeasons after removing invalid entries.
        numberOfSeasons = seasons.size();
    }

    private String getURL() {
        return String.format(url, imdbID);
    }
}
