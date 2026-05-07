package com.roam.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roam.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onSearch: (String, String, String, String, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var origin by remember { mutableStateOf("Warsaw, Poland") }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }

    val currencies = listOf("USD", "EUR", "PLN")

    val popularCities = listOf(
        "Warsaw, Poland", "Cracow, Poland", "Wroclaw, Poland", "Gdansk, Poland",
        "Paris, France", "London, UK", "New York, USA", "Tokyo, Japan",
        "Rome, Italy", "Barcelona, Spain", "Berlin, Germany",
        "Amsterdam, Netherlands", "Dubai, UAE", "Singapore",
        "Prague, Czech Republic"
    )

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDateState = rememberDatePickerState()
    val endDateState = rememberDatePickerState()

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDateState.selectedDateMillis?.let {
                        startDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                    }
                    showStartDatePicker = false
                }, colors = ButtonDefaults.textButtonColors(contentColor = Primary)) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }, colors = ButtonDefaults.textButtonColors(contentColor = Charcoal)) { Text("Cancel") }
            }
        ) {
            DatePicker(state = startDateState)
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDateState.selectedDateMillis?.let {
                        endDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                    }
                    showEndDatePicker = false
                }, colors = ButtonDefaults.textButtonColors(contentColor = Primary)) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }, colors = ButtonDefaults.textButtonColors(contentColor = Charcoal)) { Text("Cancel") }
            }
        ) {
            DatePicker(state = endDateState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(BackgroundBeige),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header without harsh gradient, organic and clean
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Saily",
                    tint = Primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Roam",
                    style = Typography.headlineLarge,
                    color = Charcoal
                )
                Text(
                    text = "Curated journeys, beautifully planned.",
                    style = Typography.bodyMedium,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Origin Input with Autocomplete
            CityAutocompleteInput(
                label = "Departure",
                value = origin,
                onValueChange = { origin = it },
                popularCities = popularCities,
                placeholder = "Where are you starting?"
            )

            // Destination Input with Autocomplete
            CityAutocompleteInput(
                label = "Destination",
                value = destination,
                onValueChange = { destination = it },
                popularCities = popularCities,
                placeholder = "Where to next?"
            )

            // Date Range
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReadOnlySearchInputField(
                    value = startDate,
                    onClick = { showStartDatePicker = true },
                    label = "Check In",
                    icon = Icons.Default.DateRange,
                    placeholder = "Select",
                    modifier = Modifier.weight(1f)
                )
                ReadOnlySearchInputField(
                    value = endDate,
                    onClick = { showEndDatePicker = true },
                    label = "Check Out",
                    icon = Icons.Default.DateRange,
                    placeholder = "Select",
                    modifier = Modifier.weight(1f)
                )
            }

            // Budget Input
            SearchInputField(
                value = budget,
                onValueChange = { budget = it },
                label = "Budget",
                icon = Icons.Default.AttachMoney,
                placeholder = "e.g. 5000"
            )

            // Currency Selector
            Column {
                Text(
                    text = "Currency",
                    style = Typography.labelLarge,
                    color = Charcoal,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    currencies.forEach { currency ->
                        val isSelected = currency == selectedCurrency
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCurrency = currency },
                            label = {
                                Text(
                                    text = currency,
                                    style = Typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                null
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = White,
                                selectedLeadingIconColor = White,
                                containerColor = White,
                                labelColor = Charcoal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Search Button (Organic Pill shape)
            Button(
                onClick = {
                    if (origin.isNotBlank() && destination.isNotBlank() && 
                        startDate.isNotBlank() && endDate.isNotBlank() && budget.isNotBlank()
                    ) {
                        onSearch(origin, destination, startDate, endDate, budget.toIntOrNull() ?: 0, selectedCurrency)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Craft My Journey",
                    style = Typography.titleMedium,
                    color = White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Cards (Paper/Organic style)
            InfoCard(
                icon = Icons.Default.AutoAwesome,
                title = "AI-Curated Itineraries",
                description = "Handpicked flights and boutique stays tailored to your taste."
            )
            InfoCard(
                icon = Icons.Default.Spa,
                title = "Relaxing Stays",
                description = "Find accommodations that offer true peace and comfort."
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityAutocompleteInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    popularCities: List<String>,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }
    val filteredCities = popularCities.filter { it.contains(value, ignoreCase = true) }

    Column {
        Text(
            text = label,
            style = Typography.labelLarge,
            color = Charcoal,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded && filteredCities.isNotEmpty(),
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    expanded = true
                },
                placeholder = { Text(placeholder, color = DarkGray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MediumGray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = SecondaryLight,
                    cursorColor = Primary
                ),
                singleLine = true,
                textStyle = Typography.bodyMedium
            )
            ExposedDropdownMenu(
                expanded = expanded && filteredCities.isNotEmpty(),
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(White)
            ) {
                filteredCities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city, style = Typography.bodyMedium) },
                        onClick = {
                            onValueChange(city)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadOnlySearchInputField(
    value: String,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = Typography.labelLarge,
            color = Charcoal,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder, color = DarkGray) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MediumGray,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            enabled = false, 
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = White,
                disabledTextColor = Charcoal,
                disabledPlaceholderColor = DarkGray,
                disabledBorderColor = SecondaryLight,
                disabledLeadingIconColor = MediumGray
            ),
            singleLine = true,
            textStyle = Typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = Typography.labelLarge,
            color = Charcoal,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = DarkGray) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MediumGray,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedBorderColor = Primary,
                unfocusedBorderColor = SecondaryLight,
                cursorColor = Primary
            ),
            singleLine = true,
            textStyle = Typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(BackgroundBeige, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Tertiary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = Typography.titleMedium,
                    color = Charcoal
                )
                Text(
                    text = description,
                    style = Typography.bodySmall,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
