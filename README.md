# TexiOne — AQI Location Booking App

Android take-home assignment. Pick two map locations, see live Air Quality Index, book the A→B pair, view history.

---

## App workflow

| Step | Screen | What happens |
|------|--------|--------------|
| 1 | **Map** | App opens to a full-screen map. Grant location permission to move the camera to your position. A live AQI badge updates as you pan (debounced 400 ms). Tap **Set A** to geocode the pin and save it as slot A. |
| 2 | **Map → Set B** | After A is filled the button reads **Set B**. Pan to a second location and tap to fill slot B. |
| 3 | **Detail** _(optional)_ | Tap a filled label (A or B) to open the Detail screen. Add an optional nickname (max 20 chars) — this replaces the address everywhere in the UI. |
| 4 | **Booking** | With both slots filled the button reads **Book**. Tap it to POST the pair. The response shows the computed price (haversine distance × 2.5). Slots are cleared after a successful booking. |
| 5 | **History** | Reach via the bottom navigation icon. Lists all bookings for the current month with a total count and total price in the header. Tap any record to re-load both locations and jump back to the map ready to re-book. |
| 6 | **Cached locations** | Tap an empty slot label on the Map screen to see previously geocoded locations. Selecting one fills the slot and animates the camera to that location. |

---

## Setup

### API keys (required before first run)

1. Copy the template to `local.properties`:

   ```bash
   cp local.default.properties local.properties
   ```

2. Fill in your real keys inside `local.properties`:

```properties
# Google Maps API key
# Create at: https://console.cloud.google.com → APIs & Services → Credentials
# Enable: Maps SDK for Android
MAPS_API_KEY=YOUR_KEY_HERE

# AQICN real-time AQI token (free)
# Get free token at: https://aqicn.org/data-platform/token/
AQICN_TOKEN=YOUR_TOKEN_HERE
```


`MAPS_API_KEY` is injected into the Android manifest via `manifestPlaceholders`.  
`AQICN_TOKEN` lands in `BuildConfig.AQICN_TOKEN` at compile time — never in source.

### Build

```bash
./gradlew assembleDebug          # compile
./gradlew testDebugUnitTest      # unit tests
./gradlew lint                   # lint
```

---

## Architecture

### Presentation (feature/)

Single Activity + 100% Jetpack Compose UI. Each screen has its own `ViewModel` annotated with `@HiltViewModel`. ViewModels expose `StateFlow`; composables collect with `collectAsStateWithLifecycle`. **Business rules live in ViewModels and domain, never in composables.**

### Domain (domain/)

Pure Kotlin — no Android or library imports. Contains:
- **Domain models**: `SelectedLocation`, `Selection`, `Booking`, `Slot` enum, `AqiState`
- **`SelectionStore`**: `@Singleton` `MutableStateFlow<Selection>` — the single source of truth for both slots. All derived state (button label, display name, next empty slot) is computed here.
- **Repository interfaces**: `AqiRepository`, `LocationRepository`, `BookingRepository` — the data layer is wired to these, never to impls
- **`RebookUseCase`**: uses `coroutineScope { async + async }` to concurrently refresh AQI for both slots; atomic — if either fetch fails, neither slot updates

### Data (data/)

Three `@Named` Retrofit instances sharing one base `OkHttpClient`:

| Name | Base URL | Purpose |
|---|---|---|
| `AQICN` | `api.waqi.info` | Live AQI fetch |
| `BigDataCloud` | `api.bigdatacloud.net` | Reverse geocoding (no key) |
| `Books` | mock / real backend | Create and list bookings |

**Address display rule**: from `localityInfo.administrative`, sort by `order`, take the last two entries, join names with `", "`. Falls back gracefully if fewer than two entries exist.

Room database (`HopInDatabase`) stores geocoded locations. Cache key = `(lat * 1000).toLong()` and `(lng * 1000).toLong()` — integer truncation (not rounding, not `String.format`) so that 37.5642 and 37.5645 share the same key. AQI is always fetched live; only geocoded addresses are cached.

### Mock server design

`FakeBooksServer` is a `@Singleton` that serves all `/books` requests from an in-memory `LinkedHashMap(capacity=50, accessOrder=true)`:

- **LRU eviction** (`accessOrder=true`): the map evicts the least-recently *accessed* entry once capacity exceeds 50. Access-ordered means reads count as use — a booking that is repeatedly listed stays alive longer than one that was only created.
- **Price computation**: haversine distance (km) × 2.5 — purely dynamic, no stored price table
- **Thread-safety**: `Mutex` guards all reads and writes
- **Month filtering**: the GET handler filters by `year`/`month` query params from the same store

`MockInterceptor` is added to the Books `OkHttpClient` only when `BuildConfig.USE_MOCK_BOOKS == true`. Swapping to a real backend is **one flag**:

```properties
# app/build.gradle.kts defaultConfig
buildConfigField("boolean", "USE_MOCK_BOOKS", "false")   # ← change this
```

`BookingRepositoryImpl`, `BookingViewModel`, `HistoryViewModel` — none of them know the server is mocked. The mock lives entirely at the OkHttp interceptor layer.

### Coordinate truncation rationale

`(value * 1000).toLong()` truncates toward zero regardless of sign or locale. `String.format("%.3f", v)` rounds (37.5649 → "37.565") and is locale-sensitive (decimal separator varies). The truncation key gives a ~111 m² tile that acts as a geocoding cache cell.

### AQI stream (Map screen)

```
snapshotFlow { cameraPosition to isMoving }
  .filter { !isMoving }
  .distinctUntilChanged()
  .debounce(400ms)
  .mapLatest { aqiRepository.getAqi(it) }   // cancels in-flight if camera moves
  .stateIn(WhileSubscribed(5000))
```

`mapLatest` cancels any running fetch when the camera moves again. `WhileSubscribed(5000)` keeps the upstream hot for 5s after the last subscriber leaves (config change resilience).

---

## What I'd add with more time

- **Offline resilience**: cache AQI values per tile with a short TTL; show stale indicator
- **Booking reset**: "Clear" button to start a new A→B pair without restarting the app
- **Real server swap test**: a small integration test that confirms the mock and real server produce structurally identical responses for the same request, so the flag swap is proven safe
- **Map marker cluster**: if history has many bookings, show them on the map with tap-to-rebook
- **Accessibility**: content descriptions on the center pin, AQI chip, and slot labels; large-text layout testing
- **Pagination for history**: the current implementation fetches all bookings for the month into memory; a `PagingSource` would scale better
