# Changelog

All notable changes to this project will be documented in this file.

## [1.2.0] - 2026-05-03

### Added
- **Direct Maps Navigation**: Added a shortcut button in the Parent Dashboard to open the Google Maps app and instantly start navigating to the child's exact coordinates. Includes a web-browser fallback if the app is missing.
- **Map Zoom Controls**: Enabled native zoom in/out controls on the parent's map view for easier navigation.

### Changed
- **Enhanced Polyline UI/UX**: Improved the route history drawing by changing the line color to bright red, increasing thickness, and adding a green start marker.
- **Performance Optimization**: Limited the polyline drawing to the last 100 location points to prevent rendering lag on extended usage.

### Fixed
- **Permission Denied Crash**: Fixed a fatal crash in the Parent Dashboard occurring when Firestore Security Rules block read access.
- **Anonymous Authentication**: Integrated `FirebaseAuth.signInAnonymously()` seamlessly on app startup to satisfy secure Firestore Rules without requiring explicit user login.

## [1.1.0] - 2026-05-03

### Added
- **Fullscreen Map UI**: Redesigned Parent Dashboard utilizing `BottomSheetScaffold` for maximum map visibility.
- **Polyline Tracking**: Visual route drawing on Google Maps to display the child's movement history.
- **Multiple Children Support**: Ability to track and quickly switch between multiple children using UI chips.
- **Child Renaming Feature**: Easily assign and change custom names for tracked devices via a prompt dialog after connection or through an edit icon.
- **Battery Optimization Bypass Prompt**: Added forced prompt on Kids mode to ignore battery optimizations, preventing the system from killing the background tracker.

### Fixed
- Fixed build error caused by `<adaptive-icon>` API level mismatch by moving adaptive icons to `mipmap-anydpi-v26`.
- Adjusted `minSdk` to 24 (Android 7.0) to broaden target device compatibility.

## [1.0.0] - 2026-05-03

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
