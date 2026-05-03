# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2024-05-03

### Added
- Initial project structure for "Sayang Anak" (disguised as Google Home).
- Dual-mode implementation: **Parent** and **Kids**.
- **Parent Mode Features**:
    - Real-time location tracking (10s interval).
    - Child management (Add/Switch Child).
    - Google Maps integration for location visualization.
    - Navigation to Google Maps app.
    - Notification trigger to wake up Child app.
    - Location history storage and viewing.
- **Kids Mode Features**:
    - Disguised UI as "System Update" screen.
    - Background location tracking and reporting to Firebase.
    - Boot receiver to start service on device startup.
    - Hidden Child ID view (Easter Egg).
- **Core Infrastructure**:
    - MVVM Architecture.
    - Firebase Firestore integration for data storage.
    - Foreground service for persistent location tracking.
    - Modern UI using Jetpack Compose.
