package com.example.medtrack.repository.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {

        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `patients` (
                    `id` TEXT NOT NULL,
                    `createdBy` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `age` INTEGER NOT NULL,
                    `emergencyContact` TEXT,
                    PRIMARY KEY(`id`),
                    FOREIGN KEY(`createdBy`)
                        REFERENCES `profiles`(`id`)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS `index_patients_createdBy`
                ON `patients` (`createdBy`)
                """.trimIndent()
            )
        }
    }
}