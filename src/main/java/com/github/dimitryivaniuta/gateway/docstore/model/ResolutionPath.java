package com.github.dimitryivaniuta.gateway.docstore.model;

/**
 * Describes which retrieval path resolved a document.
 */
public enum ResolutionPath {

    /** The document was found under the canonical product/STOMP identifier. */
    CANONICAL,

    /** The document was found only under the legacy composite Docstore identifier. */
    LEGACY_COMPOSITE
}
