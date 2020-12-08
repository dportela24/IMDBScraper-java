package com.dportela.IMDBScraper.Modules;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TVSeries {
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

    public TVSeries() {}

    public void scrapData(Document doc){
        try {
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
            ratingValue = Double.parseDouble(titleBlock.select("span[itemprop=ratingValue]").text());

            // Scrap rating count
            ratingCount = Integer.parseInt(titleBlock.select("span[itemprop=ratingCount]").text().replace(",", ""));

            // Scrap poster
            poster = doc.getElementsByClass("poster").first().selectFirst("img").attr("src");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing tv series...");
        }
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
                '}';
    }
}
