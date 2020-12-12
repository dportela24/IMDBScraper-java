package com.dportela.IMDBScraper.Modules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private Map<Integer, Season> seasons;

    public TVSeries(String imdbID) {
        this.imdbID = imdbID;
    }

    public void scrap(){
        try {
            // Fetch html
            Document doc = Jsoup.connect(getURL())
                    .header("Accept-Language", "en")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
                    .get();

            // Check if valid request
            String type = doc.selectFirst("meta[property=og:type]").attr("content");
            if (type.isEmpty() || !type.equals("video.tv_show"))
            {
                throw new IOException("TV Series not found...");
            }

            scrapSeriesData(doc);
            scrapSeasons();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing tv series...");
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
        summary = doc.getElementsByClass("summary_text").first().text();

        // Scrap episode duration
        episodeDuration = titleBlock.selectFirst("time").text();

        // Scrap runtime
        String runtime_info = titleBlock.selectFirst("a[title=\"See more release dates\"]").text();
        Pattern pattern = Pattern.compile("\\d+–?(\\d+)?");
        Matcher matcher = pattern.matcher(runtime_info);
        runtime = matcher.find() ? matcher.group() : "";

        // Scrap genres
        genres = titleBlock.getElementsByClass("subtext").first().children()
                .stream()
                .filter(element -> element.hasAttr("href") && element.attr("href").contains("genres"))
                .map(Element::text)
                .collect(Collectors.toList());

        // Scrap rating value
        String ratingValue_str = titleBlock.select("span[itemprop=ratingValue]").text();
        if (ratingValue_str.isEmpty()) {
            ratingValue = -1;
        } else {
            ratingValue = Double.parseDouble(ratingValue_str);

        }

        // Scrap rating count
        String ratingCount_str = titleBlock.select("span[itemprop=ratingCount]").text();
        if (ratingCount_str.isEmpty()) {
            ratingCount = -1;
        } else {
            ratingCount = Integer.parseInt(ratingCount_str.replace(",", ""));
        }

        // Scrap poster
        poster = doc.getElementsByClass("poster").first().selectFirst("img").attr("src");

        // Scrap number of seasons
        numberOfSeasons = Integer.parseInt(doc.getElementsByClass("seasons-and-year-nav").first()
                .selectFirst("a").text());
    }

    private void scrapSeasons() {
        try {
            seasons = new ConcurrentHashMap<>();
            ArrayList<Integer> numbers_list = new ArrayList<>(numberOfSeasons);
            for(int i = 1; i <= numberOfSeasons; i++){
                numbers_list.add(i);
            }
            seasons = numbers_list
                    .parallelStream()
                    .map( season_number -> {
                        Season season = new Season();
                        season.scrap(imdbID, season_number);
                        return season;
                    })
                    .filter(Season::isValid)
                    .collect(Collectors.toConcurrentMap(
                            Season::getNumber,
                            season -> season
                    ));

            // Newer seasons can already have a webpage but no info, resulting in invalid entries.
            // Readjust numberOfSeasons after removing invalid entries.
            numberOfSeasons = seasons.size();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing season");
        }
    }

    private String getURL() {
        return String.format(url, imdbID);
    }

    @Override
    public String toString() {
        return "TVSeries{" +
                "imdbID='" + imdbID + '\'' +
                ", name='" + name + '\'' +
                ", originalName='" + originalName + '\'' +
                ", summary='" + summary + '\'' +
                ", episodeDuration='" + episodeDuration + '\'' +
                ", runTime='" + runtime + '\'' +
                ", genres=" + genres +
                ", ratingValue=" + ratingValue +
                ", ratingCount=" + ratingCount +
                ", poster='" + poster + '\'' +
                ", numberOfSeasons=" + numberOfSeasons +
                ", seasons=" + seasons +
                '}';
    }
}
