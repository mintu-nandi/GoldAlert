package com.example.goldalert.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goldalert.data.local.entity.ThresholdEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the thresholds table — full CRUD support.
 *
 * The UI layer observes [observeAllThresholds] and [observeActiveThresholds] via Flow.
 * All writes (insert / update / delete) are suspend functions, called from UseCases.
 */
@Dao
interface ThresholdDao {

    /**
     * Emits the complete threshold list (active and inactive) ordered by creation date.
     * The ThresholdList screen observes this to show all user-defined alerts.
     */
    @Query("SELECT * FROM thresholds ORDER BY createdAt DESC")
    fun observeAllThresholds(): Flow<List<ThresholdEntity>>

    /**
     * Emits only active thresholds.
     * The EvaluateThresholdsUseCase uses this to check which thresholds to test.
     */
    @Query("SELECT * FROM thresholds WHERE isActive = 1")
    fun observeActiveThresholds(): Flow<List<ThresholdEntity>>

    /**
     * Emits a single threshold by ID; useful for an edit screen.
     */
    @Query("SELECT * FROM thresholds WHERE id = :id")
    fun observeThresholdById(id: Long): Flow<ThresholdEntity?>

    /**
     * Creates a new threshold. Returns the auto-generated row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreshold(threshold: ThresholdEntity): Long

    /**
     * Persists edits to an existing threshold (price change, toggle, label update).
     */
    @Update
    suspend fun updateThreshold(threshold: ThresholdEntity)

    /**
     * Removes a threshold by entity reference (used with swipe-to-dismiss).
     */
    @Delete
    suspend fun deleteThreshold(threshold: ThresholdEntity)

    /**
     * Removes a threshold by its ID (used when only the ID is available).
     */
    @Query("DELETE FROM thresholds WHERE id = :id")
    suspend fun deleteThresholdById(id: Long)
}
