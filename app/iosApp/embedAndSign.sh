#!/bin/bash
# embedAndSign.sh — Xcode Run Script Build Phase
# Delegates KMP framework compilation to Gradle, then lets Xcode embed & sign.
#
# Add this script as the FIRST "Run Script" build phase in the iosApp Xcode target.
# Uncheck "Based on dependency analysis" so it runs on every build.

set -e

REPO_ROOT="$(cd "$(dirname "$0")/../../../../" && pwd)"
cd "$REPO_ROOT"

if [ "$CONFIGURATION" = "Debug" ]; then
  echo "⚙️  Building SharedLogic (debug)…"
  ./gradlew :app:sharedLogic:embedAndSignAppleFrameworkForXcode
else
  echo "⚙️  Building SharedLogic (release)…"
  ./gradlew :app:sharedLogic:assembleSharedLogicReleaseXCFramework
fi
