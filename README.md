# Pelorus
 A custom Compose Multiplatform-based client for the [Compass](https://www.compass.education/) API! \
 API handling using [Kotlass](https://github.com/thennothinghappened/kotlass), HTML rendering using [HtmlText](https://github.com/thennothinghappened/HtmlText).
 Currently in beta!
 
## Features
Unticked are planned features. You can also suggest features in *Issues*!
 - [x] Schedule
   - [x] (Experimental) desktop-style view (time between classes & class length)
 - [x] Calendar
   - [x] Day based navigation
   - [ ] Date picker
 - [x] Class view
   - [x] Lesson plan
   - [ ] Resources
   - [ ] Click for class-filtered learning tasks
 - [x] Newsfeed
   - [x] Attachments
 - [x] Learning Tasks
   - [x] Basic sorting (WIP)
   - [ ] Search
   - [x] Status
   - [x] Attachments
   - [ ] Upload
   - [ ] Feedback
   - [ ] Comments
 - [ ] Event view
 - [ ] Alerts
   - [ ] Coursework Notifications
 - [ ] Profile page
   - [ ] Reports
   - [ ] Tasks
 
 
## Installation
 For Android, precompiled APKs are available in Releases. On Desktop for now, see *Compiling*.

## Login
### Web
 This is the default method for Android. Open the app and choose "Web (Recommended)" and log in as you would normally in Compass.

### Cookie (Fallback)
 Don't use this unless you have issues with Web login (if so, please report in *Issues*) or are on Desktop.
 1. Log into compass, open the F12 menu and find the Network tab.
 2. Reload the page and filter by XHR. Choose a request, and copy the `Cookie` *request header* value.
 3. For the domain, just copy the domain (and subdomain) from the site URL. (`*.compass.education`)
 4. For User Id, click on a class, and copy the *value* of `targetUserId` from the URL. If your Compass instance doesn't have this, it can also be obtained by grabbing it from the request body of `GetMyNewsFeedPaged`.
 
## Compiling
 Using either [Intellij Idea](https://www.jetbrains.com/idea/download) or [Android Studio](https://developer.android.com/studio/)
 with the Multiplatform plugin, clone and import the repo and let Gradle install dependencies. \
 Once the project sync is finished, run the `:desktop:compose desktop:run` gradle task to make sure it works. \
 Next, you can compile the APK through `:android:build:assemble`.

