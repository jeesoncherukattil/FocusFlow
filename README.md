# FocusFlow

Complete Android digital detox & study app implementing the Product Requirements Document.

## Features

- **Task-categorized Pomodoro** — Circular progress ring, activity tags, auto focus/break cycles
- **App Blocker** — UsageStats polling + block screen when restricted apps open during focus
- **Reel & Short-form Counter** — AccessibilityService detects scrolls in Instagram/YouTube/TikTok + floating overlay
- **Biometric Adaptive Scheduling** — Health Connect heart-rate reader + interval adjustment logic
- **Smart Home Sync** — Controller stub that fires on focus start/end (ready for Tuya SDK)
- **Analytics** — Live data from Room (today’s minutes, session history)
- **Settings** — Toggles for biometrics, IoT, and reel counter
- **Foreground service** — Timer continues in background
- **Room database** — Persistent sessions and tags

## Tech Stack

Kotlin · Jetpack Compose · Material 3 · MVVM · Room · Navigation · Coroutines · Health Connect client · AccessibilityService · UsageStats

## Build & Run

1. Open the `FocusFlow` folder in Android Studio (Hedgehog+).
2. Sync Gradle.
3. Run on device/emulator (API 26+).
4. Grant Usage Access and Accessibility permissions to test blocking and reel counter.

## Production notes

- AccessibilityService requires Play Store declaration and prominent disclosure.
- Tuya integration needs the official SDK + user account linking.
- Health Connect requires user permission and a paired wearable for real data.
- App blocking works best with a combination of UsageStats + optional Accessibility for stricter enforcement.
