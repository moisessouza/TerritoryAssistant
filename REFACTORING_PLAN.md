# TerritoryAssistant Refactoring & Modernization Plan (Detailed Technical Guide)

This document provides a comprehensive, step-by-step technical specification to modernize **TerritoryAssistant** from its legacy Java/SQLite architecture to modern Android Development (MAD) standards (Kotlin, Room, MVVM, Jetpack Compose, Material 3).

---

## Step 1: Kotlin Integration & Setup

### 1.1 Update `app/build.gradle`
Enable the Kotlin Android plugin and add Kotlin core dependencies:
```groovy
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.application.territoryassistant'
    compileSdk 34

    defaultConfig {
        applicationId "com.application.territoryassistant"
        minSdk 21
        targetSdk 34
        versionCode 21
        versionName "1.3.6"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = '17'
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

### 1.2 Verify Toolchain
Ensure the root `build.gradle` includes the Kotlin Gradle plugin classpath if not using plugins block:
```groovy
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.13.2'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23"
    }
}
```

---

## Step 2: Data Layer Migration (`SQLiteOpenHelper` -> Room)

### 2.1 Add Room Dependencies
In `app/build.gradle`:
```groovy
def room_version = "2.6.1"
implementation "androidx.room:room-runtime:$room_version"
implementation "androidx.room:room-ktx:$room_version"
kapt "androidx.room:room-compiler:$room_version" // or KSP
```

### 2.2 Create Entities & DAOs
Convert existing value objects (`TerritorioVO`) to Room Entities:
```kotlin
@Entity(tableName = "territorios")
data class TerritoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cod: String,
    val nome: String,
    val observacao: String?
)
```

Create DAO interfaces:
```kotlin
@Dao
interface TerritoryDao {
    @Query("SELECT * FROM territorios")
    fun getAllTerritories(): Flow<List<TerritoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerritory(territory: TerritoryEntity)

    @Query("DELETE FROM territorios WHERE id = :id")
    suspend fun deleteTerritory(id: Int)
}
```

### 2.3 Create Room Database
```kotlin
@Database(entities = [TerritoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun territoryDao(): TerritoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "territory_assistant.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

---

## Step 3: Architecture Modernization (MVVM & Coroutines)

### 3.1 ViewModel & Lifecycle Dependencies
```groovy
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
```

### 3.2 Repository Pattern
```kotlin
class TerritoryRepository(private val territoryDao: TerritoryDao) {
    val allTerritories: Flow<List<TerritoryEntity>> = territoryDao.getAllTerritories()

    suspend fun insert(territory: TerritoryEntity) {
        territoryDao.insertTerritory(territory)
    }

    suspend fun delete(id: Int) {
        territoryDao.deleteTerritory(id)
    }
}
```

### 3.3 ViewModel Implementation
```kotlin
class TerritoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TerritoryRepository
    val allTerritories: StateFlow<List<TerritoryEntity>>

    init {
        val territoryDao = AppDatabase.getDatabase(application).territoryDao()
        repository = TerritoryRepository(territoryDao)
        allTerritories = repository.allTerritories
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun insert(territory: TerritoryEntity) = viewModelScope.launch {
        repository.insert(territory)
    }

    fun delete(id: Int) = viewModelScope.launch {
        repository.delete(id)
    }
}
```

---

## Step 4: UI Migration (Jetpack Compose & Material 3)

### 4.1 Enable Compose in `app/build.gradle`
```groovy
android {
    buildFeatures {
        compose true
    }
    composeOptions {
        kotlinCompilerExtensionVersion '1.5.8'
    }
}

dependencies {
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.activity:activity-compose:1.8.2'
}
```

### 4.2 Create Composable Screens
Replace legacy XML list items and adapters with LazyColumn:
```kotlin
@Composable
fun TerritoryScreen(viewModel: TerritoryViewModel = viewModel()) {
    val territories by viewModel.allTerritories.collectAsState()

    LazyColumn {
        items(territories) { territory ->
            Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = territory.nome, style = MaterialTheme.typography.titleMedium)
                    Text(text = territory.cod, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
```

---

## Step 5: Testing & Verification
- **Unit Tests**: Test ViewModels and Repositories using `kotlinx-coroutines-test` and `JUnit 4/5`.
- **UI Tests**: Test Compose layouts with `androidx.compose.ui.test.junit4`.
