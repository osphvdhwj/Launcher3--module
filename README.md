# Infinity X Secured (Recents Hide Mod)

An LSPosed module designed specifically for AOSP/Quickstep launchers (like Infinity X Launcher) that visually hides the Recents menu thumbnails for user-selected applications.

## Features
* **Dynamic Selection:** Choose which apps to hide via a sleek, Glassmorphism/AMOLED UI.
* **Privacy First:** Replaces app snapshots with a pure AMOLED Black canvas and Neon Cyan text, preventing sensitive data from leaking when scrolling through recent apps.
* **Preserved Functionality:** Hijacks the drawing canvas (`onDraw`) rather than the view binding, ensuring tap targets and animations remain 100% intact.

## Requirements
* Android 10+ (Tested up to Android 15 / API 35)
* Root (Magisk/KernelSU)
* LSPosed Framework
* Target: `com.android.launcher3` (Infinity X Launcher)

## Build Instructions
1. Clone the repository.
2. Build via Gradle: `./gradlew assembleRelease`
3. Install, enable in LSPosed, and restart your launcher.
