package com.example.cardocs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.cardocs.CarDocsApp
import com.example.cardocs.data.CarRepository
import com.example.cardocs.data.Car
import com.example.cardocs.data.Document
import kotlinx.coroutines.flow.WhileSubscribed

class CarsViewModel(private val repository: CarRepository) : ViewModel(){
    val cars: StateFlow<List<Car>> = repository.getAllCars()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCar(name: String, licencePlate: String){
        viewModelScope.launch {
            val car = Car(
                name = name,
                licensePlate = licencePlate
            )
            repository.insertCar(car)
        }
    }

    fun deleteCar(car: Car){
        viewModelScope.launch {
            repository.deleteCar(car)
        }
    }

    fun updateCar(car: Car) {
        viewModelScope.launch {
            repository.updateCar(car)
        }
    }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CarDocsApp)
                CarsViewModel(application.container.carRepository)
            }
        }
    }
}