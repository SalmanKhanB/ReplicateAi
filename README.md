# ReplicateAI - AI Voice Generator

An Android application that leverages AI models to generate realistic speech from text input.

## Features

- **Multiple AI Voice Models**: Choose between different AI models including Kokoro 82M and Speech-02 Turbo
- **Voice Selection**: Various voice options with nationality indicators
- **Emotion Control**: Add emotional expression to your generated speech (for supported models)
- **Language Boost**: Enhance pronunciation for specific languages
- **Speed Control**: Adjust the playback speed of generated audio
- **History**: View and replay all your previous voice generation requests
- **Offline Access**: Access previously generated audio without internet connection

## Screenshots

<p align="center">
  <img src="https://github.com/SalmanKhanB/ReplicateAi/history.png" width="30%" alt="History Screen">
  <img src="https://github.com/SalmanKhanB/ReplicateAi/generation_speech02.png" width="30%" alt="Speech-02 Generation">
  <img src="https://github.com/SalmanKhanB/ReplicateAi/generation_kokoro.png" width="30%" alt="Kokoro Generation">
</p>

## Setup

### Prerequisites
- Android Studio Arctic Fox or newer
- Minimum SDK 23 (Android 6.0)
- Java 11 or Kotlin 1.5+

### API Key
To use the application, you'll need to obtain an API key from the voice service provider.

1. Create a `local.properties` file in the root directory of your project if it doesn't already exist
2. Add your API key to the file:
```
api.key=yourkey
```

### Building the App
1. Clone the repository
```bash
git clone https://github.com/yourusername/replicateai.git
```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Build and run the application on an emulator or physical device

## Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern and uses:

- Room Database for local storage of voice requests
- Retrofit for API communication
- LiveData & ViewModel for UI state management
- Coroutines for asynchronous operations

## Database Structure

The app uses Room database to store voice generation requests with the following schema:

```kotlin
@Entity(tableName = "voice_requests")
data class VoiceRequest(
    @PrimaryKey val id: String,
    val modelId: String,
    val modelName: String,
    val text: String,
    val voiceId: String,
    val voiceName: String,
    val emotion: String? = null,
    val languageBoost: String? = null,
    val outputUrl: String? = null,
    val status: String,
    val createdAt: Date,
    val error: String? = null
)
```

## How to Use

1. **Enter Text**: Type or paste the text you want to convert to speech
2. **Select Model**: Choose between Kokoro 82M or Speech-02 Turbo
3. **Choose Voice**: Select from available voices
4. **Customize**: Set emotion, language boost or speed as needed
5. **Generate**: Tap the "Generate Voice" button
6. **History**: Access your generation history from the History tab
7. **Playback**: Listen to generated speech by tapping the play button

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- This app utilizes advanced AI models for speech synthesis
- Thanks to the open-source community for various libraries used in this project

---

**Note**: This is a personal project and not affiliated with any commercial text-to-speech service.

## Contact

Salman Khan - [@Fiverr](https://www.fiverr.com/users/salmankhan150/portfolio/) - jan9222522@gmail.com

Project Link: [https://github.com/yourusername/replicateai](https://github.com/yourusername/replicateai)
