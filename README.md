# Video Playback with Ad stitching using ExoPlayer

This project demonstrates how to build a video player app using the MVVM (Model-View-ViewModel) architecture, Coroutines for background tasks, and ExoPlayer for video playback. The app supports playing videos, including advertisements (ads), and ensures seamless transitions between main videos and ads. App is compatible for all android devices between 24 - 34 android API level.


## This project uses:
* Internet Permission
* Gradle updates
* JDK : 11
* Kotlin language 
* Kotlin coroutines
* MVVM architecture
* XML layouts


## Reason behind using these technologies?

### MVVM (Model-View-ViewModel) Architecture
* Model: The `VideoRepository` class manages the list of videos (main videos and ads).
* View: Activities (`PlaylistActivity` and `PlayerActivity`) handle the UI (Views) and user interactions.
* ViewModel: The `VideoViewModel` handles the business logic (video playback, ad transitions) and exposes data to the view via LiveData.
By following the MVVM architecture, the app separates the UI from the business logic, making the code more modular, maintainable, and testable.

### Coroutines
* CoroutineScope (`viewModelScope`) is used for managing background tasks. Coroutines are launched to periodically check if it's time to play an ad, ensuring that the UI thread is not blocked.
* Coroutines are lifecycle-aware, so background tasks are automatically canceled when the ViewModel is destroyed, preventing memory leaks.

### ExoPlayer
* ExoPlayer is used for handling video playback, including streaming, buffering, and adaptive bitrate. It supports various video formats and provides a flexible API for integrating video playback with Android applications.
* PlayerView displays the video content and controls.

### LiveData
* LiveData is used to manage UI-related data in a lifecycle-conscious way. The `isAdPlaying` LiveData notifies the UI (e.g., whether an ad is playing) and updates the interface accordingly (e.g., hiding/showing controls).
* LiveData ensures that the UI reacts to changes in the data automatically.


## Main Code Structure

* `VideoRepository`: Provides the playlist of videos (including ads). Right now I have hardcoded list which can be replaced by network call to fetch list from the backend if needed.
* `VideoViewModel`: Manages the logic for video playback and ad stitching/transitions.
* `PlayerActivity`: The activity that handles video playback and stitching/transitions.
* `PlaylistActivity`: Displays the video list and allows the user to select a video.
* `PlaylistAdapter`: RecyclerView adapter used in PlaylistActivity to display the list of videos.


## How It Works

* Start: Upon opening `PlaylistActivity`, a list of videos is fetched from the repository and displayed. The user can click on any video to start playback in `PlayerActivity`.
* Video Playback: When a video starts, the `VideoViewModel` starts checking if it's time to play an ad. If 30 seconds pass, an ad will be shown.
* Ad Playback: When the ad is finished, the main video resumes from the position where it left off.
* UI Controls: During ad playback, the video controls are hidden. When the main video plays, the controls are re-enabled.

