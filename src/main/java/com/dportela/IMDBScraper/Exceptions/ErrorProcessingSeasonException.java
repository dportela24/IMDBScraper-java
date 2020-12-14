package com.dportela.IMDBScraper.Exceptions;

public class ErrorProcessingSeasonException extends RuntimeException{
    public ErrorProcessingSeasonException() {
        super("Error processing a TV Season");
    }

    public ErrorProcessingSeasonException(String errorMessage) {
        super(errorMessage);
    }

    public ErrorProcessingSeasonException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}