package com.github.dimitryivaniuta.gateway.docstore.model;

import java.util.Objects;

/**
 * Describes a document retrieval request coming from a Docstore consumer such as SPP Docs.
 *
 * <p>The core recommendation from the meeting was to fix retrieval first on the consumer side,
 * instead of immediately changing producer upload behaviour. This request object carries the minimum
 * data required to execute that retrieval strategy safely:</p>
 *
 * <ul>
 *   <li>the canonical product identifier expected by business users,</li>
 *   <li>the document name/path the consumer wants,</li>
 *   <li>the legacy format and language values used to reconstruct the historical composite key.</li>
 * </ul>
 */
public record DocumentRequest(
        String productReference,
        String documentName,
        DocumentFormat format,
        String language) {

    /**
     * Creates an immutable request and validates that the required lookup fields are present.
     *
     * @param productReference canonical product/STOMP identifier used as the preferred Docstore key
     * @param documentName logical document name or path segment requested by the caller
     * @param format document format used to construct the legacy composite key
     * @param language document language used to construct the legacy composite key
     */
    public DocumentRequest {
        requireText(productReference, "productReference");
        requireText(documentName, "documentName");
        Objects.requireNonNull(format, "format must not be null");
        requireText(language, "language");
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
