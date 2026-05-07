# 🚀 Roam Backend

Express.js server for Roam Smart Travel Planning with Google Gemini integration.

## Quick Start

```bash
npm install
cp .env.example .env
# Add your GOOGLE_API_KEY to .env
npm start
```

Server runs on `http://localhost:3000`

## 📡 API Endpoints

### Health Check
```bash
curl http://localhost:3000/health
```

### Travel Search
```bash
curl -X POST http://localhost:3000/api/search \
  -H "Content-Type: application/json" \
  -d '{
    "destination": "Paris",
    "startDate": "2024-07-01",
    "endDate": "2024-07-10",
    "budget": 5000
  }'
```

## 🔑 Environment Variables

```env
GOOGLE_API_KEY=your_gemini_api_key
PORT=3000
NODE_ENV=development
```

Get your API key: https://ai.google.dev

## 📁 Structure

```
backend/
├── server.js              # Main Express server
├── services/
│   └── travelService.js   # Search logic & Gemini calls
├── package.json
├── .env.example
└── README.md
```

## 🔄 Architecture

Currently using **mock data** for MVP. Next phase:

1. Real Gemini API integration
2. Booking.com & Skyscanner APIs
3. Web scraping for images
4. Caching layer

## 🛠️ Development

Watch mode:
```bash
npm run dev
```

Debug:
```bash
DEBUG=* npm start
```

## 📦 Dependencies

- `express` - Web framework
- `cors` - CORS middleware
- `@google/generative-ai` - Gemini API SDK
- `dotenv` - Environment variables
- `axios` - HTTP client (for real APIs)
- `cheerio` - HTML parsing (for scraping)

## 🎯 Next: Gemini Integration

Update `travelService.js`:

```javascript
export async function searchFlights(destination, startDate, endDate, budget, model) {
  const prompt = `Find flights to ${destination} from ${startDate} to ${endDate} with budget $${budget}`;
  const result = await model.generateContent(prompt);
  // Parse result and return structured data
}
```

---

Built with ❤️ for Saily
