package com.github.dimitryivaniuta.gateway.docstore.service;

import com.github.dimitryivaniuta.gateway.docstore.model.DocumentRequest;
import com.github.dimitryivaniuta.gateway.docstore.model.DocumentResolutionResult;
import java.util.Objects;

/**
 * Example application-facing facade showing how an SPP Docs style consumer can adopt the recommended
 * resolution pattern without leaking Docstore lookup complexity throughout the codebase.
 *
 * <p>In a real application this facade would sit behind a controller or service endpoint and would
 * also emit metrics such as:</p>
 *
 * <ul>
 *   <li>fallback hit rate,</li>
 *   <li>document-not-found count,</li>
 *   <li>ratio of historical traffic still using the legacy composite convention.</li>
 * </ul>
 */
public class SppDocsDocumentFacade {

    private final DocstoreFallbackResolverService fallbackResolverService;

    /**
     * Creates the facade.
     *
     * @param fallbackResolverService resolver implementing canonical-then-legacy lookup
     */
    public SppDocsDocumentFacade(DocstoreFallbackResolverService fallbackResolverService) {
        this.fallbackResolverService = Objects.requireNonNull(
                fallbackResolverService, "fallbackResolverService must not be null");
    }

    /**
     * Retrieves a document using the safe consumer-side resolution strategy.
     *
     * @param request document request originating from the caller
     * @return resolution result containing the document and the path used to find it
     */
    public DocumentResolutionResult getDocument(DocumentRequest request) {
        return fallbackResolverService.resolve(request);
    }
}
