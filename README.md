# Unsent Messenger (Android App)

An Android application built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Room Database** that captures and logs incoming Messenger notifications locally on your device, allowing you to view messages even after the sender has clicked **"Unsend for everyone"**.

---

## ✨ Features

- ⚡ **Real-time Capture**: Intercepts notifications from Facebook Messenger (`com.facebook.orca`), Messenger Lite, Instagram Direct, and WhatsApp.
- 🚫 **Unsent Message Detection**: Automatically highlights and marks messages that were retracted/unsent by the sender.
- 📱 **Cross-Platform Sender Support**: Works when the sender is using **iOS (iPhone/iPad)**, Android, Mac, or Windows Web.
- 🔒 **100% Private & On-Device**: All chat logs are stored locally in an offline SQLite/Room database. No servers, no tracking, 0 cloud uploads.
- 🔋 **Reboot & Battery Resilient**: Auto-rebinds upon device reboot (`BOOT_COMPLETED`) with battery optimization exemption support.
- 🧪 **Built-in Diagnostic Tool**: Simulate test unsent messages from Settings without needing a second phone.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.1
- **UI Framework**: Jetpack Compose (Material 3)
- **Database**: AndroidX Room (SQLite) with KSP
- **Async & Reactive**: Kotlin Coroutines & `Flow`
- **Core Service**: Android `NotificationListenerService` (`android.permission.BIND_NOTIFICATION_LISTENER_SERVICE`)
- **Navigation**: Navigation Compose

---

## 🚀 How to Build & Install on Your Phone

### Step 1: Open in Android Studio
1. Download & install [Android Studio](https://developer.android.com/studio) (free).
2. Open Android Studio -> Select **Open** -> Navigate to:
   ```
   C:\Users\chris\.gemini\antigravity\scratch\MessengerUnsentViewer
   ```
3. Let Gradle sync dependencies.

### Step 2: Connect Phone & Enable Developer Options
1. On your Android phone: Go to **Settings > About Phone** -> Tap **Build Number** 7 times to enable Developer Options.
2. Go to **Settings > System / Developer Options** -> Enable **USB Debugging**.
3. Plug your phone into your PC with a USB cable.

### Step 3: Run / Build the APK
- **Direct Run**: In Android Studio, select your connected phone from the device dropdown at the top and click the green **Run (▶)** button.
- **Generate APK to share**: In Android Studio, go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**. Transfer the `.apk` file to your phone and tap to install.

---

## ⚙️ First-Time Phone Setup (Crucial)

When you open the app on your phone:
1. **Grant Notification Access**:
   - The app will prompt you with a setup banner.
   - Tap **Enable Notification Access** -> Toggle **ON** for `Unsent Messenger`.
2. **Disable Battery Optimization**:
   - Tap **Ignore Battery Optimization** so Android won't shut down the service in the background.

---

## 🧪 Testing It Out

### Method 1: Using the Built-in Simulator
1. Open the app -> Tap the **Settings (⚙️)** icon in the top right.
2. Tap **"Simulate Test Messenger Unsend"**.
3. Go back to the main chat list -> You will see a test conversation showing a normal message and an unsent message highlighted in red!

### Method 2: Real Messenger Test
1. Have a friend (or another account on iPhone or Android) send you a message on Messenger while your phone is on the home screen or locked.
2. Have them unsend the message ("Unsend for everyone").
3. Open **Unsent Messenger** -> The deleted message will be preserved in your chat history!
