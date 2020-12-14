package com.dportela.IMDBScraper.Exceptions;

public class ErrorProcessingEpisodeException extends RuntimeException{
    public ErrorProcessingEpisodeException() {
        super("Error processing a TV episode");
    }

    public ErrorProcessingEpisodeException(String errorMessage) {
        super(errorMessage);
    }
}