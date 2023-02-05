# Pelorus
 A custom Compose Multiplatform-based client for the [Compass](https://www.compass.education/) API! \
 API handling using [Kotlass](https://github.com/thennothinghappened/kotlass). Currently in alpha
 and not very feature-rich, but functional, and much faster!
 
## Compiling
 Using either [Intellij Idea](https://www.jetbrains.com/idea/download) or [Android Studio](https://developer.android.com/studio/)
 with the Multiplatform plugin, clone and import the repo and let Gradle install dependencies. \
 Once the project sync is finished, run the `:desktop:compose desktop:run` gradle task to make sure it works. \
 Next, you can compile the APK through `:android:build:assemble`.

## Login
 Logging in will later use a better method, but currently requires grabbing the data manually
 from your browser.
 1. Log into compass, open the F12 menu and find the Network tab.
 2. Reload the page and filter by XHR. Choose a request, and copy the `Cookie` *request header* value.
 3. For the domain, just copy the domain (and subdomain) from the site URL. (`*.compass.education`)
 4. For User Id, click on a class, and copy the *value* of `targetUserId` from the URL. If your Compass instance doesn't have this, it can also be obtained by grabbing it from the request body of `GetMyNewsFeedPaged`.
