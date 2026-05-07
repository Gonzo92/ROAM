# Roam - AI Voyage Assistant

My main goal was to create something that I would like to use myself.
It has to be simple, stable and also good looking.

## Process

The whole process of making it started with a brainstorm:
- *What should the app have in the UI?*
- *How should the backend look like? — Whole app on device or only sending API requests to a cloud service?*

I decided to create an app which gathers all information from the user — Origin/Destination, Dates, Budget — at the start. Then it sends an API request to a DCP stored on Render with an AI API key to communicate with the world.

## How technologies were chosen

I brainstormed with Claude.ai using a precise prompt describing what I want and how it should look. Then I created a `.md` file with instructions to follow.

**Tools used:**

| Tool | Purpose |
|------|---------|
| **Claude.ai** | Brainstorm and ideas |
| **Claude Code** | Project plan and structure |
| **Opencode with Qwen 3.6 Plus** | Polishing backend, debugging, pushing repo to GitHub |
| **Gemini CLI** | Frontend (UI graphics, pictures, etc.) |
| **Gemini API** | Brain and eyes for searching flights, hotels, restaurants and attractions |
| **Render** | DCP hosting |
| **Android Studio** | Testing on real device |

## Whole App Workflow

```
Gathering data from User
    → Send data to DCP
    → Render creates precise prompt for Structured Output for Gemini API
    → Gemini API searches for data and returns it to DCP
    → DCP converts it and sends back to app
    → User can pick up options created for them
```

## AI Integration

Backend calls Gemini API to generate travel recommendations with a prompt that asks for:

- 3 outbound + 3 return flights with airline, times, duration, price
- 3 hotels with name, price, rating, review text
- 3 attractions with name, type, description
- 3 restaurants with name, cuisine, price range

Everything returned as strict JSON with specific field names.

## What worked

1. **Quickly setting up the initial app structure, screens, and server connections.** Tasks that usually take hours to write from scratch took me minutes.
2. **Creating visual elements** like cards, menus, and lists. I described what I wanted and the AI generated working code for the UI.
3. **Building a system that finds and organizes data correctly**, even if the names change slightly between requests, to avoid inconsistencies.
4. **When the app crashed, I simply shared the error logs with the AI.** It quickly figured out the cause and fixed it.
5. **Setting up how the app talks to the server and manages background tasks.** Who would know better how to handle AI than AI itself?

**Biggest win:** Originally 4 parallel API calls to Gemini (hitting rate limits and creating problems). AI helped me refactor into a single "orchestrator" prompt — **75% quota savings**.

## What did not work

1. **Gemini kept changing field names** (like `depTime` vs `departureTime`) and sometimes sent the wrong data format. I built a mapping engine that finds the right info regardless of the label and cleans up the mess.
2. **Switching between different Gemini versions often broke the app.** I moved the model settings to a config file so I can swap versions instantly and added a system to retry failed requests.
3. **Third-party image sites like Unsplash often shut down or blocked access**, leaving empty spaces. I switched to Pexels API and a more stable system for airline logos to make pictures always show up.
4. **Gemini sometimes generated code for newer versions of Android libraries** that I wasn't using yet. I had to ask Qwen to remove unsupported parameters to make the code compile.
5. **AI sometimes suggested places from the starting city instead of the destination.** I added a "legitimacy check" to filter out results that don't belong to the destination city.

> **Where AI helped the most:** I could go from idea to working feature in 10-15 minutes instead of hours. This let me test more ideas and discard bad ones faster.

## Summary

| Metric | Value |
|--------|-------|
| Development time | ~5 hours from idea to functional app |
| Total commits | ~50 |
| Major bugs fixed | 42 |
| Backend files | 3 (server.js, travelService.js, package.json) |
| Android files | ~15 |
| API integrations | Gemini, Pexels, Google Flights, Booking.com, Clearbit/Kayak |

## What I would do better next time?

Start with defining JSON schema instead of iterating AI prompts.

## What I'm proud of?

Every commit fixed a real user-facing issue. No feature was shipped without testing.

## Security Notes

API keys are stored in `.env` (not committed to version control).
