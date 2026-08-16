package com.example.goldalert.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.goldalert.`data`.local.dao.GoldPriceDao
import com.example.goldalert.`data`.local.dao.GoldPriceDao_Impl
import com.example.goldalert.`data`.local.dao.ThresholdDao
import com.example.goldalert.`data`.local.dao.ThresholdDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class GoldAlertDatabase_Impl : GoldAlertDatabase() {
  private val _goldPriceDao: Lazy<GoldPriceDao> = lazy {
    GoldPriceDao_Impl(this)
  }

  private val _thresholdDao: Lazy<ThresholdDao> = lazy {
    ThresholdDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "fc8a56c6be1cf900ebdfaee1c5cde0a2", "605b5d49814378c2577c9cebea76f203") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `gold_prices` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `pricePerOunce` REAL NOT NULL, `currency` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `source` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `thresholds` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `targetPrice` REAL NOT NULL, `direction` TEXT NOT NULL, `label` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `lastTriggeredAt` INTEGER)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'fc8a56c6be1cf900ebdfaee1c5cde0a2')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `gold_prices`")
        connection.execSQL("DROP TABLE IF EXISTS `thresholds`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsGoldPrices: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsGoldPrices.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGoldPrices.put("pricePerOunce", TableInfo.Column("pricePerOunce", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGoldPrices.put("currency", TableInfo.Column("currency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGoldPrices.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGoldPrices.put("source", TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysGoldPrices: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesGoldPrices: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoGoldPrices: TableInfo = TableInfo("gold_prices", _columnsGoldPrices, _foreignKeysGoldPrices, _indicesGoldPrices)
        val _existingGoldPrices: TableInfo = read(connection, "gold_prices")
        if (!_infoGoldPrices.equals(_existingGoldPrices)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |gold_prices(com.example.goldalert.data.local.entity.GoldPriceEntity).
              | Expected:
              |""".trimMargin() + _infoGoldPrices + """
              |
              | Found:
              |""".trimMargin() + _existingGoldPrices)
        }
        val _columnsThresholds: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsThresholds.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThresholds.put("targetPrice", TableInfo.Column("targetPrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThresholds.put("direction", TableInfo.Column("direction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThresholds.put("label", TableInfo.Column("label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThresholds.put("isActive", TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThresholds.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThresholds.put("lastTriggeredAt", TableInfo.Column("lastTriggeredAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysThresholds: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesThresholds: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoThresholds: TableInfo = TableInfo("thresholds", _columnsThresholds, _foreignKeysThresholds, _indicesThresholds)
        val _existingThresholds: TableInfo = read(connection, "thresholds")
        if (!_infoThresholds.equals(_existingThresholds)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |thresholds(com.example.goldalert.data.local.entity.ThresholdEntity).
              | Expected:
              |""".trimMargin() + _infoThresholds + """
              |
              | Found:
              |""".trimMargin() + _existingThresholds)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "gold_prices", "thresholds")
  }

  public override fun clearAllTables() {
    super.performClear(false, "gold_prices", "thresholds")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(GoldPriceDao::class, GoldPriceDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ThresholdDao::class, ThresholdDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun goldPriceDao(): GoldPriceDao = _goldPriceDao.value

  public override fun thresholdDao(): ThresholdDao = _thresholdDao.value
}
