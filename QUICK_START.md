# ⚡ Quick Start - Roam Smart Travel

Get Saily running in 3 steps!

## 🎯 Quick Setup (15 minutes)

### Step 1: Backend (5 min)

```bash
# Windows
cd backend
setup.bat

# macOS/Linux
cd backend
bash setup.sh
```

Then edit `.env` and add your Google API key from https://ai.google.dev

Start backend:
```bash
npm start
```

✅ Backend running on http://localhost:3000

### Step 2: Android Build (5 min)

**Option A: Android Studio (Easiest)**
1. Download: https://developer.android.com/studio
2. Open → Select `android/` folder
3. Wait for Gradle sync
4. Click green "Run" button
5. Select your phone/emulator

**Option B: Command Line (if you have Android SDK)**
```bash
cd android
./gradlew installDebug
```

✅ App installed on your device

### Step 3: Test It! (5 min)

1. Open Saily app on your phone
2. Enter:
   - Where: "Paris"
   - From: "2024-07-01"
   - To: "2024-07-10"  
   - Budget: "5000"
3. Tap "Find My Trip" 🎉

---

## 🆘 Troubleshooting

### Backend won't start?
```bash
# Check Node.js installed
node --version
npm --version

# Reinstall
npm install
npm start
```

### App can't connect to backend?
- Is backend running? (Check terminal)
- Same WiFi network?
- Try emulator? Use IP: `10.0.2.2:3000` in ApiClient.kt
- On real phone? Use your PC IP address

### Gradle error?
```bash
cd android
./gradlew clean
./gradlew build
```

### Port 3000 already in use?
```bash
# Windows
netstat -ano | findstr :3000

# Mac/Linux
lsof -i :3000

# Use different port
PORT=3001 npm start
# Update app ApiClient.kt to use 3001
```

---

## 📱 Your App is Ready!

**Next Steps:**
- Add real Gemini API integration
- Connect to Booking.com/Skyscanner APIs
- Enhance images and UI
- Build Release APK for Play Store

**Resources:**
- Full guide: `README.md`
- Setup details: `SETUP.md`
- Android docs: https://developer.android.com/docs
- Gemini API: https://ai.google.dev/docs

---

**Questions?** Check `README.md` or the docs above!

Happy traveling! 🚀✈️
