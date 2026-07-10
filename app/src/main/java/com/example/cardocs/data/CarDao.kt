package com.example.cardocs.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao{
    //Car operations
    @Query("SELECT * FROM cars ORDER BY name ASC")
    fun getAllCars(): Flow<List<Car>>

    @Query("SELECT * FROM cars WHERE id = :carId")
    suspend fun getCarById(carId: Int): Car?

    @Insert
    suspend fun insertCar(car: Car): Long

    @Update
    suspend fun updateCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)

    //Document operations

    @Query("SELECT * FROM documents WHERE carId = :carId ORDER BY expirationDate ASC")
    fun getDocumentsForCar(carId: Int): Flow<List<Document>>

    @Query("SELECT * FROM documents ORDER BY expirationDate ASC")
    fun getAllDocuments(): Flow<List<Document>>

    @Insert
    suspend fun insertDocument(document: Document)

    @Update
    suspend fun updateDocument(document: Document)

    @Delete
    suspend fun deleteDocument(document: Document)

    @Query("SELECT * FROM documents WHERE notificationDays != '' ORDER BY expirationDate ASC")
    fun getDocumentsWithPendingNotifications(): Flow<List<Document>>
}