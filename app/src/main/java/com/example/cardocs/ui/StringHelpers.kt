package com.example.cardocs.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.cardocs.R
import com.example.cardocs.data.DocumentType


@Composable
fun DocumentType.toLocalizedString(): String {
    return when (this) {
        DocumentType.INSURANCE -> stringResource(R.string.document_type_insurance)
        DocumentType.TECHNICAL_INSPECTION -> stringResource(R.string.document_type_technical_inspection)
        DocumentType.ROAD_TOLL -> stringResource(R.string.document_type_road_toll)
        DocumentType.VIGNETTE -> stringResource(R.string.document_type_vignette)
        DocumentType.OTHER -> stringResource(R.string.document_type_other)
    }
}

fun DocumentType.toLocalizedString(context: Context): String {
    return when (this) {
        DocumentType.INSURANCE -> context.getString(R.string.document_type_insurance)
        DocumentType.TECHNICAL_INSPECTION -> context.getString(R.string.document_type_technical_inspection)
        DocumentType.ROAD_TOLL -> context.getString(R.string.document_type_road_toll)
        DocumentType.VIGNETTE -> context.getString(R.string.document_type_vignette)
        DocumentType.OTHER -> context.getString(R.string.document_type_other)
    }
}