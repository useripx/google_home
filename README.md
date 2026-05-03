# Sayang Anak (Google Home)

![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange.svg)

**Sayang Anak** is a safety-focused Android application designed to help parents monitor their children's locations discreetly. To ensure safety and prevent accidental deletion, the application is disguised as "Google Home" with a system-update-style interface in Kids mode.

## 🌟 Features

### 👨‍👩‍👧 Parent Mode
- **Real-time Monitoring**: Track child locations with updates every 10 seconds.
- **Multiple Children**: Manage and switch between multiple child profiles.
- **Interactive Maps**: View current location on an integrated Google Maps view.
- **Direct Navigation**: One-click button to open Google Maps for directions to the child's location.
- **Alive Trigger**: Send a notification trigger to ensure the Child app is active in the background.
- **History Log**: Review daily location history stored locally.

### 👶 Kids Mode
- **Disguised UI**: Appears as a "System Update" screen with randomized update dates to blend in.
- **Background Tracking**: Robust foreground service that tracks location even when the app is closed.
- **Auto-Start**: Automatically resumes tracking after device reboot.
- **Hidden Identity**: Child ID is hidden behind an "Easter Egg" (long-press on the system update text).

---

## 🛠 Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend**: Firebase Firestore
- **Maps API**: Google Maps SDK for Android
- **Background Work**: Android Foreground Service & Broadcast Receiver

---

## 🚀 Setting Up the Project

To run this project on a different machine, follow these steps:

### 1. Prerequisites
- Android Studio (Jellyfish or newer recommended)
- JDK 17
- Firebase Account
- Google Cloud Console Account (for Maps API)

### 2. Firebase Setup
1. Create a new project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android App to your Firebase project using the package name: `com.googlehome.protect`.
3. Download the `google-services.json` file.
4. Place `google-services.json` into the `app/` directory of this project.
5. Enable **Firestore Database** in the Firebase Console.
6. Set Firestore rules to allow read/write (ensure you secure them for production).

### 3. Google Maps API Setup
1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select your existing Firebase project.
3. Enable "Maps SDK for Android".
4. Create an API Key in "Credentials".
5. Open `app/src/main/AndroidManifest.xml` and replace the value in `com.google.android.geo.API_KEY` with your new API Key:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE" />
   ```

### 4. Build and Run
1. Open the project in Android Studio.
2. Sync Project with Gradle Files.
3. Run the app on a physical device (recommended for location features) or an emulator with Play Services.

---

## 📄 License
This project is for educational/personal safety purposes. Please respect privacy laws in your jurisdiction.

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome!
