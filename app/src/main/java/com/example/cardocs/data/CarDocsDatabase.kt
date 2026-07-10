package com.example.cardocs.data

import androidx.room.TypeConverters
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [Car::class, Document::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CarDocsDatabase : RoomDatabase(){
    abstract fun carDao(): CarDao

    companion object{
        @Volatile
        private var INSTANCE: CarDocsDatabase? = null

        fun getDatabase(context: Context): CarDocsDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CarDocsDatabase::class.java,
                    "car_docs_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}