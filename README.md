# Photos

<p align="center">
  <strong>A JavaFX desktop application for organizing photos into albums with tags, captions, search, and user management.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Language-Java-007396?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/UI-JavaFX-2f74c0?style=for-the-badge" alt="JavaFX">
  <img src="https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/Storage-Serialized%20Data-6f42c1?style=for-the-badge" alt="Serialized storage">
</p>

## Preview

Screenshots have not been added yet because this workspace does not have a JavaFX runtime available to launch the desktop UI. After running the app locally, add screenshots to `screenshots/` such as `screenshots/login.png`, `screenshots/admin.png`, and `screenshots/albums.png`.

## Overview

Photos is a desktop photo management app built with JavaFX. It supports multiple users, an admin dashboard, stock photos, albums, captions, tags, tag/date search, and a photo viewer. Data is stored locally using Java serialization, making it easy to demo without a separate database.

The project source lives in the original course-project folder structure under `Photos03-main/Photos03`, with a root-level Maven build file added for simpler setup.

## Key Features

- Login flow for regular users plus an admin user.
- Admin dashboard for creating, listing, and deleting users.
- Album creation, deletion, and renaming.
- Photo import from the local file system.
- Captions and custom tag-value pairs for photos.
- Search by tag, multiple tag pairs, or date range.
- Compile search results into a new album.
- Photo viewer with image preview, date, and tags.
- Bundled stock-photo user and sample images.

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Java 17 |
| UI | JavaFX, FXML |
| Build | Maven with OpenJFX dependencies |
| Storage | Java serialization (`.ser` files) |
| Assets | Bundled stock photos |

## Project Structure

```text
.
├── pom.xml
├── Photos03-main/
│   ├── data/
│   │   ├── admin.ser
│   │   └── stock.ser
│   └── Photos03/
│       ├── data/stock/
│       └── src/photos/
│           ├── app/
│           ├── model/
│           └── view/
└── README.md
```

## Getting Started

### Prerequisites

- JDK 17 or newer
- Maven 3.8+

### Installation

```bash
git clone https://github.com/Hasnain3201/Photos.git
cd Photos
```

### Run

```bash
mvn javafx:run
```

The Maven build downloads JavaFX controls and FXML dependencies automatically.

### Build

```bash
mvn clean package
```

## Usage

| Action | How to Demo |
| --- | --- |
| Admin login | Enter `admin` on the login screen. |
| Create user | Log in as `admin`, create a username, then log out. |
| User login | Enter a created username, or use `stock` to view bundled stock photos. |
| Create album | Use the Album menu and choose Add Album. |
| Add photos | Select an album, then use Add Photo to Album. |
| Tag/search | Add tags to photos, then search by tag, tag pairs, or date range. |
| View photo | Double-click a photo thumbnail to open the viewer. |

## Data Notes

- User data is stored in `Photos03-main/data/*.ser`.
- Stock images are stored in `Photos03-main/Photos03/data/stock/`.
- The app now resolves those paths from the repo root, `Photos03-main`, or `Photos03-main/Photos03`.

## Troubleshooting

| Issue | Fix |
| --- | --- |
| JavaFX classes are missing | Run with Maven using `mvn javafx:run` so JavaFX dependencies are provided. |
| Maven is not installed | Install Maven, then rerun `mvn javafx:run`. |
| Stock photos do not appear | Confirm the files still exist in `Photos03-main/Photos03/data/stock/`. |
| Serialized user data becomes stale | Delete or regenerate the affected `.ser` file in `Photos03-main/data/`. |

## Future Improvements

- Add screenshots after launching the JavaFX UI locally.
- Add unit tests around album, photo, and tag model behavior.
- Improve visual styling of the FXML screens while keeping the current workflows intact.

## Author

**Hasnain Shahzad**

- GitHub: [Hasnain3201](https://github.com/Hasnain3201)
- LinkedIn: [hasnain-shahzad-cs3201](https://www.linkedin.com/in/hasnain-shahzad-cs3201/)
