package com.github.dimitryivaniuta.gateway.docstore.service;

/**
 * Thrown when a document cannot be resolved through either the canonical or legacy composite
 * Docstore identifier.
 */
public class DocumentNotFoundException extends RuntimeException {

    /**
     * Creates the exception.
     *
     * @param message human-readable error message containing the attempted identifiers
     */
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
