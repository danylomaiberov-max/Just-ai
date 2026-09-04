package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): ChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity): Long

    @Update
    suspend fun updateSession(session: ChatSessionEntity)

    @Query("DELETE FROM chat_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    @Query("DELETE FROM chat_sessions")
    suspend fun clearAllSessions()
}

@Dao
interface ModelDao {
    @Query("SELECT * FROM ai_models ORDER BY isDownloaded DESC, name ASC")
    fun getAllModels(): Flow<List<AiModelEntity>>

    @Query("SELECT * FROM ai_models")
    suspend fun getModelsList(): List<AiModelEntity>

    @Query("SELECT * FROM ai_models WHERE id = :id")
    suspend fun getModelById(id: String): AiModelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: AiModelEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertModels(models: List<AiModelEntity>)

    @Update
    suspend fun updateModel(model: AiModelEntity)

    @Query("UPDATE ai_models SET isLoadedInRam = 0")
    suspend fun unloadAllModels()

    @Query("UPDATE ai_models SET isLoadedInRam = :isLoaded WHERE id = :id")
    suspend fun setModelLoaded(id: String, isLoaded: Boolean)

    @Query("DELETE FROM ai_models WHERE id = :id")
    suspend fun deleteModel(id: String)
}

@Dao
interface VectorDao {
    @Query("SELECT * FROM vector_collections ORDER BY createdAt DESC")
    fun getAllCollections(): Flow<List<VectorCollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: VectorCollectionEntity): Long

    @Query("DELETE FROM vector_collections WHERE id = :id")
    suspend fun deleteCollection(id: Long)

    @Query("SELECT * FROM vector_documents WHERE collectionId = :collectionId")
    suspend fun getDocumentsForCollection(collectionId: Long): List<VectorDocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: VectorDocumentEntity): Long

    @Query("DELETE FROM vector_documents WHERE collectionId = :collectionId")
    suspend fun deleteDocumentsForCollection(collectionId: Long)

    @Query("SELECT * FROM vector_chunks WHERE collectionId = :collectionId")
    suspend fun getChunksForCollection(collectionId: Long): List<VectorChunkEntity>

    @Query("SELECT * FROM vector_chunks")
    suspend fun getAllChunks(): List<VectorChunkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<VectorChunkEntity>)

    @Query("DELETE FROM vector_chunks WHERE collectionId = :collectionId")
    suspend fun deleteChunksForCollection(collectionId: Long)

    @Query("DELETE FROM vector_collections")
    suspend fun clearAllCollections()
}

@Dao
interface CodeDao {
    @Query("SELECT * FROM code_snippets ORDER BY updatedAt DESC")
    fun getAllSnippets(): Flow<List<CodeSnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: CodeSnippetEntity): Long

    @Update
    suspend fun updateSnippet(snippet: CodeSnippetEntity)

    @Query("DELETE FROM code_snippets WHERE id = :id")
    suspend fun deleteSnippet(id: Long)
}

@Dao
interface ServerLogDao {
    @Query("SELECT * FROM server_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<ServerLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ServerLogEntity)

    @Query("DELETE FROM server_logs")
    suspend fun clearLogs()
}
