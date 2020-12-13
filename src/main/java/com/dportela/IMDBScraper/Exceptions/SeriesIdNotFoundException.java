package com.dportela.IMDBScraper.Exceptions;

public class SeriesIdNotFoundException extends Exception {
    public SeriesIdNotFoundException() {
        super("TV series id could not be found...");
    }

    public SeriesIdNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
