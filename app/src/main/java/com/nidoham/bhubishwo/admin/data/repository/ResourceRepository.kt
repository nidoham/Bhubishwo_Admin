package com.nidoham.bhubishwo.admin.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nidoham.bhubishwo.admin.domain.media.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ResourceRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("resources")

    companion object {
        const val DEFAULT_PAGE_SIZE = 20L
        const val FIELD_TITLE = "title"
        const val FIELD_TAGS = "tags"
        const val FIELD_CREATED_AT = "createdAt"
    }

    // ==================== PUSH ====================

    suspend fun push(resource: Resource): Result<Unit> = try {
        collection.document(resource.id).set(resource.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun pushAll(resources: List<Resource>): Result<Int> = try {
        val batch = firestore.batch()
        resources.forEach { resource ->
            batch.set(collection.document(resource.id), resource.toMap())
        }
        batch.commit().await()
        Result.success(resources.size)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== GET ====================

    suspend fun getById(id: String): Result<Resource?> = try {
        val doc = collection.document(id).get().await()
        Result.success(doc.toResource())
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getAllFlow(): Flow<List<Resource>> = callbackFlow {
        val listener = collection
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toResource() } ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun getAll(): Result<List<Resource>> = try {
        val snapshot = collection.get().await()
        Result.success(snapshot.documents.mapNotNull { it.toResource() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== SEARCH ====================

    suspend fun searchByTitle(query: String): Result<List<Resource>> {
        return try {
            if (query.isBlank()) return Result.success(emptyList())
            val end = query + '\uf8ff'
            val snapshot = collection
                .whereGreaterThanOrEqualTo(FIELD_TITLE, query)
                .whereLessThanOrEqualTo(FIELD_TITLE, end)
                .get().await()
            Result.success(snapshot.documents.mapNotNull { it.toResource() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun searchByTitleFlow(query: String): Flow<List<Resource>> = callbackFlow {
        if (query.isBlank()) { trySend(emptyList()); close(); return@callbackFlow }
        val end = query + '\uf8ff'
        val listener = collection
            .whereGreaterThanOrEqualTo(FIELD_TITLE, query)
            .whereLessThanOrEqualTo(FIELD_TITLE, end)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.documents?.mapNotNull { it.toResource() } ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun filterByTag(tag: String): Result<List<Resource>> = try {
        val snapshot = collection
            .whereArrayContains(FIELD_TAGS, tag)
            .get().await()
        Result.success(snapshot.documents.mapNotNull { it.toResource() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== PAGING ====================

    suspend fun filterByTagPage(
        tag: String,
        lastDocumentId: String? = null,
        pageSize: Long = DEFAULT_PAGE_SIZE
    ): Result<PageResult<Resource>> = try {
        // ✅ Renamed: var query → var firestoreQuery
        var firestoreQuery = collection
            .whereArrayContains(FIELD_TAGS, tag)
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .limit(pageSize)

        if (lastDocumentId != null) {
            val lastDoc = collection.document(lastDocumentId).get().await()
            firestoreQuery = firestoreQuery.startAfter(lastDoc)
        }

        val snapshot = firestoreQuery.get().await()
        val items = snapshot.documents.mapNotNull { it.toResource() }

        Result.success(PageResult(
            items = items,
            lastDocumentId = snapshot.documents.lastOrNull()?.id,
            hasNext = items.size.toLong() == pageSize
        ))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getPage(
        lastDocumentId: String? = null,
        pageSize: Long = DEFAULT_PAGE_SIZE
    ): Result<PageResult<Resource>> = try {
        // ✅ Renamed: var query → var firestoreQuery
        var firestoreQuery = collection
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .limit(pageSize)

        if (lastDocumentId != null) {
            val lastDoc = collection.document(lastDocumentId).get().await()
            firestoreQuery = firestoreQuery.startAfter(lastDoc)
        }

        val snapshot = firestoreQuery.get().await()
        val items = snapshot.documents.mapNotNull { it.toResource() }

        Result.success(PageResult(
            items = items,
            lastDocumentId = snapshot.documents.lastOrNull()?.id,
            hasNext = items.size.toLong() == pageSize
        ))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun searchPage(
        query: String,
        lastDocumentId: String? = null,
        pageSize: Long = DEFAULT_PAGE_SIZE
    ): Result<PageResult<Resource>> {
        return try {
            if (query.isBlank()) return Result.success(PageResult(emptyList(), null, false))

            val end = query + '\uf8ff'
            var firestoreQuery = collection
                .whereGreaterThanOrEqualTo(FIELD_TITLE, query)
                .whereLessThanOrEqualTo(FIELD_TITLE, end)
                .orderBy(FIELD_TITLE)
                .limit(pageSize)

            if (lastDocumentId != null) {
                val lastDoc = collection.document(lastDocumentId).get().await()
                firestoreQuery = firestoreQuery.startAfter(lastDoc)
            }

            val snapshot = firestoreQuery.get().await()
            val items = snapshot.documents.mapNotNull { it.toResource() }

            Result.success(PageResult(
                items = items,
                lastDocumentId = snapshot.documents.lastOrNull()?.id,
                hasNext = items.size.toLong() == pageSize
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== DELETE ====================

    suspend fun remove(id: String): Result<Unit> = try {
        collection.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun removeAll(ids: List<String>): Result<Unit> = try {
        val batch = firestore.batch()
        ids.forEach { id -> batch.delete(collection.document(id)) }
        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== MAPPERS ====================

    private fun Resource.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "url" to url,
        "tags" to tags.toList(),
        FIELD_CREATED_AT to System.currentTimeMillis()
    )

    private fun com.google.firebase.firestore.DocumentSnapshot.toResource(): Resource? {
        return Resource(
            id = getString("id") ?: return null,
            title = getString("title") ?: return null,
            url = getString("url") ?: return null,
            tags = (get("tags") as? List<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet()
        )
    }

    data class PageResult<T>(
        val items: List<T>,
        val lastDocumentId: String?,
        val hasNext: Boolean
    )
}