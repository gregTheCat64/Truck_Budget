package ru.javacat.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE companies_table ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
    }
}