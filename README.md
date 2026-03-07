# Kage

Personal streaming app for Fire Stick and Chromecast with Google TV.

Kage fetches a JSON catalog from a remote URL and displays a browsable grid of streams. It plays m3u8/HLS links via ExoPlayer and acestream:// links via the local Acestream Engine HTTP API.

## Setup

1. Open the project in Android Studio
2. Set your catalog URL in `app/src/main/java/com/kage/app/Config.kt`
3. Build and run on your Android TV device

## Catalog Format

Host a JSON file matching this structure:

```json
{
  "version": 1,
  "updated_at": "2025-01-01T00:00:00Z",
  "categories": [
    {
      "name": "Category Name",
      "items": [
        {
          "id": "1",
          "title": "Stream Title",
          "description": "Optional",
          "thumbnail": "https://example.com/thumb.jpg",
          "stream_type": "hls",
          "stream_url": "https://example.com/stream.m3u8"
        }
      ]
    }
  ]
}
```

Supported `stream_type` values: `hls`, `acestream`

## Acestream

Acestream channels are played via the **Ace Stream Media** app installed on the device. When you tap an acestream channel, Kage launches Ace Stream's built-in player via intent (`org.acestream.action.start_content`), which is free (ad-supported) and doesn't require a premium subscription.

### Ace Stream Setup (Fire Stick)

1. Download the APK from https://android.acestream.net/download/apk
2. Install via ADB: `adb install acestream-engine-x.x.x.apk`
3. Open Ace Stream Media once to initialize

**Tested version:** Ace Stream Media **3.2.14.5** (APK from https://android.acestream.net/download/apk)

## Tech Stack

- Kotlin + Jetpack Compose for TV
- AndroidX Media3 (ExoPlayer)
- Ktor Client + Kotlinx Serialization
- Min SDK 21 / Target SDK 34
