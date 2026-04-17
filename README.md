# RAG Document Assistant

A Retrieval-Augmented Generation (RAG) system built with Spring Boot and Spring AI.
Upload any document, ask questions, get answers — powered entirely by a local AI model.

---

## The Problem

Large Language Models (LLMs) like LLaMA know a lot about the world, but they don't know
anything about *your* documents. If you ask an LLM about a company's internal report,
a contract, or a custom knowledge base, it will either hallucinate an answer or admit it
doesn't know.

**RAG solves this.** Instead of relying on the model's training data, RAG:
1. Breaks your document into chunks
2. Converts each chunk into a vector (a list of numbers representing meaning)
3. Stores those vectors in a database
4. At query time, finds the chunks most relevant to your question
5. Feeds those chunks to the LLM as context
6. The LLM answers using *your* document, not its imagination

---

## How It Works

```
Upload Document
      │
      ▼
Tika reads file (PDF, Word, txt)
      │
      ▼
Split into chunks (TokenTextSplitter)
      │
      ▼
Embed each chunk (nomic-embed-text via Ollama)
      │
      ▼
Store vectors in pgvector (PostgreSQL)

Ask a Question
      │
      ▼
Embed the question (nomic-embed-text)
      │
      ▼
Find similar chunks in pgvector (cosine similarity)
      │
      ▼
Send chunks + question to LLaMA 3.2 (Ollama)
      │
      ▼
Get a grounded answer
```

---

## Tech Stack

| Tool | Role |
|---|---|
| Spring Boot 4.x | Application framework |
| Spring AI 2.0.0-M4 | AI abstractions (chat, embeddings, vector store) |
| Ollama (llama3.2) | Local LLM — answers questions |
| Ollama (nomic-embed-text) | Local embedding model — converts text to vectors |
| pgvector (PostgreSQL) | Vector database — stores and searches embeddings |
| Apache Tika | Document parser — reads PDF, Word, txt |
| Docker | Runs PostgreSQL + pgvector with zero setup |

**Zero API costs** — everything runs locally via Ollama.

---

## Getting Started

### Prerequisites
- Java 21+
- Maven
- Docker
- Ollama installed and running

### 1. Pull the required Ollama models
```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

### 2. Start PostgreSQL with pgvector
```bash
docker compose up -d
```

### 3. Run the application
```bash
mvn spring-boot:run
```

---

## API Endpoints

### Upload a document
```
POST /api/documents/upload
Content-Type: multipart/form-data
Body: file = <your file>
```

### Ask a question
```
GET /api/chat/ask?question=Your question here
```

---

## Project Structure

```
src/main/java/com/donald/rag/
├── RagApplication.java
├── controller/
│   ├── DocumentController.java   # handles file uploads
│   └── ChatController.java       # handles Q&A requests
└── service/
    ├── DocumentService.java      # ingestion: read → chunk → embed → store
    └── ChatService.java          # retrieval: embed question → search → answer
```

---

## Key Concepts Learned

- **Embeddings** — turning text into numbers that capture semantic meaning
- **Vector similarity search** — finding chunks that are conceptually close to a question
- **RAG pipeline** — combining retrieval with generation for grounded answers
- **QuestionAnswerAdvisor** — Spring AI abstraction that automates the RAG query flow
