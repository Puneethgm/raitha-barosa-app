# 📦 Raitha-Bharosa Hub - APK Build Instructions

## ⚠️ Prerequisites Required

Before building the APK, ensure you have:

### Required Software
- **Java 17** (or higher)
- **Android SDK** (API 26+)
- **Android Build Tools** (34.0.0+)
- **Gradle** (8.2.0 or use gradlew wrapper)

### Option 1: Build on Your Mac/Windows/Linux (Recommended)

#### macOS
```bash
# Install Java 17 using Homebrew
brew install openjdk@17

# Verify installation
java -version
# Should show: openjdk version "17.x.x"

# Set JAVA_HOME (add to ~/.zshrc or ~/.bash_profile)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH
```

#### Ubuntu/Debian Linux
```bash
# Install Java 17
sudo apt-get update
sudo apt-get install openjdk-17-jdk

# Verify
java -version
```

#### Windows
```powershell
# Download from: https://www.oracle.com/java/technologies/downloads/#java17
# Or use Chocolatey:
choco install openjdk17
```

### Option 2: Use Android Studio (Easiest)

1. **Open Android Studio**
2. **File → Open → Select project folder**
3. **Let it sync Gradle**
4. **Build → Build Bundle(s)/APK(s) → Build APK**
5. **APK will be at:** `app/build/outputs/apk/debug/app-debug.apk`

## 🏗️ Building from Command Line

Once you have Java 17 installed:

```bash
cd "/path/to/intership proj"

# Clean previous builds
./gradlew clean

# Build APK (debug)
./gradlew assembleDebug

# Build release APK (requires signing)
./gradlew assembleRelease
```

## 📊 Expected Build Output

```
BUILD SUCCESSFUL in 2m 34s
59 actionable tasks: 59 executed

APK Location:
✅ app/build/outputs/apk/debug/app-debug.apk
```

## 🚀 After Building

### Install on Device
```bash
# Connect Android device/emulator first
adb install app/build/outputs/apk/debug/app-debug.apk

# Or from Android Studio:
# Run button (▶️) → Select device
```

### APK File Details
- **Name:** `app-debug.apk`
- **Size:** ~5-10 MB
- **Package:** `com.raithabharosa.hub`
- **Version:** 1.0.0

## 🔧 Troubleshooting Build Issues

### "Java 17 not found"
```bash
# Check Java version
java -version

# If Java 8 is shown instead:
export JAVA_HOME=/path/to/java17
export PATH=$JAVA_HOME/bin:$PATH
```

### "SDK not found"
```bash
# Set Android SDK location
export ANDROID_SDK_ROOT=~/Android/Sdk
export ANDROID_HOME=~/Android/Sdk
export PATH=$ANDROID_HOME/tools/bin:$PATH
```

### Gradle build fails
```bash
# Clear gradle cache
rm -rf ~/.gradle/caches

# Rebuild
./gradlew clean build
```

### Memory issues
```bash
# Increase heap size
export GRADLE_OPTS="-Xmx4g"
./gradlew build
```

## 📱 Installing APK

### Method 1: ADB (Command Line)
```bash
# Connect device via USB
adb devices

# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Method 2: Android Studio
```
Run button (▶️) → Select connected device → Install
```

### Method 3: Direct File Transfer
```bash
# Copy APK to phone (via USB/Bluetooth)
# Then tap on APK file in file manager to install
```

## ✅ Build Configuration

Our project is configured with:
```
Android {
  minSdk: 26 (Android 8.0)
  targetSdk: 34 (Android 14)
  compileSdk: 34
}

Java {
  sourceCompatibility: 17
  targetCompatibility: 17
}
```

## 📋 Build Steps Explained

1. **Compile Sources**
   - Kotlin code → Bytecode
   - Resource files merged
   - Dependencies resolved

2. **Process Resources**
   - AndroidManifest.xml processed
   - Strings (EN + Kannada) compiled
   - Styles applied

3. **Build DAX/DEX**
   - Convert bytecode to DEX
   - Optimize for Android runtime

4. **Package APK**
   - Bundle all resources
   - Create APK file
   - Sign with debug key (auto)

5. **Output**
   - `app/build/outputs/apk/debug/app-debug.apk`

## 🎯 Full Build Process

```bash
# 1. Navigate to project
cd "/home/infaira/Desktop/intership proj"

# 2. Download dependencies (first time only)
./gradlew dependencies

# 3. Clean previous builds
./gradlew clean

# 4. Build debug APK
./gradlew assembleDebug

# 5. Verify APK exists
ls -lh app/build/outputs/apk/debug/app-debug.apk

# 6. Install on device
adb install app/build/outputs/apk/debug/app-debug.apk

# 7. Run app (if adb, otherwise use Android Studio)
adb shell am start -n com.raithabharosa.hub/.MainActivity
```

## 📊 Build Time Estimates

- **First build:** 3-5 minutes (dependencies downloaded)
- **Clean build:** 2-3 minutes
- **Incremental build:** 30-60 seconds

## 🔑 Release Build (Optional)

For Google Play Store submission:

```bash
# Generate keystore (one-time)
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias raithabharosa

# Build release APK
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=release.keystore \
  -Pandroid.injected.signing.store.password=YOUR_PASSWORD \
  -Pandroid.injected.signing.key.alias=raithabharosa \
  -Pandroid.injected.signing.key.password=YOUR_PASSWORD
```

## ✨ What You'll Get

When build completes:
- ✅ Ready-to-install APK
- ✅ Debug version (easier testing)
- ✅ Can be installed on any Android 8.0+ device
- ✅ All features working
- ✅ Offline mode operational

## 📞 Getting Help

If build fails:
1. Check Java 17 is installed
2. Check Android SDK is set
3. Try `./gradlew clean build`
4. Check Java/Gradle logs for errors

## 🎓 Build Output Files

```
app/build/
├── outputs/
│   ├── apk/
│   │   ├── debug/
│   │   │   └── app-debug.apk ← INSTALL THIS
│   │   └── release/
│   │       └── app-release-unsigned.apk
│   └── bundle/
│       └── (for Google Play)
├── intermediates/
├── generated/
└── ...
```

---

**Ready to build?** Follow the steps above on your local machine! 🚀

For questions: puneethgm@bhaai.org.in
