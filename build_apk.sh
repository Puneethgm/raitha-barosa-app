#!/bin/bash
echo "🏗️  Building Raitha-Bharosa Hub APK..."
echo "========================================="

# Check Java version
echo "Checking Java version..."
java -version

# Set Java 8 compatibility for now
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

# Try to build
echo ""
echo "Starting Gradle build..."
./gradlew clean build --no-daemon 2>&1 | tail -100

echo ""
echo "Build attempt complete. Checking for APK..."
find . -name "*.apk" -type f

