package com.github.dimitryivaniuta.gateway.docstore.model;

/**
 * Captures one lookup attempt made by the fallback resolver.
 *
 * <p>The meeting highlighted the need for safe rollout and auditability. This record exists so the
 * caller can log, inspect, or expose exactly which identifiers were attempted and whether they
 * succeeded.</p>
 *
 * @param identifier identifier attempted against Docstore
 * @param found whether Docstore returned a matching document for this identifier
 */
public record LookupAttempt(String identifier, boolean found) {
}
