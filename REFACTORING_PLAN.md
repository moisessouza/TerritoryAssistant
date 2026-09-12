# TerritoryAssistant Refactoring & Modernization Plan (Detailed Technical Guide)

This document provides a comprehensive, step-by-step technical specification to modernize **TerritoryAssistant** from its legacy Java/SQLite architecture to modern Android Development (MAD) standards (Kotlin, Room, MVVM, Jetpack Compose, Material 3).

---

## Step 1: Kotlin Integration & Setup [COMPLETED]
- Enable Kotlin Android plugin (`id 'org.jetbrains.kotlin.android'`).
- Configure `compileOptions` and `kotlinOptions` for Java 17.
- Add `androidx.core:core-ktx:1.12.0`.
- Verify build success.

---

## Step 2: Data Layer Migration (SQLiteOpenHelper -> Room) [COMPLETED]
- Add Room 2.6.1 and KSP dependencies.
- Define `@Entity` classes (`DirigenteEntity`, `GrupoEntity`, `TerritorioEntity`, `TerritorioVizinhoEntity`, `DesignacaoEntity`, `UltimaAcoesEntity`, `ConfiguracoesEntity`).
- Define `@Dao` interfaces (`DirigenteDao`, `GrupoDao`, `TerritorioDao`, `TerritorioVizinhoDao`, `DesignacaoDao`, `UltimaAcoesDao`, `ConfiguracoesDao`).
- Create `AppDatabase` singleton.
- Bridge legacy `DBHelper` classes to Room DAOs and verify 100% test coverage.

---

## Step 3.1: Repositories & Coroutines Infrastructure Setup
- **Objective**: Create the Repository layer to abstract Room DAOs and expose clean Kotlin Coroutines / Flow APIs.
- **Tasks**:
  1. Add Lifecycle ViewModel & Runtime KTX dependencies (`androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0`).
  2. Implement `LeaderRepository.kt`, `GroupRepository.kt`, `TerritoryRepository.kt`, `DesignationRepository.kt`, `SettingsRepository.kt`, `HistoryRepository.kt`.
  3. Verify build and run unit tests.

---

## Step 3.2: MVVM Migration - Leaders & Groups Modules
- **Objective**: Refactor Leaders and Groups management to MVVM.
- **Tasks**:
  1. Implement `LeaderViewModel.kt` and `GroupViewModel.kt` using `viewModelScope` and `StateFlow`.
  2. Connect `DirigentesActivity`, `NovoDirigenteActivity`, `EditarDirigenteActivity`.
  3. Connect `GruposActivity`, `NovoGrupoActivity`, `EditarGrupoActivity`.
  4. Verify build and run tests.

---

## Step 3.3: MVVM Migration - Territory Management Module
- **Objective**: Refactor Territory management to MVVM.
- **Tasks**:
  1. Implement `TerritoryViewModel.kt`.
  2. Connect `TerritoriosActivity`, `NovoTerritorioActivity`, `EditarTerritorioActivity`, `FotoTerritorioActivity`.
  3. Verify build and run tests.

---

## Step 3.4: MVVM Migration - Designations, History & Suggestions Modules
- **Objective**: Refactor Designations, History, Settings and Suggestion algorithms to MVVM.
- **Tasks**:
  1. Implement `DesignationViewModel.kt`, `HistoryViewModel.kt`, `SettingsViewModel.kt`, `SuggestionViewModel.kt`.
  2. Connect `DesignarActivity`, `VerDesignadosActivity`, `FecharDesignacaoActivity`, `HistoricoActivity`, `SugerirActivity`, `ConfiguracoesActivity`.
  3. Verify build and run tests.

---

## Step 4: UI Migration (Jetpack Compose & Material 3)
- Enable Compose build feature and add Material 3 BOM dependencies.
- Incrementally replace XML layouts and custom ArrayAdapters with Composable UI screens.

---

## Step 5: Testing & Quality Assurance
- Add unit tests for ViewModels and Repositories using `kotlinx-coroutines-test`.
- Add UI tests using Compose Testing library.
