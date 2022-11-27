package com.example.myeria.data
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [SpotEntity::class],
version = 1,
    exportSchema = false
)
abstract class SpotDatabase : RoomDatabase(){
    abstract val dao:SpotDao
}