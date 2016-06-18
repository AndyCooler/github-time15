package com.mango_apps.time15.storage;

/**
 * Thrown when a line in the csv file has an unexpected or missing element.
 */
public class CsvFileLineWrongException extends Exception {

    private String id;

    private String element;

    /**
     * Constructs a new {@code Exception} that includes the current stack trace.
     */
    public CsvFileLineWrongException(String id, String element) {
        this.id = id;
        this.element = element;
    }

}
