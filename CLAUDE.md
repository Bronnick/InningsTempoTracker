# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (minify + shrinkResources enabled)
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.inntemp.inningstempotracker.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Lint checks
./gradlew lint
```

## Project Architecture

**Stack:** Kotlin · Jetpack Compose · MVVM · Koin DI · Room · DataStore · Navigation Compose · MPAndroidChart
**Package:** `com.inntemp.inningstempotracker` · Min SDK 24 · Target SDK 36

### Package Structure

```
ui/
  navigation/   NavGraph.kt, Screen.kt (all routes and bottom tab nav)
  preloader/    PreloaderScreen + ViewModel
  onboarding/   OnboardingScreen (3-page HorizontalPager) + ViewModel
  home/         HomeScreen + ViewModel
  match/        CreateMatchScreen + ViewModel
  over/         OverInputScreen + ViewModel (bottom sheet form)
  library/      InningsLibraryScreen + ViewModel (search/filter)
  detail/       InningsDetailScreen + ViewModel (MPAndroidChart bar)
  edit/         EditInningScreen + ViewModel
  analytics/    AnalyticsScreen + ViewModel (MPAndroidChart line comparison)
  settings/     SettingsScreen + ViewModel (export/import/theme/clear)
  theme/        AppTheme.kt (LocalAppTheme token facade), Theme.kt, Color.kt
data/
  db/           AppDatabase, entities/, dao/, relations/
  model/        Models.kt (MatchWithStats, MatchDetail, Over, ExportData, PhaseType, MatchFormat)
  repository/   InningsRepository interface + InningsRepositoryImpl
  datastore/    AppPreferences (onboardingCompleted, themeSelection)
di/             AppModule.kt (single Koin module)
utils/          DateUtils.kt
```

### Key Architectural Decisions

**Theme system** — All colors, typography, shapes, and spacing are accessed via `LocalAppTheme` (never hardcoded). Token classes: `ColorTokens`, `TypographyTokens`, `ShapeTokens`, `DimensionTokens`.

**Navigation** — Single `NavHost` in `AppNavGraph`. Bottom bar shown only on Home/Library/Analytics/Settings routes. Screen arguments are primitive IDs only (e.g. `matchId: Long`).

**DI** — Single `appModule` in `di/AppModule.kt`. ViewModels with constructor parameters (matchId, overId) use Koin `parametersOf`. DataStore instance is created via `preferencesDataStore` delegate on `Context`.

**Data flow** — Room exposes `Flow<>` via DAOs → Repository maps entities to domain models → ViewModels collect as `StateFlow` → Composables use `collectAsState()`. ViewModels must not use Room entities directly; repositories convert them.

**Charts** — MPAndroidChart (View-based) wrapped in `AndroidView` composables. BarChart in InningsDetail, LineChart in Analytics.

**All UI strings** — Must use `stringResource(R.string.xxx)`. No hardcoded text in Composables.

### Screen Flow

```
PreloaderScreen → OnboardingScreen (first launch only) → HomeScreen
                                                         LibraryScreen      (bottom tabs)
                                                         AnalyticsScreen
                                                         SettingsScreen

HomeScreen / LibraryScreen → CreateMatchScreen → OverInputScreen → InningsDetailScreen → EditInningScreen
```

### Dependencies (libs.versions.toml)

Room 2.7.0 (KSP), Koin 3.5.6, Navigation Compose 2.7.7, DataStore 1.1.1, MPAndroidChart v3.1.0 (JitPack), kotlinx.serialization 1.7.3, KSP 2.2.10-2.0.2
