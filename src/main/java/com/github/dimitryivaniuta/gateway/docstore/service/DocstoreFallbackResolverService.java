package com.github.dimitryivaniuta.gateway.docstore.service;

import com.github.dimitryivaniuta.gateway.docstore.client.DocstoreClient;
import com.github.dimitryivaniuta.gateway.docstore.model.DocumentRequest;
import com.github.dimitryivaniuta.gateway.docstore.model.DocumentResolutionResult;
import com.github.dimitryivaniuta.gateway.docstore.model.LookupAttempt;
import com.github.dimitryivaniuta.gateway.docstore.model.ResolutionPath;
import com.github.dimitryivaniuta.gateway.docstore.model.StoredDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implements the recommended short-term fix from the meeting: retrieve documents by trying the
 * canonical product reference first and the legacy composite Docstore identifier second.
 *
 * <p>This approach was recommended because it is safer than immediately changing upload behaviour:</p>
 *
 * <ul>
 *   <li>it solves the visible SPP Docs retrieval problem,</li>
 *   <li>it works for historical documents already stored under old identifiers,</li>
 *   <li>it does not increase upload traffic while Docstore performance is under investigation.</li>
 * </ul>
 */
public class DocstoreFallbackResolverService {

    private final DocstoreClient docstoreClient;
    private final LegacyCompositeIdentifierBuilder identifierBuilder;

    /**
     * Creates the resolver.
     *
     * @param docstoreClient low-level client used to query Docstore
     * @param identifierBuilder helper used to reproduce the legacy composite key
     */
    public DocstoreFallbackResolverService(
            DocstoreClient docstoreClient,
            LegacyCompositeIdentifierBuilder identifierBuilder) {
        this.docstoreClient = Objects.requireNonNull(docstoreClient, "docstoreClient must not be null");
        this.identifierBuilder = Objects.requireNonNull(identifierBuilder, "identifierBuilder must not be null");
    }

    /**
     * Resolves a document using the recommended consumer-side fallback algorithm.
     *
     * <p>Algorithm:</p>
     * <ol>
     *   <li>Try the canonical product reference.</li>
     *   <li>If not found, derive the legacy composite key and try again.</li>
     *   <li>If neither lookup succeeds, fail explicitly with the attempted identifiers.</li>
     * </ol>
     *
     * @param request retrieval request containing canonical and legacy lookup attributes
     * @return successful resolution result including the winning path and attempt history
     * @throws DocumentNotFoundException when the document cannot be found under either identifier
     */
    public DocumentResolutionResult resolve(DocumentRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        List<LookupAttempt> attempts = new ArrayList<>(2);
        StoredDocument canonicalDocument = docstoreClient
                .fetch(request.productReference(), request.documentName())
                .orElse(null);
        attempts.add(new LookupAttempt(request.productReference(), canonicalDocument != null));

        if (canonicalDocument != null) {
            return new DocumentResolutionResult(canonicalDocument, ResolutionPath.CANONICAL, attempts);
        }

        String legacyCompositeIdentifier = identifierBuilder.build(
                request.productReference(), request.format(), request.language());

        StoredDocument legacyDocument = docstoreClient
                .fetch(legacyCompositeIdentifier, request.documentName())
                .orElse(null);
        attempts.add(new LookupAttempt(legacyCompositeIdentifier, legacyDocument != null));

        if (legacyDocument != null) {
            return new DocumentResolutionResult(legacyDocument, ResolutionPath.LEGACY_COMPOSITE, attempts);
        }

        throw new DocumentNotFoundException(
                "Document was not found under canonical identifier '%s' or legacy composite identifier '%s'"
                        .formatted(request.productReference(), legacyCompositeIdentifier));
    }
}
