package com.example.goldalert.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.goldalert.`data`.local.entity.GoldPriceEntity
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class GoldPriceDao_Impl(
  __db: RoomDatabase,
) : GoldPriceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGoldPriceEntity: EntityInsertAdapter<GoldPriceEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfGoldPriceEntity = object : EntityInsertAdapter<GoldPriceEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `gold_prices` (`id`,`pricePerOunce`,`currency`,`timestamp`,`source`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GoldPriceEntity) {
        statement.bindLong(1, entity.id)
        statement.bindDouble(2, entity.pricePerOunce)
        statement.bindText(3, entity.currency)
        statement.bindLong(4, entity.timestamp)
        statement.bindText(5, entity.source)
      }
    }
  }

  public override suspend fun insertPrice(price: GoldPriceEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfGoldPriceEntity.insert(_connection, price)
  }

  public override suspend fun insertPrices(prices: List<GoldPriceEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfGoldPriceEntity.insert(_connection, prices)
  }

  public override fun observeLatestPrice(): Flow<GoldPriceEntity?> {
    val _sql: String = "SELECT * FROM gold_prices ORDER BY timestamp DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("gold_prices")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPricePerOunce: Int = getColumnIndexOrThrow(_stmt, "pricePerOunce")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _result: GoldPriceEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPricePerOunce: Double
          _tmpPricePerOunce = _stmt.getDouble(_columnIndexOfPricePerOunce)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          _result = GoldPriceEntity(_tmpId,_tmpPricePerOunce,_tmpCurrency,_tmpTimestamp,_tmpSource)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllPrices(): Flow<List<GoldPriceEntity>> {
    val _sql: String = "SELECT * FROM gold_prices ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("gold_prices")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPricePerOunce: Int = getColumnIndexOrThrow(_stmt, "pricePerOunce")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _result: MutableList<GoldPriceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GoldPriceEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPricePerOunce: Double
          _tmpPricePerOunce = _stmt.getDouble(_columnIndexOfPricePerOunce)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          _item = GoldPriceEntity(_tmpId,_tmpPricePerOunce,_tmpCurrency,_tmpTimestamp,_tmpSource)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPriceCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM gold_prices"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOlderThan(olderThan: Long) {
    val _sql: String = "DELETE FROM gold_prices WHERE timestamp < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, olderThan)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
