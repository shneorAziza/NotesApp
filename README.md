Location-Based Notes App

Project Description
A simple application for creating, viewing, and editing location-based notes. The app was designed and built using Jetpack Compose and the MVVM architectural pattern.

Key Features
Authentication: Login and registration using Firebase Authentication.
Main Screen: Displays notes in either a list view or a map view.
Note Screen: Supports creating, editing, viewing, and deleting notes.
Data Storage: Note data is stored in a local database using Room.

How to Run the App
Clone the project from the GitHub repository.
Open the project in Android Studio.
Sync Gradle.
Set up Firebase Authentication for your project.
Run the application on a virtual or physical device.

Architectural Approach
The app is built on the principles of MVVM (Model-View-ViewModel), which ensures a clear separation between the data and the UI.
UI: Built with Jetpack Compose.
ViewModel: Manages business logic and UI state.
Repository: An abstraction layer that separates the ViewModel from the data source (Room).

Known Limitations
It's not possible to update the location of an existing note.
It's not possible to attach an image to a note (bonus feature).
