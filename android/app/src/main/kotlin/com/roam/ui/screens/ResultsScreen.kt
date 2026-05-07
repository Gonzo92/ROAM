package com.roam.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import com.roam.data.Attraction
import com.roam.data.Flight
import com.roam.data.Hotel
import com.roam.data.Restaurant
import com.roam.data.getCurrencySymbol
import com.roam.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    destination: String,
    flights: List<Flight>?,
    hotels: List<Hotel>?,
    attractions: List<Attraction>?,
    restaurants: List<Restaurant>?,
    currency: String = "USD",
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedOutboundId by remember { mutableStateOf<String?>(null) }
    var selectedReturnId by remember { mutableStateOf<String?>(null) }
    var showBookingSheet by remember { mutableStateOf(false) }
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }
    var showHotelSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currencySymbol = getCurrencySymbol(currency)

    val outboundFlights = (flights ?: emptyList()).filter { it.direction == "outbound" }
    val returnFlights = (flights ?: emptyList()).filter { it.direction == "return" }

    val selectedOutbound = outboundFlights.find { it.id == selectedOutboundId }
    val selectedReturn = returnFlights.find { it.id == selectedReturnId }
    val totalPrice = (selectedOutbound?.price ?: 0) + (selectedReturn?.price ?: 0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBeige)
    ) {
        // Header
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Your Trip to $destination",
                        style = Typography.headlineSmall,
                        color = Charcoal,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Curated recommendations",
                        style = Typography.bodySmall,
                        color = DarkGray
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Charcoal
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundBeige,
                titleContentColor = Charcoal
            )
        )

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = BackgroundBeige,
            contentColor = Charcoal,
            indicator = { tabPositions ->
                androidx.compose.material3.TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = Primary
                )
            }
        ) {
            listOf("Flights", "Hotels", "Attractions", "Restaurants").forEachIndexed { index, label ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = label,
                            style = Typography.titleMedium,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Charcoal else DarkGray
                        )
                    }
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> FlightsList(
                flights = flights ?: emptyList(),
                selectedOutbound = selectedOutboundId,
                selectedReturn = selectedReturnId,
                onOutboundSelected = { selectedOutboundId = it },
                onReturnSelected = { selectedReturnId = it },
                currency = currency
            )
            1 -> HotelsList(
                hotels = hotels ?: emptyList(),
                onBookHotel = { selectedHotel = it; showHotelSheet = true },
                currency = currency
            )
            2 -> AttractionsList(attractions ?: emptyList(), currency = currency)
            3 -> RestaurantsList(restaurants ?: emptyList(), currency = currency)
        }
    }

    // Bottom Summary Bar
    if (selectedTab == 0 && selectedOutboundId != null && selectedReturnId != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total: $currencySymbol$totalPrice",
                        style = Typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )
                    Text(
                        text = "${selectedOutbound?.airline} + ${selectedReturn?.airline}",
                        style = Typography.bodySmall,
                        color = DarkGray
                    )
                }
                Button(
                    onClick = { showBookingSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Book Flights", color = White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Booking BottomSheet
    if (showBookingSheet && selectedOutbound != null && selectedReturn != null) {
        ModalBottomSheet(
            onDismissRequest = { showBookingSheet = false },
            containerColor = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Your Selected Flights",
                    style = Typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal
                )

                // Outbound
                Text(
                    text = "Outbound",
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                )
                Text(
                    text = selectedOutbound.airline ?: "",
                    style = Typography.titleMedium,
                    color = Charcoal,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${selectedOutbound.departureAirport} → ${selectedOutbound.arrivalAirport}  ${selectedOutbound.departureTime}-${selectedOutbound.arrivalTime}",
                    style = Typography.bodyMedium,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Duration: ${selectedOutbound.duration}  •  $currencySymbol${selectedOutbound.price}",
                    style = Typography.bodySmall,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedOutbound.bookingUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Book This Flight", color = White)
                }

                // Return
                Text(
                    text = "Return",
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
                Text(
                    text = selectedReturn.airline ?: "",
                    style = Typography.titleMedium,
                    color = Charcoal,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${selectedReturn.departureAirport} → ${selectedReturn.arrivalAirport}  ${selectedReturn.departureTime}-${selectedReturn.arrivalTime}",
                    style = Typography.bodyMedium,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Duration: ${selectedReturn.duration}  •  $currencySymbol${selectedReturn.price}",
                    style = Typography.bodySmall,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedReturn.bookingUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Book This Flight", color = White)
                }

                // Total
                Divider(modifier = Modifier.padding(vertical = 24.dp), color = SecondaryLight)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total",
                        style = Typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )
                    Text(
                        text = "$currencySymbol$totalPrice",
                        style = Typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Hotel Booking BottomSheet
    if (showHotelSheet && selectedHotel != null) {
        ModalBottomSheet(
            onDismissRequest = { showHotelSheet = false },
            containerColor = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Reserve Your Stay",
                    style = Typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal
                )

                Text(
                    text = selectedHotel?.name ?: "",
                    style = Typography.titleLarge,
                    color = Charcoal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text(
                    text = selectedHotel?.location ?: "",
                    style = Typography.bodyMedium,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${selectedHotel?.rating ?: 0.0} • ${selectedHotel?.stars ?: 0}★",
                        style = Typography.bodyMedium,
                        color = Charcoal,
                        modifier = Modifier.padding(start = 4.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = selectedHotel?.reviewText ?: "",
                    style = Typography.bodyMedium,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 16.dp),
                    maxLines = 3
                )

                Text(
                    text = "$currencySymbol${selectedHotel?.price ?: 0}/night",
                    style = Typography.headlineSmall,
                    color = Charcoal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )

                val amenities = selectedHotel?.amenities ?: emptyList()
                if (amenities.isNotEmpty()) {
                    Text(
                        text = "Amenities:",
                        style = Typography.labelLarge,
                        color = Charcoal,
                        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                    )
                    amenities.forEach { amenity ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = amenity,
                                style = Typography.bodySmall,
                                color = Charcoal,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedHotel?.bookingUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Open Booking Link", color = White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FlightsList(
    flights: List<Flight>,
    selectedOutbound: String?,
    selectedReturn: String?,
    onOutboundSelected: (String) -> Unit,
    onReturnSelected: (String) -> Unit,
    currency: String = "USD"
) {
    val outbound = flights.filter { it.direction == "outbound" }
    val returnFlights = flights.filter { it.direction == "return" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (outbound.isNotEmpty()) {
            item {
                Text(
                    text = "Outbound Flights",
                    style = Typography.titleLarge,
                    color = Charcoal,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            items(outbound) { flight ->
                FlightCard(
                    flight = flight,
                    selected = (flight.id == selectedOutbound),
                    onSelect = { onOutboundSelected(flight.id ?: "") },
                    currency = currency
                )
            }
        }

        if (returnFlights.isNotEmpty()) {
            item {
                Text(
                    text = "Return Flights",
                    style = Typography.titleLarge,
                    color = Charcoal,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
            }
            items(returnFlights) { flight ->
                FlightCard(
                    flight = flight,
                    selected = (flight.id == selectedReturn),
                    onSelect = { onReturnSelected(flight.id ?: "") },
                    currency = currency
                )
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun FlightCard(flight: Flight, selected: Boolean, onSelect: () -> Unit, currency: String = "USD") {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Primary.copy(alpha = 0.05f) else White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) Primary else SecondaryLight
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Image
            AsyncImage(
                model = flight.image?.takeIf { it.isNotBlank() },
                contentDescription = flight.airline ?: "Airline",
                modifier = Modifier
                    .size(56.dp)
                    .background(BackgroundBeige, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentScale = ContentScale.Fit,
                placeholder = null,
                error = null
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = flight.airline ?: "Unknown Airline",
                    style = Typography.titleMedium,
                    color = Charcoal,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(flight.departureAirport?.takeIf { it.isNotBlank() } ?: "", style = Typography.bodyMedium, color = Charcoal, fontWeight = FontWeight.Bold)
                        Text(flight.departureTime?.takeIf { it.isNotBlank() } ?: "", style = Typography.bodySmall, color = DarkGray)
                    }
                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        tint = MediumGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(flight.arrivalAirport?.takeIf { it.isNotBlank() } ?: "", style = Typography.bodyMedium, color = Charcoal, fontWeight = FontWeight.Bold)
                        Text(flight.arrivalTime?.takeIf { it.isNotBlank() } ?: "", style = Typography.bodySmall, color = DarkGray)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Duration: ${flight.duration ?: "N/A"}",
                        style = Typography.bodySmall,
                        color = DarkGray
                    )
                    Text(
                        text = "•",
                        style = Typography.bodySmall,
                        color = DarkGray
                    )
                    Text(
                        text = if (flight.direction == "return") "Return" else "Outbound",
                        style = Typography.bodySmall,
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = "${getCurrencySymbol(currency)}${flight.price ?: 0}",
                    style = Typography.titleLarge,
                    color = Charcoal,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                RadioButton(
                    selected = selected,
                    onClick = { onSelect() },
                    colors = RadioButtonDefaults.colors(selectedColor = Primary, unselectedColor = MediumGray)
                )
            }
        }
    }
}

@Composable
fun HotelsList(
    hotels: List<Hotel>,
    onBookHotel: (Hotel) -> Unit,
    currency: String = "USD"
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(hotels) { hotel ->
            HotelCard(hotel, onBookHotel = { onBookHotel(hotel) }, currency = currency)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun HotelCard(hotel: Hotel, onBookHotel: () -> Unit, currency: String = "USD") {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryLight)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header with image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = hotel.image?.takeIf { it.isNotBlank() },
                    contentDescription = hotel.name ?: "Hotel",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = null,
                    error = null
                )

                // Overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                ),
                                startY = 100f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = hotel.name ?: "Boutique Hotel",
                        style = Typography.headlineSmall,
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = hotel.location ?: "Secret Location",
                            style = Typography.bodySmall,
                            color = White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            // Info section
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Warning,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${hotel.rating ?: 0.0} • ${hotel.stars ?: 0}★",
                            style = Typography.bodyMedium,
                            color = Charcoal,
                            modifier = Modifier.padding(start = 6.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "${getCurrencySymbol(currency)}${hotel.price ?: 0}/night",
                        style = Typography.titleLarge,
                        color = Charcoal,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = hotel.description ?: "A beautiful place to stay.",
                        style = Typography.bodyMedium,
                        color = DarkGray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Amenities
                    val amenities = hotel.amenities ?: emptyList()
                    if (amenities.isNotEmpty()) {
                        Text(
                            text = "Amenities",
                            style = Typography.labelLarge,
                            color = Charcoal,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        amenities.forEach { amenity ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = amenity,
                                    style = Typography.bodyMedium,
                                    color = Charcoal,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onBookHotel() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Reserve Stay", color = White, style = Typography.titleMedium)
                    }
                } else {
                    Text(
                        text = "Tap to see details",
                        style = Typography.bodySmall,
                        color = Primary,
                        modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun AttractionsList(attractions: List<Attraction>, currency: String = "USD") {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(attractions) { attraction ->
            AttractionCard(attraction)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun AttractionCard(attraction: Attraction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryLight)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = attraction.image?.takeIf { it.isNotBlank() },
                contentDescription = attraction.name ?: "Attraction",
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop,
                placeholder = null,
                error = null
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = attraction.name ?: "Landmark",
                        style = Typography.titleMedium,
                        color = Charcoal,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Warning,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${attraction.rating ?: 0.0}",
                            style = Typography.bodySmall,
                            color = Charcoal,
                            modifier = Modifier.padding(start = 4.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = " • ${attraction.type ?: "Attraction"}",
                            style = Typography.bodySmall,
                            color = DarkGray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Text(
                        text = attraction.description?.takeIf { it.isNotBlank() } ?: "",
                        style = Typography.bodySmall,
                        color = DarkGray,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                val context = androidx.compose.ui.platform.LocalContext.current
                Row(
                    modifier = Modifier
                        .clickable {
                            val url = attraction.googleMapsUrl?.takeIf { it.isNotBlank() }
                                ?: "https://www.google.com/maps/search/?api=1&query=${attraction.name}+${attraction.location}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = attraction.location ?: "",
                        style = Typography.bodySmall,
                        color = Primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantsList(restaurants: List<Restaurant>, currency: String = "USD") {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(restaurants) { restaurant ->
            RestaurantCard(restaurant)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryLight)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = restaurant.image?.takeIf { it.isNotBlank() },
                contentDescription = restaurant.name ?: "Restaurant",
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop,
                placeholder = null,
                error = null
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = restaurant.name ?: "Dining",
                        style = Typography.titleMedium,
                        color = Charcoal,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Warning,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${restaurant.rating ?: 0.0}",
                            style = Typography.bodySmall,
                            color = Charcoal,
                            modifier = Modifier.padding(start = 4.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = " • ${restaurant.cuisine ?: ""}",
                            style = Typography.bodySmall,
                            color = DarkGray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Text(
                        text = restaurant.description?.takeIf { it.isNotBlank() } ?: "",
                        style = Typography.bodySmall,
                        color = DarkGray,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                val context = androidx.compose.ui.platform.LocalContext.current
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.priceRange ?: "$$",
                        style = Typography.bodyMedium,
                        color = Charcoal,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .clickable {
                                val url = restaurant.googleMapsUrl?.takeIf { it.isNotBlank() }
                                    ?: "https://www.google.com/maps/search/?api=1&query=${restaurant.name}+${restaurant.location}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = restaurant.location ?: "",
                            style = Typography.bodySmall,
                            color = Primary,
                            modifier = Modifier.padding(start = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
