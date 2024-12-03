package com.shop;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {


    private Map<String, Document> storage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() != null) {
            storage.put(document.getId(), document);
        } else {
            storage.put(UUID.randomUUID().toString(), document);
        }
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */

    private List<Document> prefixCheck(SearchRequest request, Document document) {
        List<Document> result = new ArrayList<>();

        if (request.getTitlePrefixes() != null && document.getTitle() != null) {
            for (String prefix : request.getTitlePrefixes()) {
                if (document.getTitle().startsWith(prefix)) {
                    result.add(document);
                }
            }
        }
        return result;
    }

    private List<Document> containsContentCheck(SearchRequest request, Document document) {
        List<Document> result = new ArrayList<>();

        if (request.getContainsContents() != null && document.getContent() != null) {
            for (String content : request.getContainsContents()) {
                if (document.getContent().contains(content)) {
                    result.add(document);
                }
            }
        }
        return result;
    }

    private List<Document> authorIdCheck(SearchRequest request, Document document) {
        List<Document> result = new ArrayList<>();

        if (request.getAuthorIds() != null && document.getAuthor().getId() != null)
            for (String authorId : request.getAuthorIds()) {
                if (document.getAuthor().getId() == authorId) {
                    result.add(document);
                }
            }
        return result;
    }

    private List<Document> dateCheck(SearchRequest request, Document document) {
        List<Document> result = new ArrayList<>();

        if (
                document.getCreated() != null &&
                        request.getCreatedFrom() != null &&
                        request.getCreatedTo() != null) {

            if (document.getCreated().isAfter(request.getCreatedFrom()) &&
                    document.getCreated().isBefore(request.getCreatedTo())) {
                result.add(document);
            }
        }
        return result;
    }

    public List<Document> search(SearchRequest request) {

        List<Document> result = new ArrayList<>();

        for (Document document : storage.values()) {

            result.addAll(prefixCheck(request, document));

            result.addAll(containsContentCheck(request, document));

            result.addAll(authorIdCheck(request, document));

            result.addAll(dateCheck(request, document));
        }
        return result;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {

        Document document = storage.get(id);
        return Optional.ofNullable(document);
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}