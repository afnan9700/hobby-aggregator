package com.somedomain.hobbyaggregator.common;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String field, Object value) {
        super(resource + " already exists with " + field + ": " + value);
    }
}