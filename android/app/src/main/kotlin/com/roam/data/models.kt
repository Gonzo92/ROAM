package com.roam.data

import com.google.gson.annotations.SerializedName

// Request Models
data class TravelSearchRequest(
    val origin: String = "Warsaw, Poland",
    val destination: String,
    val startDate: String,
    val endDate: String,
    val budget: Int,
    val currency: String = "USD",
    val adults: Int = 1,
    val children: Int = 0
)

// Response Models
data class TravelSearchResponse(
    val flights: List<Flight>? = emptyList(),
    val hotels: List<Hotel>? = emptyList(),
    val attractions: List<Attraction>? = emptyList(),
    val restaurants: List<Restaurant>? = emptyList()
)

data class Flight(
    val id: String? = "",
    val airline: String? = "Unknown Airline",
    val departure: String? = "",
    val arrival: String? = "",
    val departureAirport: String? = "",
    val arrivalAirport: String? = "",
    val departureTime: String? = "",
    val arrivalTime: String? = "",
    val duration: String? = "",
    val direction: String? = "outbound",
    val price: Int? = 0,
    val image: String? = "",
    val bookingUrl: String? = ""
)

data class Hotel(
    val id: String? = "",
    val name: String? = "Unknown Hotel",
    val location: String? = "",
    val price: Int? = 0,
    val rating: Double? = 0.0,
    val stars: Int? = 0,
    val reviewText: String? = "",
    val image: String? = "",
    val description: String? = "",
    val amenities: List<String>? = emptyList(),
    val bookingUrl: String? = ""
)

data class Attraction(
    val id: String? = "",
    val name: String? = "Unknown",
    val type: String? = "",
    val rating: Double? = 0.0,
    val stars: Int? = 0,
    val image: String? = "",
    val description: String? = "",
    val location: String? = "",
    val ticketPrice: Int? = 0,
    val openHours: String? = "",
    val googleMapsUrl: String? = ""
)

data class Restaurant(
    val id: String? = "",
    val name: String? = "Unknown",
    val cuisine: String? = "",
    val rating: Double? = 0.0,
    val image: String? = "",
    val priceRange: String? = "$$",
    val location: String? = "",
    val description: String? = "",
    val bookingUrl: String? = "",
    val googleMapsUrl: String? = ""
)

// UI State
sealed class TravelUiState {
    object Idle : TravelUiState()
    object Loading : TravelUiState()
    data class Success(val data: TravelSearchResponse, val currency: String = "USD") : TravelUiState()
    data class Error(val message: String) : TravelUiState()
}

fun getCurrencySymbol(currency: String): String = when (currency.uppercase()) {
    "EUR" -> "€"
    "PLN" -> "zł"
    else -> "$"
}
