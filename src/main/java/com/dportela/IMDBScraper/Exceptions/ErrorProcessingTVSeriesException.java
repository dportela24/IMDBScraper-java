package com.dportela.IMDBScraper.Exceptions;

public class ErrorProcessingTVSeriesException extends RuntimeException{
    public ErrorProcessingTVSeriesException() {
        super("Error processing the TV Series");
    }

    public ErrorProcessingTVSeriesException(String errorMessage) {
        super(errorMessage);
    }

    public ErrorProcessingTVSeriesException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
