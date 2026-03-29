package com.github.dimitryivaniuta.gateway.docstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.dimitryivaniuta.gateway.docstore.client.DocstoreClient;
import com.github.dimitryivaniuta.gateway.docstore.model.DocumentFormat;
import com.github.dimitryivaniuta.gateway.docstore.model.DocumentRequest;
import com.github.dimitryivaniuta.gateway.docstore.model.ResolutionPath;
import com.github.dimitryivaniuta.gateway.docstore.model.StoredDocument;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DocstoreFallbackResolverService}.
 */
class DocstoreFallbackResolverServiceTest {

    private InMemoryDocstoreClient docstoreClient;
    private DocstoreFallbackResolverService service;

    @BeforeEach
    void setUp() {
        docstoreClient = new InMemoryDocstoreClient();
        service = new DocstoreFallbackResolverService(docstoreClient, new LegacyCompositeIdentifierBuilder());
    }

    @Test
    void shouldReturnCanonicalDocumentWhenPresent() {
        docstoreClient.put("123456P", "client-term-sheet-final", document("123456P", "client-term-sheet-final"));

        var result = service.resolve(new DocumentRequest(
                "123456P", "client-term-sheet-final", DocumentFormat.PDF, "English"));

        assertThat(result.resolutionPath()).isEqualTo(ResolutionPath.CANONICAL);
        assertThat(result.document().resolvedIdentifier()).isEqualTo("123456P");
        assertThat(result.attempts()).hasSize(1);
    }

    @Test
    void shouldFallBackToLegacyCompositeIdentifier() {
        docstoreClient.put(
                "123456P-PDF-English",
                "client-term-sheet-final",
                document("123456P-PDF-English", "client-term-sheet-final"));

        var result = service.resolve(new DocumentRequest(
                "123456P", "client-term-sheet-final", DocumentFormat.PDF, "English"));

        assertThat(result.resolutionPath()).isEqualTo(ResolutionPath.LEGACY_COMPOSITE);
        assertThat(result.document().resolvedIdentifier()).isEqualTo("123456P-PDF-English");
        assertThat(result.attempts()).hasSize(2);
        assertThat(result.attempts().get(0).found()).isFalse();
        assertThat(result.attempts().get(1).found()).isTrue();
    }

    @Test
    void shouldFailWhenDocumentCannotBeFoundUnderEitherIdentifier() {
        var request = new DocumentRequest("123456P", "client-term-sheet-final", DocumentFormat.PDF, "English");

        assertThatThrownBy(() -> service.resolve(request))
                .isInstanceOf(DocumentNotFoundException.class)
                .hasMessageContaining("123456P")
                .hasMessageContaining("123456P-PDF-English");
    }

    private StoredDocument document(String identifier, String documentName) {
        return new StoredDocument(identifier, documentName, "ok".getBytes(), Instant.parse("2026-03-28T10:15:30Z"));
    }

    /**
     * Tiny in-memory test double used by the unit tests.
     */
    private static final class InMemoryDocstoreClient implements DocstoreClient {
        private final Map<String, StoredDocument> storage = new HashMap<>();

        void put(String identifier, String documentName, StoredDocument document) {
            storage.put(key(identifier, documentName), document);
        }

        @Override
        public Optional<StoredDocument> fetch(String identifier, String documentName) {
            return Optional.ofNullable(storage.get(key(identifier, documentName)));
        }

        private String key(String identifier, String documentName) {
            return identifier + "::" + documentName;
        }
    }
}
