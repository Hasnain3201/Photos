# Photos Application

## Overview

This project is a single-user photo application that allows storage and management of photos in one or more albums. The application is built using JavaFX for the user interface and includes features such as tagging, searching, and organizing photos into albums.

## Features

- **Photo Management**: Store, organize, and manage photos in multiple albums.
- **Tagging System**: Tag photos with custom attributes for easy search and organization.
- **Date Handling**: Use the last modification date of the photo file as the date the photo was taken.
- **Stock Photos**: Pre-loaded stock photos included in the application.
- **User Management**: Admin functionality to manage user accounts.
- **Search Functionality**: Search photos by date range and tag values.
- **JavaFX Interface**: User interface designed with JavaFX and FXML.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) installed on your system.
  

##Application Usage

Login: Users can log in with a username (password is optional).

Admin Functions: Admin can create, delete, and list users.

Photo Management: Users can create, delete, and rename albums. They can also add, remove, tag, and search photos within albums.

Search Photos: Users can search for photos by date range or by tag-value pairs (e.g., person=susan).

Slide Show: Users can view photos in a manual slideshow within an album.

Logout and Quit: Users can log out or quit the application, saving all changes made during the session.

## Example Usage

# Login as admin
Username: admin

# Create a new user
Admin: Create user -> Username: john

# Login as user
Username: john

# Create a new album
User: Create album -> Album name: Vacation

# Add a photo to the album
User: Add photo -> Select photo from file system -> Add tags (optional)

# Search for photos by tag
User: Search photos -> Tag: location=beach

# View a photo in the album
User: Open album -> Select photo -> View photo details and tags

# Logout
User: Logout

# Development Details
Packages: The project is organized into packages for the model, view, and controller.

Main Class: Photos with the main method to launch the application.

Data Storage: Uses Java serialization for storing and retrieving photo and album data.

JavaFX: The GUI is designed using JavaFX and FXML.

# Author
Hasnain Shahzad
