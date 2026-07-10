package com.example.cardocs.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate?{
        return dateString?.let{ LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromDocumentType(type: DocumentType): String {
        return type.name
    }

    @TypeConverter
    fun toDocumentType(value: String): DocumentType {
        return DocumentType.valueOf(value)
    }

    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toIntList(value: String): List<Int>{
        return if (value.isEmpty()) emptyList()
            else value.split(",").map { it.toInt() }
    }
}
