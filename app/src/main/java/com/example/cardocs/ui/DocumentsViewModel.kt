package com.example.cardocs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.cardocs.CarDocsApp
import com.example.cardocs.data.CarRepository
import com.example.cardocs.data.Document
import com.example.cardocs.data.DocumentType

class DocumentsViewModel (
    private val repository: CarRepository,
    private val carId: Int
) : ViewModel() {
    val documents: StateFlow<List<Document>> = repository.getDocumentsForCar(carId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDocument(
        type: DocumentType,
        expirationDate: LocalDate,
        notificationDays: List<Int>,
        description: String = ""
    ) {
        viewModelScope.launch {
            val document = Document(
                carId = carId,
                type = type,
                expirationDate = expirationDate,
                notificationDays = notificationDays.sortedDescending(),
                description = description
            )
            repository.insertDocument(document)
        }
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            repository.deleteDocument(document)
        }
    }

    fun updateDocument(document: Document) {
        viewModelScope.launch {
            repository.updateDocument(document)
        }
    }

    companion object{
        fun provideFactory(carId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CarDocsApp)
                val repository = application.container.carRepository
                DocumentsViewModel(repository, carId)
            }
        }
    }
}