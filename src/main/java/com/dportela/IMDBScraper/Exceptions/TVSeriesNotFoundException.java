package com.dportela.IMDBScraper.Exceptions;

public class TVSeriesNotFoundException extends RuntimeException{
    public TVSeriesNotFoundException() {
        super("TV series could not be found...");
    }

    public TVSeriesNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
