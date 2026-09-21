
# 🚗 Car Document Expiry Tracker

A native Android app that helps you keep track of insurance, technical inspection, and road toll expiration dates across multiple vehicles — with automated, tiered push notifications so you never miss a deadline.

Built as a personal project to tackle a real world problem.

## Features

- 🚙 **Multiple cars** — track documents separately for each vehicle
- 📄 **Document tracking** — insurance, technical inspection, road toll, vignette, and custom "other" document types
- 🔔 **Smart notifications** — get notified at customizable intervals before expiry (e.g. 30, 7, and 1 days before)
- 📅 **Native date picker** — pick expiry dates from a calendar, no manual typing
- ✏️ **Full CRUD** — add, edit, and delete both cars and documents, with delete confirmation dialogs
- 🎨 **Color-coded status** — documents are visually flagged as expired, expiring soon, or up to date
- 🌍 **Localization** — available in English and Romanian, with automatic switching based on device language
- 🌗 **Material 3 design** — clean, modern UI built entirely with Jetpack Compose


## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM |
| Local database | Room (SQLite) |
| Async | Kotlin Coroutines, Flow / StateFlow |
| Background work | WorkManager |
| Navigation | Jetpack Navigation Compose |

## Architecture

```
UI Layer (Jetpack Compose)
        ↓
ViewModel (state + business logic)
        ↓
Repository (single source of truth)
        ↓
Room Database (Cars & Documents, with foreign keys)
```

- **Cars** and **Documents** are stored in separate tables with a one-to-many relationship (one car → many documents), using cascading deletes.
- Each `Document` stores a descending list of notification offsets (e.g. `[30, 7, 1]`); a daily `WorkManager` job checks all documents, fires a notification when the next threshold is crossed, and removes it from the list.
- UI state is exposed via `StateFlow`, so any database change is automatically reflected on screen — no manual refresh logic needed.

## Getting Started

### Prerequisites
- Minimum SDK: API 26 (Android 8.0)

### Setup
Go to "Release" and download the latest version
## Roadmap / Ideas for Future Improvements

- [ ] Dashboard view showing all upcoming expirations across every car
- [ ] Search and filter documents
- [ ] Export/backup data
- [ ] Attach photos of documents
- [ ] Widget for home screen

