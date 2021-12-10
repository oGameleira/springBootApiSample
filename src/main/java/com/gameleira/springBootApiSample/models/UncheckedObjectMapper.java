package com.gameleira.springBootApiSample.models;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.concurrent.CompletionException;

public class UncheckedObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
    /**
     * Parses the given JSON string into a Map.
     */
    Post readValue(String content) {
        try {
            return this.readValue(content, new TypeReference<>() {
            });
        } catch (IOException ioe) {
            throw new CompletionException(ioe);
        }
    }

}