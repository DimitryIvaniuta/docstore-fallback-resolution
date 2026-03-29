package com.github.dimitryivaniuta.gateway.docstore.client;

import com.github.dimitryivaniuta.gateway.docstore.model.StoredDocument;
import java.util.Optional;

/**
 * Minimal Docstore client abstraction used by the fallback resolver.
 *
 * <p>The design intentionally stays small because the recommendation from the meetings was to change
 * retrieval behaviour on the consumer side, not to redesign Docstore itself. Any HTTP, gRPC, or SDK
 * integration can implement this interface.</p>
 */
public interface DocstoreClient {

    /**
     * Retrieves a document by Docstore identifier and document name.
     *
     * @param identifier canonical or composite Docstore identifier
     * @param documentName logical document name/path segment
     * @return matching document when found, otherwise an empty optional
     */
    Optional<StoredDocument> fetch(String identifier, String documentName);
}
