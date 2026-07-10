package com.example.cardocs.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cardocs.R
import com.example.cardocs.data.Document
import com.example.cardocs.data.DocumentType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    carId: Int,
    carName: String,
    onNavigateBack: () -> Unit,
    viewModel: DocumentsViewModel = viewModel(factory = DocumentsViewModel.provideFactory(carId))
) {
    val documents by viewModel.documents.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingDocument by remember { mutableStateOf<Document?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(carName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_document))
            }
        }
    ) { padding ->
        if (documents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_documents_yet))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(documents) { document ->
                    DocumentItem(
                        document = document,
                        onDelete = { viewModel.deleteDocument(document) },
                        onEdit = { editingDocument = document }
                    )
                }
            }
        }

        if (showDialog) {
            AddDocumentDialog(
                onDismiss = { showDialog = false },
                onConfirm = { type, expiryDate, notificationDays, description ->
                    viewModel.addDocument(type, expiryDate, notificationDays, description)
                    showDialog = false
                }
            )
        }

        editingDocument?.let { document ->
            EditDocumentDialog(
                document = document,
                onDismiss = { editingDocument = null },
                onConfirm = { updatedDocument ->
                    viewModel.updateDocument(updatedDocument)
                    editingDocument = null
                }
            )
        }
    }
}

@Composable
fun DocumentItem(
    document: Document,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val daysUntilExpiration = ChronoUnit.DAYS.between(today, document.expirationDate)
    val isExpired = daysUntilExpiration < 0
    val isExpiringSoon = daysUntilExpiration in 0..30

    val cardColors = when {
        isExpired -> Color(0xFFE57373)        // Material Red 300
        isExpiringSoon -> Color(0xFFFFB74D)   // Material Orange 300
        else -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColors)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.description.ifEmpty { document.type.toLocalizedString() },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.expires, document.expirationDate.format(DateTimeFormatter.ofPattern("dd, MMM yyyy"))),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = when {
                        isExpired -> stringResource(R.string.expired)
                        daysUntilExpiration == 0L -> stringResource(R.string.expires_today)
                        daysUntilExpiration == 1L -> stringResource(R.string.expires_tomorrow)
                        else ->  stringResource(R.string.days_remaining, daysUntilExpiration)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (document.notificationDays.isNotEmpty()){
                    Text(
                        text = stringResource(R.string.notifications_info, document.notificationDays.joinToString(", ")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_document),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { showDeleteDialog = true}){
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_document),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_document)) },
            text = {
                Text(stringResource(R.string.delete_document_confirmation, document.type.toLocalizedString()))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: DocumentType, expiryDate: LocalDate, notificationDays: List<Int>, description: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(DocumentType.INSURANCE) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var notificationDaysString by remember { mutableStateOf("30,7,1") }
    var expanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val showDescriptionField = selectedType == DocumentType.OTHER || selectedType == DocumentType.ROAD_TOLL || selectedType == DocumentType.VIGNETTE

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_document)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedType.toLocalizedString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.document_type)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DocumentType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.toLocalizedString()) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (showDescriptionField) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.description_optional)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = selectedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.expiry_date)) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notificationDaysString,
                    onValueChange = { notificationDaysString = it },
                    label = { Text(stringResource(R.string.notification_days)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDate?.let { date ->
                        val notificationDays = notificationDaysString.split(",").mapNotNull { it.trim().toIntOrNull() }
                        onConfirm(selectedType, date, notificationDays, description)
                    }
                },
                enabled = selectedDate != null
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDocumentDialog(
    document: Document,
    onDismiss: () -> Unit,
    onConfirm: (Document) -> Unit
) {
    var selectedType by remember { mutableStateOf(document.type) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(document.expirationDate) }
    var notificationDaysString by remember { mutableStateOf(document.notificationDays.joinToString(",")) }
    var expanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf(document.description) }
    var showDatePicker by remember { mutableStateOf(false) }

    val showDescriptionField = selectedType == DocumentType.OTHER || selectedType == DocumentType.ROAD_TOLL || selectedType == DocumentType.VIGNETTE

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_document)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedType.toLocalizedString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.document_type)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DocumentType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.toLocalizedString()) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (showDescriptionField) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.description_optional)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = selectedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.expiry_date)) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notificationDaysString,
                    onValueChange = { notificationDaysString = it },
                    label = { Text(stringResource(R.string.notification_days)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDate?.let { date ->
                        val notificationDays = notificationDaysString.split(",").mapNotNull { it.trim().toIntOrNull() }.sortedDescending()
                        onConfirm(document.copy(type = selectedType, expirationDate = date, notificationDays = notificationDays, description = description))
                    }
                },
                enabled = selectedDate != null
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
