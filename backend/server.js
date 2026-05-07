import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import { GoogleGenAI } from '@google/genai';
import { searchAllTravelData } from './services/travelService.js';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

const ai = new GoogleGenAI({ apiKey: process.env.GOOGLE_API_KEY });
// Using gemini-2.5-flash-lite as primary in 2026 for better availability
const MODEL_NAME = 'gemini-2.5-flash-lite'; 

app.get('/health', (req, res) => {
    res.json({ status: 'ok' });
});

app.post('/api/search', async (req, res) => {
    try {
        const { origin, destination, startDate, endDate, budget, currency } = req.body;
        if (!destination || !startDate || !endDate || !budget) {
            return res.status(400).json({ error: 'Missing fields' });
        }

        const startFrom = origin || 'Warsaw, Poland';
        const selectedCurrency = currency || 'USD';
        console.log(`[Search] [Final Fix] ${startFrom} -> ${destination} | Currency: ${selectedCurrency}`);

        const response = await searchAllTravelData(startFrom, destination, startDate, endDate, budget, ai, MODEL_NAME, selectedCurrency);
        res.json(response);
    } catch (error) {
        console.error('[Search] Fatal error:', error.message);
        res.status(500).json({ error: error.message });
    }
});

app.listen(PORT, () => {
    console.log(`🚀 Roam AI [STABLE 2026] ready on port ${PORT}`);
});
