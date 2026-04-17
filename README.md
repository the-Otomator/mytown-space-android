# Gesher TWA Template

Turn your web app into an Android app with native push notifications via Gesher.

## Setup (5 steps)

### 1. Clone this repo

```bash
git clone https://github.com/the-Otomator/gesher-twa-template
cd gesher-twa-template
```

### 2. Configure your app

Edit `app/src/main/res/values/strings.xml`:

- `launch_url` → your web app URL
- `gesher_api_url` → https://api.gesher.pro (or your instance)
- `gesher_api_key` → your Bearer token from the Gesher dashboard
- `asset_statements` → update domain to match `launch_url`

Also set `applicationId` in `app/build.gradle.kts` to your package name, and use the same package when registering the Android app in Firebase.

### 3. Add Firebase

1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app (package name must match `applicationId` in `app/build.gradle.kts`)
3. Download `google-services.json` → place in `app/` (see `app/google-services.json.example` as a non-secret placeholder shape only)
4. In Gesher dashboard → your app → Android/FCM → paste Service Account JSON

### 4. Add Digital Asset Links

On your web server, host at `https://your-app.com/.well-known/assetlinks.json`:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "pro.gesher.twa",
    "sha256_cert_fingerprints": ["YOUR_SIGNING_KEY_SHA256"]
  }
}]
```

Use your real package name and release signing certificate SHA-256.

### 5. Build and run

Open the project in Android Studio, sync Gradle, then Run.

## Notes

- `MainActivity` extends the Android Browser Helper `LauncherActivity` for a proper Trusted Web Activity flow.
- FCM registration with Gesher runs on token refresh and on first successful `FirebaseMessaging.getInstance().token` in `MainActivity`.
