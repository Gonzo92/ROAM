# 🔧 Complete Setup Guide - Roam Smart Travel

Step-by-step instructions to get Saily running on your phone.

## Prerequisites

- **Android Phone** (Android 7+)
- **Computer** with Node.js installed
- **Google Gemini API Key** (free tier available)

## Part 1: Backend Setup (Node.js)

### 1.1 Install Node.js
Download from: https://nodejs.org/  
(Choose LTS version)

### 1.2 Setup Backend

```bash
# Navigate to backend folder
cd backend

# Install dependencies
npm install

# Create .env file
copy .env.example .env
# OR on macOS/Linux:
cp .env.example .env
```

### 1.3 Get Google Gemini API Key

1. Visit: https://ai.google.dev
2. Click "Get API Key"
3. Create a new project or select existing
4. Generate API key
5. Copy the key

### 1.4 Add API Key to .env

Edit `backend/.env`:
```env
GOOGLE_API_KEY=your_api_key_here
PORT=3000
NODE_ENV=development
```

### 1.5 Test Backend

```bash
npm start
```

You should see:
```
🚀 Saily Backend running on http://localhost:3000
📡 API endpoint: POST http://localhost:3000/api/search
💚 Health check: GET http://localhost:3000/health
```

Test it:
```bash
curl http://localhost:3000/health
```

Should return: `{"status":"ok","timestamp":"2024-..."}`

Keep the backend running! (Don't close this terminal)

---

## Part 2: Building Android App

### Option A: Using Android Studio (Easiest)

1. **Install Android Studio**: https://developer.android.com/studio
2. **Open the Project**:
   - Launch Android Studio
   - "Open an Existing Project"
   - Select `android/` folder from Saily
3. **Wait for Gradle Sync** (5-10 minutes first time)
4. **Connect Your Phone**:
   - Enable Developer Mode (tap Build Number 7 times in Settings)
   - Enable USB Debugging in Developer Options
   - Connect via USB cable
5. **Build & Run**:
   - Click the green "Run" button in Android Studio
   - Select your device
   - Wait for build to complete (2-5 minutes)
   - App will install and launch on your phone

### Option B: Using Command Line

Prerequisites: Android SDK installed

```bash
cd android

# Clean build
./gradlew clean

# Build APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or one command:
./gradlew installDebug --daemon
```

APK location: `android/app/build/outputs/apk/debug/app-debug.apk`

### Option C: Manual APK Installation

If you have a pre-built APK:

```bash
# Using ADB (Android Debug Bridge)
adb install android/app/build/outputs/apk/debug/app-debug.apk
```

---

## Part 3: Configuring the App

### 3.1 Backend URL

If running backend on your computer:

**On Emulator:**
- Backend URL: `http://10.0.2.2:3000`

**On Real Phone (Same Network):**
- Get your computer's IP: 
  - Windows: `ipconfig` (look for IPv4 Address)
  - Mac/Linux: `ifconfig` (look for inet)
- Backend URL: `http://192.168.x.x:3000` (replace with your IP)

Update in `ApiClient.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:3000"  // for emulator
// OR
private const val BASE_URL = "http://192.168.1.100:3000"  // for real device
```

Then rebuild the app.

### 3.2 Network Configuration

Make sure:
1. ✅ Backend is running on port 3000
2. ✅ Your phone is on same WiFi network as computer
3. ✅ Firewall isn't blocking port 3000 (Windows Defender/Mac Firewall)

Test connection:
```bash
# From your phone browser, try:
http://192.168.x.x:3000/health
```

---

## Part 4: Testing the App

### 4.1 First Run

1. Launch Saily on your phone
2. Fill in:
   - **Where**: "Paris"
   - **From**: "2024-07-01"
   - **To**: "2024-07-10"
   - **Budget**: "5000"
3. Tap "Find My Trip"
4. Watch the beautiful results appear!

### 4.2 Troubleshooting

**"Can't connect to server"**
- ✅ Backend is running? (`npm start` in terminal)
- ✅ Same WiFi network?
- ✅ Correct IP address in ApiClient.kt?
- ✅ Firewall blocking port 3000?

**"App crashes on load"**
- ✅ Check Android logs: `adb logcat | grep saily`
- ✅ Try rebuilding: `./gradlew clean installDebug`

**"No results showing"**
- ✅ Backend is returning mock data by default
- ✅ Check backend console for errors

**"Emulator very slow"**
- Use real Android device instead
- Or enable emulator acceleration (HAXM)

---

## Part 5: Production Deployment (Optional)

### Build Release APK

```bash
cd android
./gradlew assembleRelease
```

APK: `android/app/build/outputs/apk/release/app-release.apk`

### Deploy Backend

For production, use:
- **Heroku**: Free tier available
- **Railway**: Simple deployment
- **Render**: Easy setup
- **AWS/Google Cloud**: More control

Example (Heroku):
```bash
cd backend
heroku create saily-backend
git push heroku main
```

---

## 🎉 You're Done!

Your Saily app is running! 

Next steps:
- Integrate real Gemini API calls
- Add actual Booking.com/Skyscanner APIs
- Enhance images and descriptions
- Share with friends!

---

## 📞 Need Help?

Check `README.md` for more details or visit:
- Android Docs: https://developer.android.com/docs
- Node.js Docs: https://nodejs.org/docs
- Gemini Docs: https://ai.google.dev/docs

Good luck! 🚀
