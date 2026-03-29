package com.github.dimitryivaniuta.gateway.docstore.model;

import java.util.List;
import java.util.Objects;

/**
 * Wraps the document returned by the consumer-side fallback strategy together with the diagnostic
 * information collected while resolving it.
 *
 * <p>This output is intended for a calling application such as SPP Docs so it can:</p>
 *
 * <ul>
 *   <li>serve the document to the end user,</li>
 *   <li>log whether the request depended on the legacy composite path,</li>
 *   <li>measure how much historical traffic still relies on the old Docstore convention.</li>
 * </ul>
 */
public record DocumentResolutionResult(
        StoredDocument document,
        ResolutionPath resolutionPath,
        List<LookupAttempt> attempts) {

    /**
     * Creates an immutable resolution result.
     *
     * @param document resolved document payload
     * @param resolutionPath winning lookup path
     * @param attempts ordered lookup attempts made by the resolver
     */
    public DocumentResolutionResult {
        Objects.requireNonNull(document, "document must not be null");
        Objects.requireNonNull(resolutionPath, "resolutionPath must not be null");
        Objects.requireNonNull(attempts, "attempts must not be null");
        attempts = List.copyOf(attempts);
    }
}
