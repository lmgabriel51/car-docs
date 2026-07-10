package com.example.cardocs.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.time.LocalDate

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val licensePlate: String = ""
)

@Entity(
    tableName = "documents",
    foreignKeys = [
        ForeignKey(
            entity = Car::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carId: Int,
    val type: DocumentType,
    val expirationDate: LocalDate,
    val notificationDays: List<Int> = listOf(30, 7, 1).sortedDescending(),
    val description: String = ""
)


enum class DocumentType{
    INSURANCE,
    TECHNICAL_INSPECTION,
    VIGNETTE,
    ROAD_TOLL,
    OTHER
}
