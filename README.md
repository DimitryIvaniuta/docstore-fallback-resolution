# Docstore fallback resolution - recommended Java approach

This mini-project implements the **recommended short-term fix** from the Docstore discussion:

1. **Do not immediately change producer upload logic in Document Service**.
2. **Fix retrieval on the consumer side first**.
3. **Query Docstore by the canonical product key first**.
4. **If not found, query again using the legacy composite key** (`productRef-FORMAT-language`).
5. Keep measuring fallback usage so the team can later migrate to the clean upload model.

## Why this approach was recommended

It is the safest immediate fix because it:

- solves the visible retrieval issue for SPP Docs style consumers,
- works for **historical** documents already stored under legacy composite identifiers,
- avoids doubling upload traffic while Docstore / Document Service performance is still under investigation.

## Implemented sources

### Main classes

- `DocstoreClient` - abstraction over the actual Docstore HTTP/SDK call.
- `LegacyCompositeIdentifierBuilder` - reproduces the old composite key convention.
- `DocstoreFallbackResolverService` - implements canonical lookup, then fallback lookup.
- `SppDocsDocumentFacade` - example consumer-side entry point.
- Model classes under `model/` describe the request, result, attempts, and document payload.

### Tests

`DocstoreFallbackResolverServiceTest` covers:

- canonical key success,
- legacy composite fallback success,
- full miss / explicit exception.

## Step-by-step resolution path for the real system

### Step 1 - Implement retrieval fallback in the consumer
Use `DocstoreFallbackResolverService` (or the same logic) inside the consumer that currently queries Docstore directly.

Pseudo-flow:

```text
try productRef
if not found -> try productRef-FORMAT-language
if still not found -> return not found / error
```

### Step 2 - Add observability
Record metrics/logs for:

- canonical hits,
- fallback hits,
- misses.

That tells you how many users still depend on the old composite identifier model.

### Step 3 - Speak to downstream consumers
Before changing uploads, confirm whether other systems such as SPP Docs, Klondike, or reports still rely on composite identifiers.

### Step 4 - Fix performance bottlenecks
Do **not** enable dual-upload until Document Service ↔ Docstore latency / timeout issues are understood and stabilized.

### Step 5 - Move to the clean producer model later
Once safe:

- group all documents by canonical product/STOMP identifier,
- treat format/language changes as rendition/version concerns,
- release behind a feature flag if needed.

## Build

```bash
./gradlew test
```
