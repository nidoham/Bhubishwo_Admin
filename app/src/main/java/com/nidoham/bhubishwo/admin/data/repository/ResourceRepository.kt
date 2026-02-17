package com.nidoham.bhubishwo.admin.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
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
        const val FIELD_ID = "id"
    }

    // ==================== PUSH (CREATE / UPDATE) ====================

    /**
     * Saves a resource. Uses SetOptions.merge() to update fields without
     * overwriting the entire document if it already exists.
     */
    suspend fun push(resource: Resource): Result<Unit> = try {
        val data = resource.toMap()
        collection.document(resource.id).set(data, SetOptions.merge()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun pushAll(resources: List<Resource>): Result<Int> = try {
        val batch = firestore.batch()
        resources.forEach { resource ->
            // merge() ensures we don't accidentally wipe existing metadata if logic changes
            batch.set(collection.document(resource.id), resource.toMap(), SetOptions.merge())
        }
        batch.commit().await()
        Result.success(resources.size)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== GET ====================

    suspend fun getById(id: String): Result<Resource?> = try {
        val doc = collection.document(id).get().await()
        Result.success(doc.toSafeResource())
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getAllFlow(): Flow<List<Resource>> = callbackFlow {
        val listener = collection
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { it.toSafeResource() } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getAll(): Result<List<Resource>> = try {
        val snapshot = collection.orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING).get().await()
        Result.success(snapshot.documents.mapNotNull { it.toSafeResource() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== SEARCH & FILTER ====================

    suspend fun searchByTitle(query: String): Result<List<Resource>> {
        return try {
            if (query.isBlank()) return Result.success(emptyList())
            val end = query + '\uf8ff' // Unicode trick for "starts with" query
            val snapshot = collection
                .whereGreaterThanOrEqualTo(FIELD_TITLE, query)
                .whereLessThanOrEqualTo(FIELD_TITLE, end)
                .orderBy(FIELD_TITLE) // Required for range query
                .get().await()
            Result.success(snapshot.documents.mapNotNull { it.toSafeResource() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun searchByTitleFlow(query: String): Flow<List<Resource>> = callbackFlow {
        if (query.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val end = query + '\uf8ff'
        val listener = collection
            .whereGreaterThanOrEqualTo(FIELD_TITLE, query)
            .whereLessThanOrEqualTo(FIELD_TITLE, end)
            .orderBy(FIELD_TITLE)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.mapNotNull { it.toSafeResource() } ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun filterByTag(tag: String): Result<List<Resource>> = try {
        val snapshot = collection
            .whereArrayContains(FIELD_TAGS, tag)
            .get().await()
        Result.success(snapshot.documents.mapNotNull { it.toSafeResource() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== PAGING ====================

    /**
     * Note: For this to work with 'orderBy(FIELD_CREATED_AT)', you need a Composite Index
     * in Firebase Console for: tags (Array) + createdAt (Desc).
     */
    suspend fun filterByTagPage(
        tag: String,
        lastDocumentId: String? = null,
        pageSize: Long = DEFAULT_PAGE_SIZE
    ): Result<PageResult<Resource>> = try {
        var queryBuilder = collection
            .whereArrayContains(FIELD_TAGS, tag)
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .limit(pageSize)

        if (lastDocumentId != null) {
            // Note: This requires an extra read. Optimization: Pass timestamp instead of ID if possible.
            val lastDoc = collection.document(lastDocumentId).get().await()
            if (lastDoc.exists()) {
                queryBuilder = queryBuilder.startAfter(lastDoc)
            }
        }

        val snapshot = queryBuilder.get().await()
        val items = snapshot.documents.mapNotNull { it.toSafeResource() }

        Result.success(
            PageResult(
                items = items,
                lastDocumentId = snapshot.documents.lastOrNull()?.id,
                hasNext = items.size.toLong() == pageSize
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getPage(
        lastDocumentId: String? = null,
        pageSize: Long = DEFAULT_PAGE_SIZE
    ): Result<PageResult<Resource>> = try {
        var queryBuilder = collection
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .limit(pageSize)

        if (lastDocumentId != null) {
            val lastDoc = collection.document(lastDocumentId).get().await()
            if (lastDoc.exists()) {
                queryBuilder = queryBuilder.startAfter(lastDoc)
            }
        }

        val snapshot = queryBuilder.get().await()
        val items = snapshot.documents.mapNotNull { it.toSafeResource() }

        Result.success(
            PageResult(
                items = items,
                lastDocumentId = snapshot.documents.lastOrNull()?.id,
                hasNext = items.size.toLong() == pageSize
            )
        )
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
            var queryBuilder = collection
                .whereGreaterThanOrEqualTo(FIELD_TITLE, query)
                .whereLessThanOrEqualTo(FIELD_TITLE, end)
                .orderBy(FIELD_TITLE)
                .limit(pageSize)

            if (lastDocumentId != null) {
                val lastDoc = collection.document(lastDocumentId).get().await()
                if (lastDoc.exists()) {
                    queryBuilder = queryBuilder.startAfter(lastDoc)
                }
            }

            val snapshot = queryBuilder.get().await()
            val items = snapshot.documents.mapNotNull { it.toSafeResource() }

            Result.success(
                PageResult(
                    items = items,
                    lastDocumentId = snapshot.documents.lastOrNull()?.id,
                    hasNext = items.size.toLong() == pageSize
                )
            )
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

    // ==================== MAPPERS & UTILS ====================

    private fun Resource.toMap(): Map<String, Any?> = mapOf(
        FIELD_ID to id,
        FIELD_TITLE to title,
        "url" to url,
        FIELD_TAGS to tags.toList(),
        // Use serverTimestamp for accuracy.
        // NOTE: In a 'merge' operation, this might overwrite original creation time
        // depending on your business logic requirements.
        FIELD_CREATED_AT to FieldValue.serverTimestamp()
    )

    /**
     * Converts a DocumentSnapshot to a Resource safely.
     *
     * Because the Resource domain class has an 'init' block that throws
     * IllegalArgumentException for invalid data, we MUST wrap creation in a try-catch.
     * Otherwise, one bad document in the DB will crash the entire list.
     */
    private fun com.google.firebase.firestore.DocumentSnapshot.toSafeResource(): Resource? {
        val id = getString(FIELD_ID) ?: return null
        val title = getString(FIELD_TITLE) ?: return null
        val url = getString("url") ?: return null
        val tags = (get(FIELD_TAGS) as? List<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet()

        return try {
            Resource(id, title, url, tags)
        } catch (e: Exception) {
            // Log the error internally if needed (e.g. Timber.e("Invalid resource $id: ${e.message}"))
            // Return null so mapNotNull skips this item
            null
        }
    }

    data class PageResult<T>(
        val items: List<T>,
        val lastDocumentId: String?,
        val hasNext: Boolean
    )
}