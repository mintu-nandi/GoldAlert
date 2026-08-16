package com.example.goldalert.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.goldalert.`data`.local.entity.ThresholdEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class ThresholdDao_Impl(
  __db: RoomDatabase,
) : ThresholdDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfThresholdEntity: EntityInsertAdapter<ThresholdEntity>

  private val __deleteAdapterOfThresholdEntity: EntityDeleteOrUpdateAdapter<ThresholdEntity>

  private val __updateAdapterOfThresholdEntity: EntityDeleteOrUpdateAdapter<ThresholdEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfThresholdEntity = object : EntityInsertAdapter<ThresholdEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `thresholds` (`id`,`targetPrice`,`direction`,`label`,`isActive`,`createdAt`,`lastTriggeredAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ThresholdEntity) {
        statement.bindLong(1, entity.id)
        statement.bindDouble(2, entity.targetPrice)
        statement.bindText(3, entity.direction)
        statement.bindText(4, entity.label)
        val _tmp: Int = if (entity.isActive) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        statement.bindLong(6, entity.createdAt)
        val _tmpLastTriggeredAt: Long? = entity.lastTriggeredAt
        if (_tmpLastTriggeredAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpLastTriggeredAt)
        }
      }
    }
    this.__deleteAdapterOfThresholdEntity = object : EntityDeleteOrUpdateAdapter<ThresholdEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `thresholds` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ThresholdEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfThresholdEntity = object : EntityDeleteOrUpdateAdapter<ThresholdEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `thresholds` SET `id` = ?,`targetPrice` = ?,`direction` = ?,`label` = ?,`isActive` = ?,`createdAt` = ?,`lastTriggeredAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ThresholdEntity) {
        statement.bindLong(1, entity.id)
        statement.bindDouble(2, entity.targetPrice)
        statement.bindText(3, entity.direction)
        statement.bindText(4, entity.label)
        val _tmp: Int = if (entity.isActive) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        statement.bindLong(6, entity.createdAt)
        val _tmpLastTriggeredAt: Long? = entity.lastTriggeredAt
        if (_tmpLastTriggeredAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpLastTriggeredAt)
        }
        statement.bindLong(8, entity.id)
      }
    }
  }

  public override suspend fun insertThreshold(threshold: ThresholdEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfThresholdEntity.insertAndReturnId(_connection, threshold)
    _result
  }

  public override suspend fun deleteThreshold(threshold: ThresholdEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfThresholdEntity.handle(_connection, threshold)
  }

  public override suspend fun updateThreshold(threshold: ThresholdEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfThresholdEntity.handle(_connection, threshold)
  }

  public override fun observeAllThresholds(): Flow<List<ThresholdEntity>> {
    val _sql: String = "SELECT * FROM thresholds ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("thresholds")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTargetPrice: Int = getColumnIndexOrThrow(_stmt, "targetPrice")
        val _columnIndexOfDirection: Int = getColumnIndexOrThrow(_stmt, "direction")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfLastTriggeredAt: Int = getColumnIndexOrThrow(_stmt, "lastTriggeredAt")
        val _result: MutableList<ThresholdEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ThresholdEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTargetPrice: Double
          _tmpTargetPrice = _stmt.getDouble(_columnIndexOfTargetPrice)
          val _tmpDirection: String
          _tmpDirection = _stmt.getText(_columnIndexOfDirection)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpIsActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpLastTriggeredAt: Long?
          if (_stmt.isNull(_columnIndexOfLastTriggeredAt)) {
            _tmpLastTriggeredAt = null
          } else {
            _tmpLastTriggeredAt = _stmt.getLong(_columnIndexOfLastTriggeredAt)
          }
          _item = ThresholdEntity(_tmpId,_tmpTargetPrice,_tmpDirection,_tmpLabel,_tmpIsActive,_tmpCreatedAt,_tmpLastTriggeredAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActiveThresholds(): Flow<List<ThresholdEntity>> {
    val _sql: String = "SELECT * FROM thresholds WHERE isActive = 1"
    return createFlow(__db, false, arrayOf("thresholds")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTargetPrice: Int = getColumnIndexOrThrow(_stmt, "targetPrice")
        val _columnIndexOfDirection: Int = getColumnIndexOrThrow(_stmt, "direction")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfLastTriggeredAt: Int = getColumnIndexOrThrow(_stmt, "lastTriggeredAt")
        val _result: MutableList<ThresholdEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ThresholdEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTargetPrice: Double
          _tmpTargetPrice = _stmt.getDouble(_columnIndexOfTargetPrice)
          val _tmpDirection: String
          _tmpDirection = _stmt.getText(_columnIndexOfDirection)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpIsActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpLastTriggeredAt: Long?
          if (_stmt.isNull(_columnIndexOfLastTriggeredAt)) {
            _tmpLastTriggeredAt = null
          } else {
            _tmpLastTriggeredAt = _stmt.getLong(_columnIndexOfLastTriggeredAt)
          }
          _item = ThresholdEntity(_tmpId,_tmpTargetPrice,_tmpDirection,_tmpLabel,_tmpIsActive,_tmpCreatedAt,_tmpLastTriggeredAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeThresholdById(id: Long): Flow<ThresholdEntity?> {
    val _sql: String = "SELECT * FROM thresholds WHERE id = ?"
    return createFlow(__db, false, arrayOf("thresholds")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTargetPrice: Int = getColumnIndexOrThrow(_stmt, "targetPrice")
        val _columnIndexOfDirection: Int = getColumnIndexOrThrow(_stmt, "direction")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfLastTriggeredAt: Int = getColumnIndexOrThrow(_stmt, "lastTriggeredAt")
        val _result: ThresholdEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTargetPrice: Double
          _tmpTargetPrice = _stmt.getDouble(_columnIndexOfTargetPrice)
          val _tmpDirection: String
          _tmpDirection = _stmt.getText(_columnIndexOfDirection)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpIsActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpLastTriggeredAt: Long?
          if (_stmt.isNull(_columnIndexOfLastTriggeredAt)) {
            _tmpLastTriggeredAt = null
          } else {
            _tmpLastTriggeredAt = _stmt.getLong(_columnIndexOfLastTriggeredAt)
          }
          _result = ThresholdEntity(_tmpId,_tmpTargetPrice,_tmpDirection,_tmpLabel,_tmpIsActive,_tmpCreatedAt,_tmpLastTriggeredAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteThresholdById(id: Long) {
    val _sql: String = "DELETE FROM thresholds WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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
