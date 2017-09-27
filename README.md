# Memonic

[<img src="https://github.com/DongJunJin/Memonic/raw/master/screenshots/Main.jpeg" width="250">](https://github.com/DongJunJin/Memonic/raw/master/screenshots/Main.jpeg)

Memonic is a [hackathon project](https://devpost.com/software/memonic) for [MHacks X](https://mhacks.org/) that uses [Microsoft's Emotion API](https://azure.microsoft.com/en-us/services/cognitive-services/emotion/) to perform emotional analysis on an image and produces notes from the C Major [Pentatonic scale](https://en.wikipedia.org/wiki/Pentatonic_scale) based on the results. The current version uses a HashMap and some JSON processing to play an mp3 file corresponding to the api response emotion with the highest confidence value. The goal is to eventually generate the music from an image, or possibly in realtime with video.

### Prerequisites

[Android Studio](https://developer.android.com/studio/index.html)

An Android phone with API v21 or higher

### Installing

Download the project from [Github](https://github.com/DongJunJin/Memonic)
Open with Android Studio
Plug in your device and click Run

### Usage

Click the :notes: at the top to play the available sample notes and test sound levels.

Click the :heavy_plus_sign: to take a photo to send.

Once a photo has been loaded, hit the :arrow_forward: to send it to the API.

If the API detects faces in the photo, you should see something like this:

[<img src="https://github.com/DongJunJin/Memonic/raw/master/screenshots/ResultWithFaces.jpeg" width="250">](https://github.com/DongJunJin/Memonic/raw/master/screenshots/ResultWithFaces.jpeg)

Otherwise (if it doesn't crash):

[<img src="https://github.com/DongJunJin/Memonic/raw/master/screenshots/ResultWithoutFaces.jpeg" width="250">](https://github.com/DongJunJin/Memonic/raw/master/screenshots/ResultWithoutFaces.jpeg)



## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Android](https://maven.apache.org/) - Dependency Management

## Contributing

Submit a pull request or comment, and we'll take a look.

## Authors

* **Andrew Jin** - [DongJunJin](https://github.com/DongJunJin)
* **Iman Nandi** - [inandi2015](https://github.com/inandi2015)
* **Tarun Khanna** - [tkhanna42](https://github.com/tkhanna42)

See also the list of [contributors](https://github.com/DongJunJin/Memonic/contributors) who participated in this project.

## License

This project is licensed under the MIT License

