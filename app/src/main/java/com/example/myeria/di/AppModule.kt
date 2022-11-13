package com.example.myeria.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myeria.data.SpotDao
import com.example.myeria.data.SpotDatabase
import com.example.myeria.data.SpotEntity
import com.example.myeria.data.SpotRepositoryImpl
import com.example.myeria.domain.model.Spot
import com.example.myeria.domain.repository.SpotRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideGameDao(database: SpotDatabase): SpotDao {
        return database.dao
    }
    @Singleton
    @Provides
    fun provideSpotDatabase(@ApplicationContext context: Context, spotDaoProvider: Provider<SpotDao>): SpotDatabase {
        return Room.databaseBuilder(
            context,
            SpotDatabase::class.java,
            "spots.db"
        )  .addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Initialize the database with the first game
                    CoroutineScope(SupervisorJob()).launch {

                        spotDaoProvider.get().insertParkingSpot(SpotEntity(false))
                    }
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)

                    // Ensure there is always one game in the database
                    // This will capture the case of the app storage
                    // being cleared
                    // This uses the existing instance, so the DB won't leak
                    CoroutineScope(SupervisorJob()).launch {
                        val gameDao = spotDaoProvider.get()

                        if (gameDao.getParkingSpots().equals(0) ) {
                            gameDao.insertParkingSpot(SpotEntity(true))
                        }
                    }
                }
            }
        ).build()
    }
    @Singleton
    @Provides
    fun provideSpotRepository(db:SpotDatabase): SpotRepository {
        return SpotRepositoryImpl(db.dao)
    }
}