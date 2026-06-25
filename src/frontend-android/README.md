# GoTogether Android Frontend

This is the native Android implementation of the GoTogether app, migrated from the React frontend. It follows the **Neo-Brutalism** design system and integrates with the Spring Boot backend.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Asynchronous**: Coroutines & Flow
- **Networking**: Retrofit & OkHttp
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: Coil

## Project Structure
- `api/`: Retrofit interfaces and client configuration.
- `model/`: Data classes matching the backend API.
- `viewmodel/`: State management and business logic.
- `ui/theme/`: Colors, Typography, and Compose Theme.
- `ui/components/`: Reusable Neo-Brutalism widgets (NeoCard, NeoButton).
- `ui/screens/`: Main app screens (Challenges, Profile, Map).

## Setup Instructions
1. **Backend**: Ensure the Spring Boot backend is running (typically on port 8080).
2. **API URL**: 
   - If using the **Android Emulator**, the app is configured to use `http://10.0.2.2:8080` to reach your host's localhost.
   - If using a **Physical Device**, update `RetrofitClient.kt` with your machine's local IP address.
3. **Build**: Run `./gradlew assembleDebug` or open the project in Android Studio.

## Styling (Neo-Brutalism)
The app uses a custom `GoTogetherTheme` that enforces:
- High-contrast black borders (`2.dp`).
- Vibrant brand colors (`BrandYellow`).
- Bold, heavy typography for headings.

## Future Additions
- **Map Integration**: The `MapScreen` currently uses a placeholder. Integration with OpenStreetMap (via Osmdroid) or Google Maps is planned for the next phase.
- **QR Scanner**: For challenge participation verification (User Story #9).
- **AI Chatbot**: Integration with the backend AI assistant service.
