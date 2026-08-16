package com.example.goldalert.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goldalert.data.local.entity.GoldPriceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the gold_prices table.
 *
 * All observation methods return [Flow] so downstream consumers (Repository →
 * ViewModel → UI) react automatically whenever the Repository writes a new price.
 * No caller needs to manually refresh — this is the RDLA reactive chain.
 */
@Dao
interface GoldPriceDao {

    /**
     * Emits the single most-recent price row whenever it changes.
     * This is the primary feed for the Dashboard screen.
     */
    @Query("SELECT * FROM gold_prices ORDER BY timestamp DESC LIMIT 1")
    fun observeLatestPrice(): Flow<GoldPriceEntity?>

    /**
     * Emits the full price history ordered newest-first.
     * Used for historical charts / sparklines.
     */
    @Query("SELECT * FROM gold_prices ORDER BY timestamp DESC")
    fun observeAllPrices(): Flow<List<GoldPriceEntity>>

    /**
     * Called by the Repository after a successful API fetch (SSOT write).
     * The Flow collectors above will automatically re-emit after this insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrice(price: GoldPriceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrices(prices: List<GoldPriceEntity>)

    @Query("SELECT COUNT(*) FROM gold_prices")
    suspend fun getPriceCount(): Int

    @Query("DELETE FROM gold_prices WHERE timestamp < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)
}
