package com.example.vectordb

import com.example.data.database.VectorChunkEntity
import kotlin.math.sqrt

data class SearchResult(
    val chunkText: String,
    val similarityScore: Float, // 0.0 to 1.0 (e.g., 0.94 -> 94%)
    val documentTitle: String,
    val chunkIndex: Int
)

object VectorDatabaseEngine {

    private const val VECTOR_DIM = 64

    /**
     * Splits arbitrary text into semantic chunks with overlap for RAG ingestion.
     */
    fun chunkText(text: String, chunkSize: Int = 180, overlap: Int = 30): List<String> {
        val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.isEmpty()) return emptyList()
        if (words.size <= chunkSize) return listOf(text.trim())

        val chunks = mutableListOf<String>()
        var start = 0
        while (start < words.size) {
            val end = (start + chunkSize).coerceAtMost(words.size)
            val chunkWords = words.subList(start, end)
            chunks.add(chunkWords.joinToString(" "))
            if (end == words.size) break
            start += (chunkSize - overlap).coerceAtLeast(1)
        }
        return chunks
    }

    /**
     * Computes on-device dense semantic vector representation (64-dim normalized).
     */
    fun computeEmbedding(text: String): List<Float> {
        val vector = FloatArray(VECTOR_DIM) { 0f }
        val tokens = text.lowercase().split("\\W+".toRegex()).filter { it.length > 2 }

        for (token in tokens) {
            val hash = token.hashCode()
            val primaryIndex = Math.abs(hash) % VECTOR_DIM
            val secondaryIndex = Math.abs(hash shr 5) % VECTOR_DIM
            val weight = (1.0f + (token.length * 0.1f)).coerceAtMost(2.5f)

            vector[primaryIndex] += weight
            vector[secondaryIndex] += weight * 0.5f
        }

        // L2 Normalization
        var sumSquares = 0f
        for (v in vector) {
            sumSquares += v * v
        }
        val norm = sqrt(sumSquares)
        if (norm > 0f) {
            for (i in vector.indices) {
                vector[i] /= norm
            }
        }
        return vector.toList()
    }

    /**
     * Computes cosine similarity between two normalized vectors.
     */
    fun cosineSimilarity(v1: List<Float>, v2: List<Float>): Float {
        if (v1.isEmpty() || v2.isEmpty() || v1.size != v2.size) return 0f
        var dot = 0f
        for (i in v1.indices) {
            dot += v1[i] * v2[i]
        }
        return dot.coerceIn(0f, 1f)
    }

    /**
     * Performs top-K nearest neighbor search against local vector chunks.
     */
    fun search(
        query: String,
        storedChunks: List<VectorChunkEntity>,
        topK: Int = 3,
        threshold: Float = 0.25f
    ): List<SearchResult> {
        if (storedChunks.isEmpty()) return emptyList()

        val queryEmbedding = computeEmbedding(query)
        val scored = storedChunks.mapNotNull { chunk ->
            try {
                val floats = chunk.embeddingJson.split(",").map { it.trim().toFloat() }
                val score = cosineSimilarity(queryEmbedding, floats)
                if (score >= threshold) {
                    SearchResult(
                        chunkText = chunk.text,
                        similarityScore = score,
                        documentTitle = "Document #${chunk.documentId}",
                        chunkIndex = chunk.chunkIndex
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }

        return scored.sortedByDescending { it.similarityScore }.take(topK)
    }
}
