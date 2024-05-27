package org.dindier.oicraft.util.search;

import lombok.Setter;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;

/**
 * A utility class to search items with Lucene
 *
 * @param <T> the type of the items
 * @author Rzb, LeoDreamer
 */
public class SearchHelper<T> {

    @Setter
    private List<T> items; // the items to search
    @Setter
    private int maxSearchResult = 50; // the maximum number of search results
    private String identifier; // the field that uniquely identifies the item

    private final Map<String, Function<T, String>> fieldsMap = new HashMap<>();
    private final Map<String, Float> boosts = new HashMap<>();

    /**
     * Set the identifier of the items
     *
     * @param identifier the name of the identifier field
     * @param getter     a function that gets the identifier value from the item
     */
    public void setIdentifier(String identifier, Function<T, String> getter) {
        this.identifier = identifier;
        fieldsMap.put(identifier, getter);
    }

    /**
     * Add a field to the search helper
     *
     * @param field  the name of the field
     * @param getter a function that gets the field value from the item
     * @param boost  the boost of the field
     */
    public void addField(String field, Function<T, String> getter, float boost) {
        fieldsMap.put(field, getter);
        boosts.put(field, boost);
    }

    /**
     * Search the items with the keyword.
     * Set identifier and fields before calling this method
     *
     * @param keyword the keyword to search
     * @return a list of identifiers of the items that match the keyword
     */
    public List<T> search(String keyword) {
        List<String> identifiers = new ArrayList<>();
        try (Directory directory = new ByteBuffersDirectory();
             IndexWriter indexWriter = new IndexWriter(directory,
                     new IndexWriterConfig(new SmartChineseAnalyzer()))
        ) {
            // index the items
            for (T t : items) {
                Document document = new Document();
                for (Map.Entry<String, Function<T, String>> entry : fieldsMap.entrySet()) {
                    document.add(new TextField(entry.getKey(), entry.getValue().apply(t), Field.Store.YES));
                }
                indexWriter.addDocument(document);
            }
            indexWriter.commit();
            indexWriter.close();

            // parse the query
            String[] fields = new String[fieldsMap.size() - 1]; // should not include the identifier
            int cnt = 0;
            for (String field : fieldsMap.keySet()) {
                if (!field.equals(identifier)) {
                    fields[cnt++] = field;
                }
            }

            MultiFieldQueryParser parser = new MultiFieldQueryParser(
                    fields, new SmartChineseAnalyzer(), boosts);
            Query query;
            try {
                query = parser.parse(keyword);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // search the items
            try (IndexReader indexReader = DirectoryReader.open(directory)) {
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                TopDocs topDocs = indexSearcher.search(query, maxSearchResult);
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    identifiers.add(indexSearcher.doc(scoreDoc.doc).get(identifier));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Function<T, String> identifierGetter = fieldsMap.get(identifier);
        return items.stream().filter(t -> identifiers.contains(identifierGetter.apply(t))).toList();
    }
}
