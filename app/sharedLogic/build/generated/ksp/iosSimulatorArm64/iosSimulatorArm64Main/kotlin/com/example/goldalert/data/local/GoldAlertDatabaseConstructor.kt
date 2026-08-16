package com.example.goldalert.`data`.local

import androidx.room.RoomDatabaseConstructor

public actual object GoldAlertDatabaseConstructor : RoomDatabaseConstructor<GoldAlertDatabase> {
  override fun initialize(): GoldAlertDatabase = com.example.goldalert.`data`.local.GoldAlertDatabase_Impl()
}
