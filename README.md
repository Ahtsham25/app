# Imran Series Library

Ibn-e-Safi ke Imran Series novels (archive.org collection) padhne aur download karne ki Android app.

## Features
- 117 novels ki catalog list (naam + number)
- Click karne par: **Online Read** (archive.org ka apna embedded reader, app ke andar WebView mein khulta hai) ya **Download** (EPUB file phone ke `Downloads/ImranSeries/` folder mein save hoti hai)
- Book list `books.json` se aati hai — GitHub par edit karke bina naya APK banaye list update ho sakti hai

## APK banane ka tareeqa (bina desktop/Android Studio ke)

1. Ek naya **GitHub repository** banayein (public ya private, dono chalega)
2. Is poore folder (`ImranSeriesLibrary`) ka sara content us repo mein upload kar dein (GitHub website se "Add file → Upload files" se bhi ho sakta hai, phone/Chromebook se)
3. `app/src/main/java/com/shami/imranseries/BooksRepository.kt` file mein `REMOTE_BOOKS_URL` line ko apne repo ke `books.json` ke raw link se replace karein:
   ```
   https://raw.githubusercontent.com/USERNAME/REPO/main/books.json
   ```
4. Repo ke **Actions** tab mein jayein — "Build APK" workflow khud chal jayega (ya "Run workflow" button dabayein)
5. Build complete hone ke baad, workflow run ke neeche **Artifacts** section se `ImranSeriesLibrary-debug-apk` download karein — ye ek ZIP hai jismein APK hai
6. APK phone mein install kar lein (Unknown sources allow karna par sakta hai)

## Naye novels ya list update karne ke liye
`books.json` file GitHub par seedha edit kar dein (koi naya APK build karne ki zaroorat nahi — app har baar khulnay par ye file fetch karti hai).

Har entry ka format:
```json
{
  "id": "121",
  "title": "Novel Ka Naam",
  "number": "121",
  "embed_url": "https://archive.org/embed/COLLECTION/IDENTIFIER",
  "download_url": "https://archive.org/download/COLLECTION/IDENTIFIER.epub",
  "details_url": "https://archive.org/details/COLLECTION/IDENTIFIER"
}
```
