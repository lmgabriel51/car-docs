package com.example.cardocs.data

import kotlinx.coroutines.flow.Flow

class CarRepository(private val carDao: CarDao){
    //Car operations
    fun getAllCars(): Flow<List<Car>> = carDao.getAllCars()

    suspend fun getCarById(carId: Int): Car? = carDao.getCarById(carId)

    suspend fun insertCar(car: Car): Long = carDao.insertCar(car)

    suspend fun updateCar(car: Car) = carDao.updateCar(car)

    suspend fun deleteCar(car: Car) = carDao.deleteCar(car)

    //Document operations

    fun getDocumentsForCar(carId: Int): Flow<List<Document>> =
        carDao.getDocumentsForCar(carId)

    suspend fun insertDocument(document: Document) = carDao.insertDocument(document)

    suspend fun updateDocument(document: Document) = carDao.updateDocument(document)

    suspend fun deleteDocument(document: Document) = carDao.deleteDocument(document)

    fun getDocumentsWithPendingNotifications(): Flow<List<Document>> =
        carDao.getDocumentsWithPendingNotifications()
}