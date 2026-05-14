# playlist-maker-android-madaevAlikhan

## Описание проекта

Playlist Maker — мобильное Android-приложение, предназначенное для поиска музыкальных композиций и создания персональных плейлистов. Пользователь может находить треки через iTunes API, сохранять понравившиеся композиции в избранное и формировать собственные подборки с уникальными обложками.

### Возможности приложения

- Поиск музыкальных треков через iTunes API
- Просмотр подробной информации о композициях
- Добавление треков в список избранного
- Создание и редактирование пользовательских плейлистов
- Установка изображений для обложек плейлистов
- Сохранение истории поисковых запросов
- Локальное хранение данных между запусками приложения

## Версии SDK

- Минимальная версия Android: API 29 (Android 10)
- Целевая версия Android: API 36
- Версия SDK для компиляции: API 36

## Используемые технологии

- Jetpack Compose — построение пользовательского интерфейса
- Room — работа с локальной базой данных
- Retrofit — выполнение HTTP-запросов
- Coil — загрузка и отображение изображений
- DataStore — хранение настроек и пользовательских данных
- Kotlin Coroutines — выполнение асинхронных операций

## Структура проекта

```
├── main
│   ├── AndroidManifest.xml
│   ├── ic_launcher-playstore.png
│   ├── java
│   │   └── com
│   │       └── practicum
│   │           └── playlistmaker
│   │               ├── data
│   │               │   ├── db
│   │               │   │   ├── AppDatabase.kt
│   │               │   │   ├── PlaylistDAO.kt
│   │               │   │   ├── PlaylistEntity.kt
│   │               │   │   ├── TrackDAO.kt
│   │               │   │   └── TrackEntity.kt
│   │               │   ├── network
│   │               │   │   ├── BaseResponse.kt
│   │               │   │   ├── ItunesApiService.kt
│   │               │   │   ├── ItunesSearchResponseDto.kt
│   │               │   │   ├── ItunesTrackDto.kt
│   │               │   │   ├── RetrofitNetworkClient.kt
│   │               │   │   ├── TrackDto.kt
│   │               │   │   ├── TracksSearchRequest.kt
│   │               │   │   └── TracksSearchResponse.kt
│   │               │   ├── preferences
│   │               │   │   ├── SearchHistoryPreferences.kt
│   │               │   │   └── ThemePreferences.kt
│   │               │   └── repository
│   │               │       ├── PlaylistsRepositoryImpl.kt
│   │               │       ├── SearchHistoryRepositoryImpl.kt
│   │               │       └── TracksRepositoryImpl.kt
│   │               ├── domain
│   │               │   ├── creator
│   │               │   │   └── Creator.kt
│   │               │   ├── models
│   │               │   │   ├── Playlist.kt
│   │               │   │   └── Track.kt
│   │               │   ├── network
│   │               │   │   └── NetworkClient.kt
│   │               │   └── repository
│   │               │       ├── PlaylistsRepository.kt
│   │               │       ├── SearchHistoryRepository.kt
│   │               │       └── TracksRepository.kt
│   │               └── ui
│   │                   ├── activity
│   │                   │   └── MainActivity.kt
│   │                   ├── navigation
│   │                   │   ├── PlaylistHost.kt
│   │                   │   └── Screens.kt
│   │                   ├── screens
│   │                   │   ├── FavoritesScreen.kt
│   │                   │   ├── MainScreen.kt
│   │                   │   ├── NewPlaylistScreen.kt
│   │                   │   ├── playlists
│   │                   │   │   ├── components
│   │                   │   │   │   └── PlaylistListItem.kt
│   │                   │   │   └── PlaylistViewModel.kt
│   │                   │   ├── PlaylistScreen.kt
│   │                   │   ├── PlaylistsScreen.kt
│   │                   │   ├── search
│   │                   │   │   ├── components
│   │                   │   │   │   └── TrackListItem.kt
│   │                   │   │   ├── SearchState.kt
│   │                   │   │   └── SearchViewModel.kt
│   │                   │   ├── SearchScreen.kt
│   │                   │   ├── SettingsScreen.kt
│   │                   │   └── TrackDetailsScreen.kt
│   │                   └── theme
│   │                       ├── Color.kt
│   │                       ├── Theme.kt
│   │                       └── Type.kt
│   └── res
│       ├── drawable
│       │   ├── ic_launcher_background.xml
│       │   ├── ic_launcher_foreground.xml
│       │   ├── ic_music.xml
│       │   ├── no_connection.xml
│       │   └── no_tracks.xml
│       ├── mipmap-anydpi-v26
│       │   ├── ic_launcher_round.xml
│       │   └── ic_launcher.xml
│       ├── mipmap-hdpi
│       │   ├── ic_launcher_foreground.webp
│       │   ├── ic_launcher_round.webp
│       │   └── ic_launcher.webp
│       ├── mipmap-mdpi
│       │   ├── ic_launcher_foreground.webp
│       │   ├── ic_launcher_round.webp
│       │   └── ic_launcher.webp
│       ├── mipmap-xhdpi
│       │   ├── ic_launcher_foreground.webp
│       │   ├── ic_launcher_round.webp
│       │   └── ic_launcher.webp
│       ├── mipmap-xxhdpi
│       │   ├── ic_launcher_foreground.webp
│       │   ├── ic_launcher_round.webp
│       │   └── ic_launcher.webp
│       ├── mipmap-xxxhdpi
│       │   ├── ic_launcher_foreground.webp
│       │   ├── ic_launcher_round.webp
│       │   └── ic_launcher.webp
│       ├── values
│       │   ├── colors.xml
│       │   ├── strings.xml
│       │   └── themes.xml
│       └── xml
│           ├── backup_rules.xml
│           └── data_extraction_rules.xml
└── test
    └── java
        └── com
            └── practicum
                └── playlistmaker
                    └── ExampleUnitTest.kt
```

## Сборка и запуск проекта

### 1. Клонирование репозитория

git clone https://github.com/alikhan902/playlist-maker-android-madaevAlikhan
cd playlist-maker-android

### 2. Открытие проекта

Запустите Android Studio и выберите пункт Open, затем укажите директорию проекта.

### 3. Синхронизация зависимостей

Если синхронизация Gradle не выполнилась автоматически, нажмите кнопку Sync Now.

### 4. Подготовка устройства

Создайте виртуальное устройство через AVD Manager или подключите физическое Android-устройство с включённой USB-отладкой.

### 5. Запуск приложения

Нажмите кнопку Run в Android Studio и дождитесь завершения сборки.