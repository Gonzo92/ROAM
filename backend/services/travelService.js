async function getPexelsImage(query) {
    try {
        const res = await fetch(
            `https://api.pexels.com/v1/search?query=${encodeURIComponent(query)}&per_page=1`,
            { headers: { Authorization: process.env.PEXELS_API_KEY } }
        );
        const data = await res.json();
        return data.photos?.[0]?.src?.large || null;
    } catch {
        return null;
    }
}

async function checkHotelAvailability(hotelName, destination, checkin, checkout) {
    try {
        const url = `https://www.booking.com/searchresults.html?ss=${encodeURIComponent(hotelName)}&ssne=${encodeURIComponent(destination)}&checkin=${checkin}&checkout=${checkout}`;
        const res = await fetch(url, {
            method: 'GET',
            headers: { 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36' }
        });
        const html = await res.text();
        const isUnavailable = html.includes("We don't have any availability") ||
                              html.includes("no properties") ||
                              html.includes("no results");
        return !isUnavailable;
    } catch {
        return true;
    }
}

function getAirlineLogo(airlineName) {
    const lower = airlineName.toLowerCase();
    const iataMap = {
        'lot': 'LO',
        'lufthansa': 'LH',
        'ryanair': 'FR',
        'wizz': 'W6',
        'easyjet': 'U2',
        'air france': 'AF',
        'klm': 'KL',
        'british airways': 'BA',
        'emirates': 'EK',
        'delta': 'DL',
        'united': 'UA',
        'american': 'AA',
        'qatar': 'QR',
        'turkish': 'TK',
        'swiss': 'LX',
        'iberia': 'IB',
        'norwegian': 'DY',
        'finnair': 'AY',
        'austrian': 'OS',
        'sas': 'SK',
        'air canada': 'AC',
        'jetblue': 'B6',
        'southwest': 'WN',
        'alitalia': 'AZ',
        'tap': 'TP',
        'aer lingus': 'EI',
        'brussels': 'SN',
        'vueling': 'VY',
        'air europa': 'UX',
        'play': 'OG',
        'eurowings': 'EW',
    };
    for (const [key, iata] of Object.entries(iataMap)) {
        if (lower.includes(key)) {
            return `https://content.r9cdn.net/rimg/provider-logos/airlines/v/${iata}.png`;
        }
    }
    return `https://cdn-icons-png.flaticon.com/512/723/723955.png`;
}

async function extractJson(text, destination, startDate, endDate, currency = 'USD') {
    console.log('[RAW AI TEXT]', text);
    if (!text || typeof text !== 'string') {
        console.error('[extractJson] Invalid text input');
        return null;
    }
    try {
        const jsonMatch = text.match(/\[[\s\S]*\]|\{[\s\S]*\}/);
        if (!jsonMatch) return null;
        let rawData = JSON.parse(jsonMatch[0]);

        const findValue = (obj, fragments, defaultValue = "") => {
            if (!obj || typeof obj !== 'object') return null;
            const keys = Object.keys(obj);
            for (const key of keys) {
                const lowerK = key.toLowerCase();
                if (fragments.some(f => lowerK.includes(f))) {
                    const val = obj[key];
                    if (val && String(val).trim() !== "" && val !== 0) return val;
                }
            }
            for (const key of keys) {
                if (obj[key] && typeof obj[key] === 'object') {
                    const result = findValue(obj[key], fragments, null);
                    if (result) return result;
                }
            }
            return defaultValue;
        };

        const getArr = (keyFrags) => {
            const found = findValue(rawData, keyFrags, []);
            return Array.isArray(found) ? found : [found].filter(i => i);
        };

        const isLegit = (item) => {
            const name = String(findValue(item, ['name', 'airline'], "")).toLowerCase();
            const desc = String(findValue(item, ['review', 'summary', 'desc'], "")).toLowerCase();
            const dest = destination.toLowerCase().split(',')[0].trim();
            if ((name.includes("warsaw") || desc.includes("warsaw")) && !name.includes("air") && !name.includes("flight")) {
                return false;
            }
            return true;
        };

        const flights = getArr(['flight', 'transportation', 'transit']).map(f => {
            const airline = findValue(f, ['airline', 'name', 'carrier'], "Flight");
            const depAirport = findValue(f, ['dep_airport', 'departure_airport', 'dep', 'from'], "");
            const arrAirport = findValue(f, ['arr_airport', 'arrival_airport', 'arr', 'to'], "");
            const depTime = findValue(f, ['dep_time', 'departure_time'], "");
            const arrTime = findValue(f, ['arr_time', 'arrival_time'], "");
            const depCode = String(depAirport).toLowerCase().replace(/[^a-z]/g, '');
            const arrCode = String(arrAirport).toLowerCase().replace(/[^a-z]/g, '');
            const googleFlightsUrl = `https://www.google.com/travel/flights?q=Flights+from+${depCode}+to+${arrCode}+on+${startDate}&curr=${currency}`;
            return {
                id: Math.random().toString(36).slice(2, 11),
                airline: String(airline),
                departure: String(depAirport),
                arrival: String(arrAirport),
                departureAirport: String(depAirport),
                arrivalAirport: String(arrAirport),
                departureTime: String(depTime),
                arrivalTime: String(arrTime),
                duration: String(findValue(f, ['flight_duration', 'duration'], "2h 30m")),
                price: parseInt(String(findValue(f, ['price', 'cost'], 0)).replace(/\D/g, '')) || 0,
                image: getAirlineLogo(airline),
                direction: String(findValue(f, ['direction', 'type'], "outbound")),
                bookingUrl: googleFlightsUrl,
                description: String(findValue(f, ['review', 'summary', 'desc', 'note'], "Flight to destination")),
            };
        });

        const hotelPromises = getArr(['hotel', 'accommodation']).filter(isLegit).map(async (h) => {
            const name = findValue(h, ['name', 'hotel'], "Hotel");
            const isAvailable = await checkHotelAvailability(name, destination, startDate, endDate);
            if (!isAvailable) return null;

            const pexelsImg = await getPexelsImage(`${name} hotel ${destination}`);
            const bookingUrl = `https://www.booking.com/searchresults.html?ss=${encodeURIComponent(String(name))}&ssne=${encodeURIComponent(destination)}&checkin=${startDate}&checkout=${endDate}`;

            return {
                id: Math.random().toString(36).slice(2, 11),
                name: String(name),
                location: String(findValue(h, ['loc', 'addr', 'city'], "City Center")),
                price: parseInt(String(findValue(h, ['price', 'night'], 0)).replace(/\D/g, '')) || 0,
                rating: parseFloat(String(findValue(h, ['rating', 'stars'], 4.5)).match(/[\d.]+/)) || 4.5,
                stars: parseInt(String(findValue(h, ['stars', 'star_count', 'star_rating'], 4))) || 4,
                reviewText: String(findValue(h, ['review_text', 'review', 'summary'], "Great stay, excellent location.")),
                image: pexelsImg || `https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800`,
                description: String(findValue(h, ['review', 'summary', 'desc', 'about'], "Top-rated accommodation.")),
                amenities: ["Free WiFi", "AC", "Pool"],
                bookingUrl: bookingUrl
            };
        });
        const hotels = (await Promise.all(hotelPromises)).filter(h => h !== null);

        const attractionPromises = getArr(['attraction', 'place', 'sight']).filter(isLegit).map(async (a) => {
            const name = findValue(a, ['name', 'attraction', 'place'], "Attraction");
            const pexelsImg = await getPexelsImage(`${name} ${destination}`);
            return {
                id: Math.random().toString(36).slice(2, 11),
                name: String(name),
                type: String(findValue(a, ['type', 'cat'], "Sightseeing")),
                rating: parseFloat(String(findValue(a, ['rating', 'stars'], 4.8)).match(/[\d.]+/)) || 4.8,
                stars: parseInt(String(findValue(a, ['stars', 'star_count'], 5))) || 5,
                image: pexelsImg || `https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800`,
                description: String(findValue(a, ['review', 'summary', 'desc', 'about'], "Must-see landmark.")),
                location: String(findValue(a, ['loc', 'addr'], "Destination Area")),
                ticketPrice: parseInt(String(findValue(a, ['price', 'ticket'], 0)).replace(/\D/g, '')) || 0,
                openHours: "9:00 - 18:00",
                googleMapsUrl: `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(name + ' ' + destination)}`
            };
        });
        const attractions = await Promise.all(attractionPromises);

        const restaurantPromises = getArr(['restaurant', 'dining', 'food']).filter(isLegit).map(async (r) => {
            const name = findValue(r, ['name', 'restaurant', 'eat'], "Restaurant");
            const pexelsImg = await getPexelsImage(`${name} restaurant ${destination}`);
            return {
                id: Math.random().toString(36).slice(2, 11),
                name: String(name),
                cuisine: String(findValue(r, ['cuisine', 'food'], "Local")),
                rating: parseFloat(String(findValue(r, ['rating', 'stars'], 4.3)).match(/[\d.]+/)) || 4.3,
                image: pexelsImg || `https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800`,
                priceRange: String(findValue(r, ['price', 'range'], "$$")),
                location: String(findValue(r, ['loc', 'addr'], "Destination Area")),
                description: String(findValue(r, ['review', 'summary', 'desc', 'about'], "Excellent dining experience.")),
                bookingUrl: "",
                googleMapsUrl: `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(name + ' ' + destination)}`
            };
        });
        const restaurants = await Promise.all(restaurantPromises);

        const finalData = { flights, hotels, attractions, restaurants };

        console.log('[DEBUG FINAL SEND]', JSON.stringify(finalData, null, 2));
        return finalData;
    } catch (e) {
        console.error("Mapping Error:", e.message);
        return null;
    }
}

async function callAiWithRetry(ai, model, prompt, dest, startDate, endDate, currency, retries = 3) {
    for (let i = 0; i < retries; i++) {
        try {
            const response = await ai.models.generateContent({
                model: model,
                contents: [{ role: 'user', parts: [{ text: prompt }] }],
                config: { tools: [{ googleSearch: {} }] }
            });
            const data = await extractJson(response.text, dest, startDate, endDate, currency);
            if (data && data.hotels.length > 0) return data;
        } catch (error) {
            console.error(`[AI] Error:`, error.message);
            if (i < retries - 1) await new Promise(res => setTimeout(res, 3000));
        }
    }
    return { flights: [], hotels: [], attractions: [], restaurants: [] };
}

export async function searchAllTravelData(origin, destination, startDate, endDate, budget, ai, modelName, currency = 'USD') {
    const model = modelName || 'gemini-2.5-flash-lite';
    const currencySymbol = currency === 'EUR' ? '€' : currency === 'PLN' ? 'zł' : '$';
    const prompt = `SEARCH FOR TRAVEL TO ${destination.toUpperCase()} ONLY. ALL PRICES MUST BE IN ${currency} (${currencySymbol}).
    1. Find 3 OUTBOUND flights from ${origin} to ${destination} AND 3 RETURN flights from ${destination} back to ${origin}. Each flight MUST have: airline, departure_airport (e.g. "WAW"), arrival_airport (e.g. "CDG"), departure_time (e.g. "08:30"), arrival_time (e.g. "10:45"), flight_duration (e.g. "2h 30m"), price (integer in ${currency}), direction (either "outbound" or "return").
    2. Find 3 hotels IN ${destination}. Each hotel MUST have: name, price (integer per night in ${currency}), rating (float 0-5), stars (integer 1-5), review_text (short text review, e.g. "Great location, friendly staff"), image_url (direct URL to a REAL photo of this specific hotel from Google Search, ending in .jpg/.png/.webp).
    3. Find 3 attractions IN ${destination}. Each attraction MUST have: name, type (e.g. "Museum", "Park"), rating (float 0-5), description (short text about what it is), image_url (direct URL to a REAL photo of this specific attraction from Google Search, ending in .jpg/.png/.webp).
    4. Find 3 restaurants IN ${destination}. Each restaurant MUST have: name, cuisine type, rating (float 0-5), price_range (e.g. "$", "$$", "$$$"), description (short text about the place), image_url (direct URL to a REAL photo of this specific restaurant from Google Search, ending in .jpg/.png/.webp).

    IMPORTANT: Use Google Search to find REAL image URLs for each specific place. Do NOT use placeholder or stock images. Search for actual photos of "[Place Name] [Destination]".

    STRICTLY FORBIDDEN: Do NOT include any attractions or restaurants from ${origin}.
    FORMAT: You MUST return a FLAT JSON array with keys: "flights", "hotels", "attractions", "restaurants".
    Each item must have EXACTLY these field names (snake_case):
    - flights: airline, departure_airport, arrival_airport, departure_time, arrival_time, flight_duration, price, direction
    - hotels: name, price, rating, stars, review_text, image_url
    - attractions: name, type, rating, description, image_url
    - restaurants: name, cuisine, rating, price_range, description, image_url`;

    return await callAiWithRetry(ai, model, prompt, destination, startDate, endDate, currency);
}
