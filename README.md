# GoldAlert

GoldAlert is a Kotlin Multiplatform (KMP) application designed to monitor gold prices and provide timely notifications when user-defined price thresholds are reached. It offers seamless support for both Android and iOS platforms, utilizing shared business logic and native background synchronization mechanisms.

## Features

- **Real-time Price Monitoring**: Keep track of the latest gold prices and trends.
- **Custom Price Thresholds**: Set personalized high and low price alerts.
- **Background Synchronization**: The app syncs prices and evaluates thresholds every 5 minutes in the background, even when closed.
- **Native Notifications**: Receive instant local notifications on your device when a threshold is triggered.
- **Android Home Screen Widget**: Keep track of the current gold prices right from your Android home screen.
- **Cross-Platform Shared Logic**: Built using Kotlin Multiplatform (KMP) to maximize code reuse across Android and iOS.

## Architecture

GoldAlert follows a modern mobile architecture pattern with a strong separation of concerns, heavily leveraging KMP for shared domain and data layers. The shared business logic strictly adheres to **Clean Architecture** principles, organizing the code into distinct layers:
- **Presentation Layer**: Contains cross-platform ViewModels managing the UI state and interactions.
- **Domain Layer**: Houses pure business logic and use cases (e.g., `EvaluateThresholdsUseCase`), completely independent of any frameworks or platforms.
- **Data Layer**: Implements the repositories and manages data sources like network APIs and local storage.

![GoldAlert Architecture](/Users/mintunandi/.gemini/antigravity-ide/brain/fe450cbb-4429-45f1-8a34-adf2097b2a1c/architecture_diagram_1786914595917.jpg)

### Flow Description

1. **User Interaction**: Users interact with the native UI to view prices and configure threshold alerts. These preferences are stored securely in the shared Room Database, which is an encrypted SQLite database using SQLCipher.
2. **Background Sync**: 
    - **Android**: Uses `WorkManager` (specifically chained `OneTimeWorkRequest`s) to wake up every 5 minutes and fetch the latest price.
    - **iOS**: Uses `BGAppRefreshTask` registered with `BGTaskScheduler` to perform periodic background fetches.
3. **Evaluation**: When a background sync occurs, the native layers invoke the shared `EvaluateThresholdsUseCase`.
4. **Triggering Alerts**: The use case queries the `GoldRepository` for active thresholds. If a threshold condition is met (e.g., current price > max threshold), the use case deactivates the threshold in the database and fires a callback to the platform-specific notification service.
5. **Notification**: The native platform service (`NotificationManager` on Android, `UNUserNotificationCenter` on iOS) displays the alert to the user.

## Platform Support

- **Android**: 
  - Supported via the `androidApp` module.
  - Requires Android 8.0 (API level 26) or higher for robust notification channel support.
  - Background execution powered by AndroidX `WorkManager`.

- **iOS**:
  - Supported via the `iosApp` module.
  - Requires iOS 13.0 or higher for modern background task scheduling.
  - Background execution powered by `BGTaskScheduler` with `fetch` and `processing` background modes enabled.

## Development Setup

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest version recommended)
- [Xcode](https://developer.apple.com/xcode/) (latest version recommended for iOS development)
- [Kotlin Multiplatform Mobile (KMM) Plugin](https://kotlinlang.org/docs/kmm-plugin-and-deps.html) for Android Studio
- JDK 17+

### Running the App

#### Android
1. Open the project in Android Studio.
2. Select the `androidApp` run configuration.
3. Select an Android Emulator or connected physical device.
4. Click **Run**.

#### iOS
1. Open the project in Android Studio or Xcode.
2. Ensure the Kotlin shared module is built.
3. In Android Studio, select the `iosApp` run configuration, choose an iOS Simulator, and click **Run**.
4. Alternatively, open `iosApp/iosApp.xcworkspace` in Xcode, select your target device/simulator, and hit **Play**.

## Libraries and Technologies Used

- **Kotlin Multiplatform**: Core shared logic.
- **Room**: Shared local SQLite database for cross-platform data persistence, secured with SQLCipher for full database encryption.
- **Coroutines & Flow**: Asynchronous programming and reactive data streams.
- **WorkManager**: Android reliable background processing.
- **BGTaskScheduler**: iOS background task execution.