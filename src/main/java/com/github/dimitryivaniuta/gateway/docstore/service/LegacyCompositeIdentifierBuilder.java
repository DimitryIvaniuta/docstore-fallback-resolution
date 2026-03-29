package com.github.dimitryivaniuta.gateway.docstore.service;

import com.github.dimitryivaniuta.gateway.docstore.model.DocumentFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Builds the historical composite Docstore identifier discussed in the meetings.
 *
 * <p>The legacy pattern is effectively:</p>
 *
 * <pre>{@code
 * productReference-FORMAT-language
 * }</pre>
 *
 * <p>Although this is not the desired long-term model, the consumer-side resolution fix needs to
 * reproduce it exactly so existing documents remain discoverable.</p>
 */
public final class LegacyCompositeIdentifierBuilder {

    /**
     * Creates the composite Docstore identifier.
     *
     * @param productReference canonical product/STOMP identifier
     * @param format document format component
     * @param language document language component
     * @return composite identifier compatible with the legacy Docstore convention
     */
    public String build(String productReference, DocumentFormat format, String language) {
        requireText(productReference, "productReference");
        Objects.requireNonNull(format, "format must not be null");
        requireText(language, "language");
        return productReference + '-' + format.name().toUpperCase(Locale.ROOT) + '-'
                + language.trim();
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
