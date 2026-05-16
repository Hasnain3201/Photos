# Photos

<p align="center">
  <strong>A JavaFX desktop photo library with users, albums, captions, tags, search, and a built-in stock-photo demo account.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%20%2F%208-007396?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/UI-JavaFX-2f74c0?style=for-the-badge" alt="JavaFX">
  <img src="https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/Data-Serialized%20Users-6f42c1?style=for-the-badge" alt="Serialized users">
</p>

## Overview

Photos is a desktop photo-management app built with JavaFX and FXML. It supports an admin workflow, user libraries, album management, imported photos, captions, tag-value metadata, date/tag search, and a viewer for browsing selected photos. Data is stored locally with Java serialization, so the app can be demonstrated without a separate database.

The original project structure is preserved under `Photos03-main/Photos03`, with a root Maven file added for a clearer modern run path.

## Highlights

- Admin login for creating, listing, and deleting users.
- User login with a bundled `stock` account.
- Album creation, deletion, and renaming.
- Local photo imports through the JavaFX file chooser.
- Captions and custom tags on photos.
- Search by tag, multiple tag pairs, or date range.
- Compile search results into a new album.
- Photo viewer with image, date, caption, and tag details.

## Tech Stack

| Layer | Tools |
| --- | --- |
| Language | Java |
| UI | JavaFX, FXML |
| Build | Maven with OpenJFX dependencies |
| Storage | Java serialization (`.ser`) |
| Assets | Bundled stock images |

## Project Structure

```text
.
├── pom.xml
├── screenshots/
│   ├── login.png
│   ├── admin-dashboard.png
│   ├── photo-library.png
│   └── photo-viewer.png
├── Photos03-main/
│   ├── data/
│   └── Photos03/
│       ├── data/stock/
│       └── src/photos/
└── README.md
```

## Getting Started

### Prerequisites

- JDK 17+
- Maven 3.8+

### Run

```bash
git clone https://github.com/Hasnain3201/Photos.git
cd Photos
mvn javafx:run
```

### Build

```bash
mvn clean package
```

## Demo Flow

| Step | Action |
| --- | --- |
| Admin | Log in as `admin` and create a user. |
| Stock Library | Log in as `stock` to view bundled sample photos. |
| Albums | Create, rename, delete, and select albums from the main view. |
| Photos | Import images, add captions, and double-click to open the viewer. |
| Search | Search by date or tag pairs, then compile results into a new album. |

## Data Notes

- Serialized user data is stored in `Photos03-main/data/*.ser`.
- Stock images are stored in `Photos03-main/Photos03/data/stock/`.
- The app resolves those paths from the repo root, `Photos03-main`, or `Photos03-main/Photos03`.

## Troubleshooting

| Issue | Fix |
| --- | --- |
| JavaFX classes are missing | Run with Maven so OpenJFX dependencies are provided. |
| Maven is not on PATH | Install Maven or run through an IDE with JavaFX configured. |
| Stock photos are missing | Confirm files exist in `Photos03-main/Photos03/data/stock/`. |
| Serialized data is stale | Remove the affected `.ser` file and let the app recreate it. |

## Author

**Hasnain Shahzad**

- GitHub: [Hasnain3201](https://github.com/Hasnain3201)
- LinkedIn: [hasnain-shahzad-cs3201](https://www.linkedin.com/in/hasnain-shahzad-cs3201/)
