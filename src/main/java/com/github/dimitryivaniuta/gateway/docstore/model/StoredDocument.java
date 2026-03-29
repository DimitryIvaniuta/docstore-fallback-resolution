package com.github.dimitryivaniuta.gateway.docstore.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a document returned by Docstore.
 *
 * <p>This type intentionally captures the resolved identifier that was ultimately used to fetch the
 * document. That makes it observable whether the request succeeded through the canonical product key
 * or the legacy composite fallback path.</p>
 */
public record StoredDocument(
        String resolvedIdentifier,
        String documentName,
        byte[] content,
        Instant retrievedAt) {

    /**
     * Creates a stored document payload.
     *
     * @param resolvedIdentifier identifier under which the document was located in Docstore
     * @param documentName logical document name/path returned to the caller
     * @param content binary document contents
     * @param retrievedAt retrieval timestamp for observability and debugging
     */
    public StoredDocument {
        if (resolvedIdentifier == null || resolvedIdentifier.isBlank()) {
            throw new IllegalArgumentException("resolvedIdentifier must not be blank");
        }
        if (documentName == null || documentName.isBlank()) {
            throw new IllegalArgumentException("documentName must not be blank");
        }
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(retrievedAt, "retrievedAt must not be null");
    }
}
