package com.github.dimitryivaniuta.gateway.docstore.model;

/**
 * Enumerates the logical file formats used by Docstore lookup.
 *
 * <p>The legacy composite-key convention discussed in the meeting uses the document format as one
 * of the key parts. The consumer-side fallback resolver keeps this value explicit so the caller can
 * deterministically build the old-style identifier when needed.</p>
 */
public enum DocumentFormat {

    /** Portable Document Format. */
    PDF,

    /** Microsoft Word document. */
    DOC,

    /** HyperText Markup Language. */
    HTML,

    /** Any other format not yet modelled as a first-class enum constant. */
    OTHER
}
