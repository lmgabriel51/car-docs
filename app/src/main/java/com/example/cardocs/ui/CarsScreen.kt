package com.example.cardocs.ui

import android.app.AlertDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cardocs.data.Car
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlin.math.sin
import com.example.cardocs.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarsScreen(
    viewModel: CarsViewModel = viewModel(factory = CarsViewModel.Factory),
    onCarClick: (carId: Int, carName: String) -> Unit = { _, _ -> }
) {
    val cars by viewModel.cars.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingCar by remember { mutableStateOf<Car?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicule") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_car))
            }
        }
    ) { padding ->
        if (cars.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_cars_yet))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cars) { car ->
                    CarItem(
                        car = car,
                        onClick = { onCarClick(car.id, car.name) },
                        onDelete = { viewModel.deleteCar(car) },
                        onEdit = { editingCar = car}
                    )
                }
            }
        }

        if (showDialog) {
            AddCarDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, plate ->
                    viewModel.addCar(name, plate)
                    showDialog = false
                }
            )
        }

        editingCar?.let { car ->
            EditCarDialog(
                car = car,
                onDismiss = { editingCar = null},
                onConfirm = { updatedCar ->
                    viewModel.updateCar(updatedCar)
                    editingCar = null
                }
            )
        }
    }
}
@Composable
fun CarItem(
    car: Car,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Clickable Card - takes most of the space
        Card(
            modifier = Modifier.weight(1f),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = car.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (car.licensePlate.isNotEmpty()) {
                        Text(
                            text = car.licensePlate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        // Edit button
        IconButton(onClick = onEdit) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit_car),
                tint = MaterialTheme.colorScheme.primary
            )
        }


        // Delete button - separate from card, on the right
        IconButton(
            onClick = { showDeleteDialog = true }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_car),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_car)) },
            text = {
                Text(stringResource(R.string.delete_car_confirmation, car.name))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
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
@Composable
fun AddCarDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, plate: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_car)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.car_name)) },
                    placeholder = { Text("ex - Honda Civic") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = plate,
                    onValueChange = { newValue -> plate = newValue.uppercase() },
                    label = { Text(stringResource(R.string.license_plate)) },
                    placeholder = { Text("ex - B123ABC") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, plate)
                    }
                },
                enabled = name.isNotBlank()
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
}


@Composable
fun EditCarDialog(
    car: Car,
    onDismiss: () -> Unit,
    onConfirm: (Car) -> Unit
) {
    var name by remember { mutableStateOf(car.name) }
    var plate by remember { mutableStateOf(car.licensePlate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_car)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { newValue -> name = newValue},
                    label = { Text(stringResource(R.string.car_name))},
                    placeholder = { Text("ex - Honda")},
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = plate,
                    onValueChange = { newValue -> plate = newValue.uppercase() },
                    label = { Text(stringResource(R.string.license_plate)) },
                    placeholder = { Text("ex - B123ABC")},
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(car.copy(name = name, licensePlate = plate))
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        }

    )
}