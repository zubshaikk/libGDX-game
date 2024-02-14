# Drop Game
This is a game based on [![A Simple Game](https://libgdx.com/wiki/start/a-simple-game)] with some changes. Drop is a simple yet engaging game built with the LibGDX framework. In this game, the player controls a bucket to catch falling raindrops. As the game progresses, raindrops fall with increasing speed, challenging the player to catch as many as possible. Miss too many, and it's game over!

## Features
Simple Controls: Move the bucket left and right to catch falling raindrops.
Increasing Difficulty: The raindrops fall faster over time, increasing the challenge.
Score Tracking: Keep track of how many drops you've caught.
Audio Feedback: Enjoy the sound of rain and the satisfying splash of catching a raindrop.

### How to Play
Start the Game: Tap anywhere on the screen to start the game.
Move the Bucket: Use arrow keys or mouse to move the bucket.
Catch Raindrops: Position the bucket under falling raindrops to catch them.
Avoid Missing Drops: The game ends if you miss 5 raindrops.

### Installation
To run Drop on your local machine, follow these steps:
Clone this repository to your local machine.
```
git clone https://github.com/zubshaikk/libGDX-game
```
Ensure you have Java JDK installed.

Navigate to the project's root directory and build the project using Gradle.
```
.\gradlew desktop:run
```
Building from Source
To build a runnable JAR file:
```
.\gradlew desktop:dist
```
The JAR file will be located in desktop/build/libs/.

### Dependencies
LibGDX - The game framework used.

### Acknowledgments
Thanks to the LibGDX community for the fantastic game development framework.
Special thanks to:
* [![Junggle](http://jazzy.junggle.net/)] for Water drop sound effect.
* [![Quillraven](https://github.com/Quillraven/SimpleKtxGame/tree/master/android/assets)] for the images.
* [![acclivity](https://freesound.org/people/acclivity/sounds/28283/)] for the background Rain music.
