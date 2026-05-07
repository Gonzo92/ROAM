# How to Launch Roam

## Quick Start

The backend is already deployed and running. You can launch the Android app immediately by opening the project in Android Studio.

**Deployed Backend URL:** `https://roam-backend-nqus.onrender.com`  
**Health Check:** `https://roam-backend-nqus.onrender.com/health`

---

## Option A: Run Android App Only (Fastest)

### Prerequisites
- Android Studio (latest stable)
- JDK 17

### Steps
1. Open Android Studio → **File → Open** → select `android/` folder
2. Wait for Gradle sync to complete
3. Click **Run** (green play button) or `Shift + F10`
4. The app connects to the deployed backend automatically

> **Note:** First backend request may take 10-15 seconds due to Render cold start on free tier.

---

## Option B: Run Backend Locally + Android App

### Prerequisites
- Node.js 18+ (`node --version`)
- npm (`npm --version`)
- Android Studio (latest stable)
- JDK 17

### Step 1: Backend Setup

```bash
cd backend/

# Install dependencies
npm install

# Create environment file
copy .env.example .env

# Edit .env and add your API keys:
# GOOGLE_API_KEY=your_gemini_api_key_here
# PEXELS_API_KEY=your_pexels_api_key_here
```

**Get API Keys:**
- **Google Gemini:** https://ai.google.dev → Create API key
- **Pexels:** https://www.pexels.com/api/ → Register for free key

```bash
# Start the server
npm start
# Server runs on http://localhost:3000
```

### Step 2: Configure Android to Use Local Backend

Edit `android/app/src/main/kotlin/com/roam/data/ApiService.kt`:

```kotlin
// Change this line:
private const val BASE_URL = "https://roam-backend-nqus.onrender.com"

// To:
private const val BASE_URL = "http://10.0.2.2:3000"  // Android emulator
// Or for physical device on same WiFi:
private const val BASE_URL = "http://YOUR_LOCAL_IP:3000"
```

### Step 3: Run Android App

1. Open `android/` folder in Android Studio
2. Wait for Gradle sync
3. Click **Run** (green play button)

---

## Option C: Deploy Backend to Render

1. Push code to GitHub repository
2. Go to https://render.com → New Web Service
3. Connect your repository
4. Set root directory to `backend/`
5. Build command: `npm install`
6. Start command: `npm start`
7. Add environment variables:
   - `GOOGLE_API_KEY` — your Gemini API key
   - `PEXELS_API_KEY` — your Pexels API key
   - `NODE_ENV` — `production`
8. Deploy — Render will provide a URL like `https://your-app.onrender.com`
9. Update `BASE_URL` in `ApiService.kt` to your Render URL

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Health check — returns `{"status": "ok"}` |
| POST | `/api/search` | Search for travel options |

### POST /api/search Request Body

```json
{
  "origin": "Warsaw, Poland",
  "destination": "Paris, France",
  "startDate": "2026-06-15",
  "endDate": "2026-06-22",
  "budget": 3000,
  "currency": "USD"
}
```

### Response

```json
{
  "flights": [
    {
      "id": "abc123",
      "airline": "LOT Polish Airlines",
      "departureAirport": "WAW",
      "arrivalAirport": "CDG",
      "departureTime": "08:30",
      "arrivalTime": "10:45",
      "duration": "2h 30m",
      "price": 450,
      "direction": "outbound",
      "image": "https://content.r9cdn.net/rimg/provider-logos/airlines/v/LO.png",
      "bookingUrl": "https://www.google.com/travel/flights?q=Flights+from+waw+to+cdg+on+2026-06-15&curr=USD"
    }
  ],
  "hotels": [...],
  "attractions": [...],
  "restaurants": [...]
}
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Backend cold start timeout | First request takes 10-15s on Render free tier. Wait or retry. |
| 404 on Gemini API | Check `GOOGLE_API_KEY` in `.env`. Verify key is active at ai.google.dev |
| No photos showing | Check `PEXELS_API_KEY` in `.env`. Verify at pexels.com/api |
| Android can't connect to localhost | Use `10.0.2.2` for emulator, or your PC's IP for physical device |
| Gradle sync fails | Ensure JDK 17 is set in Android Studio (File → Project Structure) |
| `node_modules` missing | Run `npm install` in `backend/` directory |

---

## Project Structure

```
roam/
├── android/                    # Android app (Jetpack Compose)
│   ├── app/src/main/kotlin/com/roam/
│   │   ├── MainActivity.kt     # Entry point
│   │   ├── data/               # API, models, repository
│   │   ├── ui/screens/         # SearchScreen, ResultsScreen
│   │   ├── ui/theme/           # Colors, typography
│   │   └── viewmodel/          # TravelViewModel
│   └── app/src/main/res/       # Resources, icons, strings
│
└── backend/                    # Node.js Express server
    ├── server.js               # Express app, routes
    ├── services/
    │   └── travelService.js    # AI integration, data mapping
    ├── package.json
    └── .env.example            # Environment template
```
