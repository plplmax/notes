# Notes

Notes is an offline-first Android app for cloud notes

## Requirements
- [Firebase account with the created project](https://firebase.google.com/docs/android/setup)
- `google-services.json` from the previous step

## Tech stack
- Kotlin + Coroutines
- Hilt
- JUnit 4
- Mockito
- MVVM + LiveData + Clean Architecture
- Firebase Auth
- Firebase Realtime Database

## Installation

```batch
git clone git@github.com:plplmax/notes.git
```

```batch
cd notes
```

```batch
cp -f /src/to/your/google-services.json /app/google-services.json
```

Just to build an APK:

```batch
./gradlew assembleDebug
```

or to build and immediately install it on a running emulator or connected device:

```batch
./gradlew installDebug
```

## Demo
  
<details>
  
| App icon      | Sign up       | Sign in       |
| ------------- | ------------- | ------------- |
| <img src="https://user-images.githubusercontent.com/50287455/179399912-f7dccb7c-6d07-4565-ba09-d71f632953a7.jpg" width="320">  | <img src="https://user-images.githubusercontent.com/50287455/179400424-86f7664f-737d-48ee-825f-f7f6422e80e0.jpg" width="320">  | <img src="https://user-images.githubusercontent.com/50287455/179400618-f1e5b91d-b4a7-4c57-b762-1f9c3acd5eaa.jpg" width="320">  |

| Note list     | Note context menu | Toolbar menu  |
| ------------- | ----------------- | ------------- |
| <img src="https://user-images.githubusercontent.com/50287455/179400729-7f01b3fa-05e2-470f-9371-ae81c7f52450.jpg" width="320">  | <img src="https://user-images.githubusercontent.com/50287455/179400763-64a5db6e-1def-4dfe-9b1a-9fe0759e713c.jpg" width="320">  | <img src="https://user-images.githubusercontent.com/50287455/179400841-f524bc59-2d36-4875-9764-29b44cef8b57.jpg" width="320">  |

| New note      | Edit Note         | Navigation drawer |
| ------------- | ----------------- | ----------------- |
| <img src="https://user-images.githubusercontent.com/50287455/179401002-acc6d25e-50d9-466b-beb5-2f46566b25a2.jpg" width="320">  | <img src="https://user-images.githubusercontent.com/50287455/179401005-8852bf0f-2582-43e7-8dce-731307e5656b.jpg" width="320">  | <img src="https://user-images.githubusercontent.com/50287455/179401007-eb80e92c-4493-4731-9421-265b981cc053.jpg" width="320">  |
  
</details>

## Contributing
If you want to make small changes, please create a pull request. Major changes will be rejected.

## License
[MIT](https://choosealicense.com/licenses/mit/)
