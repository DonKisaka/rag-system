package com.donald.rag.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {
    private final VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestDocument(Resource file) {
        // Step 1: Read the file (PDF, Word, txt — Tika handles all)
        TikaDocumentReader reader = new TikaDocumentReader(file);
        List<Document> documents = reader.get();

        // Step 2: Split into chunks (AI can't process huge documents at once)
        TokenTextSplitter splitter =TokenTextSplitter.builder().build();
        List<Document> chunks = splitter.apply(documents);

        // Step 3: Embed + store in pgvector (Spring AI does both in one call)
        vectorStore.add(chunks);
    }
}
