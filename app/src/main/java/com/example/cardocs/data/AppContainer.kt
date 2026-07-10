package com.example.cardocs.data

import android.content.Context

interface AppContainer {
    val carRepository: CarRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val carRepository: CarRepository by lazy {
        CarRepository(CarDocsDatabase.getDatabase(context).carDao())
    }
}
