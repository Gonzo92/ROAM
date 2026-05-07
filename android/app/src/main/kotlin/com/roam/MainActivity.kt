package com.roam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.roam.data.TravelUiState
import com.roam.ui.screens.ResultsScreen
import com.roam.ui.screens.SearchScreen
import com.roam.ui.theme.Black
import com.roam.ui.theme.DarkGray
import com.roam.ui.theme.Error
import com.roam.ui.theme.Primary
import com.roam.ui.theme.Typography
import com.roam.ui.theme.White
import com.roam.viewmodel.TravelViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[TravelViewModel::class.java]
        setContent {
            MaterialApp(viewModel)
        }
    }
}

@Composable
fun MaterialApp(viewModel: TravelViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var searchParams by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    var selectedCurrency by remember { mutableStateOf("USD") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White
    ) {
        Crossfade(targetState = uiState, label = "screen_transition") { state ->
            when (state) {
                is TravelUiState.Idle -> {
                    SearchScreen(
                        onSearch = { origin, destination, startDate, endDate, budget, currency ->
                            searchParams = Triple(startDate, endDate, destination)
                            selectedCurrency = currency
                            viewModel.searchTravelOptions(origin, destination, startDate, endDate, budget, currency)
                        }
                    )
                }
                is TravelUiState.Loading -> LoadingScreen()
                is TravelUiState.Success -> {
                    ResultsScreen(
                        destination = searchParams?.third ?: "Unknown",
                        flights = state.data.flights,
                        hotels = state.data.hotels,
                        attractions = state.data.attractions,
                        restaurants = state.data.restaurants,
                        currency = state.currency,
                        onBack = { viewModel.resetState() }
                    )
                }
                is TravelUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.resetState() }
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    val tips = listOf(
        "Did you know? Booking flights on Tuesdays can save you up to 15%.",
        "Tip: Always pack a universal power adapter for international trips.",
        "Fun Fact: Tokyo has more Michelin-starred restaurants than any other city.",
        "Tip: Rolling your clothes instead of folding them saves space and prevents wrinkles.",
        "Insight: Tuesday and Wednesday are generally the least crowded days to fly.",
        "Travel Hack: Download offline maps of your destination to navigate without using data.",
        "Did you know? Traveling during shoulder season (spring/fall) offers lower prices and fewer crowds.",
        "Tip: Keep a digital copy of your passport and important documents in a secure cloud storage.",
        "Fun Fact: Iceland has no mosquitoes at all!",
        "Tip: Notify your bank before traveling internationally to avoid blocked cards.",
        "Insight: Local street food is often the most authentic and budget-friendly way to eat.",
        "Travel Hack: Carry a reusable water bottle to stay hydrated and reduce plastic waste.",
        "Did you know? The Great Wall of China is not visible from space with the naked eye.",
        "Tip: Learning basic phrases in the local language can greatly enhance your experience.",
        "Fun Fact: France is the most visited country in the world.",
        "Tip: Wear your heaviest clothes on the plane to save weight and space in your luggage.",
        "Insight: Public transportation is the best way to see a city like a local.",
        "Travel Hack: Use a private browser window when searching for flights to avoid price hikes."
    )
    val images = listOf(
        "https://images.unsplash.com/photo-1499856871958-5b9627545d1a?auto=format&fit=crop&w=800&q=80", // Paris
        "https://images.unsplash.com/photo-1542051842-875c7ddc5ee9?auto=format&fit=crop&w=800&q=80", // Japan
        "https://images.unsplash.com/photo-1522083165195-3424ed129620?auto=format&fit=crop&w=800&q=80", // Coast
        "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?auto=format&fit=crop&w=800&q=80", // Mountains
        "https://images.unsplash.com/photo-1514890547357-a9ee2887a35f?auto=format&fit=crop&w=800&q=80", // Venice
        "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=800&q=80", // Santorini
        "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=800&q=80", // London
        "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?auto=format&fit=crop&w=800&q=80", // New York
        "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=800&q=80", // Bali
        "https://images.unsplash.com/photo-1552832230-c0197dd311b5?auto=format&fit=crop&w=800&q=80", // Rome
        "https://images.unsplash.com/photo-1583422409516-29151240ca27?auto=format&fit=crop&w=800&q=80"  // Barcelona
    )

    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(6500)
            currentIndex = (currentIndex + 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.roam.ui.theme.BackgroundBeige)
    ) {
        // Image layer with crossfade
        Crossfade(
            targetState = currentIndex % images.size,
            label = "image_fade",
            animationSpec = androidx.compose.animation.core.tween(1500)
        ) { index ->
            coil.compose.AsyncImage(
                model = images[index],
                contentDescription = "Travel Landscape",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }

        // Gradient Scrim to blend image into the beige background smoothly
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            com.roam.ui.theme.BackgroundBeige.copy(alpha = 0.5f),
                            com.roam.ui.theme.BackgroundBeige
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Content layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rotating Tip
            Crossfade(
                targetState = tips[currentIndex % tips.size],
                label = "tip_fade",
                animationSpec = androidx.compose.animation.core.tween(800)
            ) { tip ->
                Text(
                    text = tip,
                    style = Typography.titleLarge,
                    color = com.roam.ui.theme.Charcoal,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            // Minimalist AI Loading Indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = com.roam.ui.theme.Primary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "AI is curating your journey...",
                    style = Typography.bodyMedium,
                    color = com.roam.ui.theme.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = Error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Oops!",
                style = Typography.headlineSmall,
                color = Black
            )
            Text(
                text = message,
                style = Typography.bodyMedium,
                color = DarkGray,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text("Try Again", color = Color.White)
            }
        }
    }
}

