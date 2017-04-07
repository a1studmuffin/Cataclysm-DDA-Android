# Cataclysm: Dark Days Ahead (Android Port)

This is an unofficial port of [Cataclysm: Dark Days Ahead](https://github.com/CleverRaven/Cataclysm-DDA/) (0.C Experimental) for Android.

Features:
- Tilesets, sound, localization, lua mod support
- Backwards compatible with desktop 0.C save games
- Stores game data in a publicly writeable location:  
`/sdcard/Android/data/com.MichaelDavies.CataclysmDDA/files/`
- Supports installation of custom tilesets, mods, soundpacks, and save games
- Works with a physical keyboard or virtual keyboard & touchscreen
- Auto-saves when the app loses focus (screen locked, switched apps etc.)
- Highly customizable touch controls and  automatic in-game contextual shortcuts

It uses the [Android NDK](https://developer.android.com/ndk/index.html) to compile the game's C++ source code as a native library, with some Java glue to hold everything together.

The Cataclysm-DDA version number used by this port can be found here: [version.h](https://github.com/a1studmuffin/Cataclysm-DDA-Android/blob/master/jni/src/version.h).

## Download

Get it on [Google Play](https://play.google.com/store/apps/details?id=com.MichaelDavies.CataclysmDDA), or download an APK from the [releases](https://github.com/a1studmuffin/Cataclysm-DDA-Android/releases) section.

## Overview

Since Cataclysm-DDA supports a graphical tiles build using the [SDL](https://www.libsdl.org/) graphics library, this project is unsurprisingly based on SDL's [Android project template](https://github.com/a1studmuffin/Cataclysm-DDA-Android/tree/master/jni/SDL2/android-project). For more information see the SDL docs [README-android.md](https://github.com/a1studmuffin/Cataclysm-DDA-Android/blob/master/jni/SDL2/docs/README-android.md).

Here's a rundown of what's in each folder:

`assets/android` - Android-specific game assets.  
`assets/data` - An exact copy of Cataclysm-DDA's [data](https://github.com/CleverRaven/Cataclysm-DDA/tree/master/data) folder.  
`assets/gfx` - An exact copy of Cataclysm-DDA's [gfx](https://github.com/CleverRaven/Cataclysm-DDA/tree/master/gfx) folder.  
`assets/lang` - An exact copy of Cataclysm-DDA's [lang](https://github.com/CleverRaven/Cataclysm-DDA/tree/master/lang) folder.  
`assets/lua` - An exact copy of Cataclysm-DDA's [lua](https://github.com/CleverRaven/Cataclysm-DDA/tree/master/lua) folder.  
`jni/` - C/C++ source code for all native libraries. Includes modified versions of external libraries `SDL2-2.0.5`, `SDL2_image-2.0.1`, `SDL2_mixer-2.0.1`, `SDL2_ttf-2.0.14`, `lua5.1.5`, and `libintl-lite-0.5` (a lightweight `gettext` replacement). Also includes `src` which is a modified version of Cataclysm-DDA's [src](https://github.com/CleverRaven/Cataclysm-DDA/tree/master/src) folder. All changes are guarded with `__ANDROID__` preprocessor defines.  
`res/` - Android app resources (icons etc.)  
`src/` - Java app source code. Note that some changes have been made to [SDLActivity.java](https://github.com/a1studmuffin/Cataclysm-DDA-Android/blob/master/src/org/libsdl/app/SDLActivity.java) from the original SDL Android project template's [SDLActivity.java](https://github.com/a1studmuffin/Cataclysm-DDA-Android/blob/master/src/org/libsdl/app/SDLActivity.java).

## Compiling

### Dependencies

This port was developed under Windows using:

- Java JDK 1.8.0
- Apache Ant 1.10.0
- Android SDK 24
- Android NDK r10e

It compiles via the Android command line tools (as opposed to Android Studio).

It should also compile under Linux or Mac OS if your environment is set up correctly, though this is untested.

You'll need commands `ndk-build` and `ant` to build the APK, as well as `adb` to deploy the APK to a device. 

For more information see the relevant platform installation guide for each dependency above.

### Steps

This modified Cataclysm source code comes with [version.h](https://github.com/a1studmuffin/Cataclysm-DDA-Android/blob/master/jni/src/version.h) and Lua [catabindings.cpp](https://github.com/a1studmuffin/Cataclysm-DDA-Android/blob/master/jni/src/lua/catabindings.cpp) pre-generated, so there's no need to run those steps before compiling.

First, use Android Tools to update the project. From the command line in the project folder, run:

    $ android update project -p .

Then compile the C/C++ source code into debuggable native shared libraries:

    $ ndk-build
    
or to build release libraries, run:

    $ ndk-build NDK_DEBUG=0 APP_CFLAGS+=-DRELEASE=1

Finally, build the APK with:

    $ ant debug
or

    $ ant release
    
Note: The release APK will be unsigned. To sign it, you need to generate a keystore and point to it within `ant.properties`. More info on that [here](http://shallowsky.com/blog/programming/android-ant-build.html).

## Maintenance

To keep maintenance simple, there are a few policies I'm adhering to:

1) No changes should ever be made to Cataclysm-DDA's original data files under `assets`, except when updating the full app (code and data) to a newer Cataclysm-DDA version.

2) All modifications to Cataclysm's C/C++ source code under `jni/src` must be guarded with `__ANDROID__` preprocessor defines, and must only address Android-specific issues. This simplifies the process of updating the app to newer Cataclysm-DDA versions.

3) Updating the build to a newer Cataclysm-DDA version must always be done as an atomic operation. Specifically this means I won't be accepting pull requests for spot fixes to code or data from newer versions of the game. Instead everything must come at once, including merging/updating all game code (`jni/src`) and assets (`data`, `gfx`, `lang`, `lua`), regenerating `version.h` and lua bindings (`catabindings.cpp`), updating the assets file count in `SplashScreen.java`, and ticking `AndroidManifest.xml` with a new version code + name. This ensures the Android build is always at parity with the desktop build for the same version as shown in the main menu.

## Contributing

You're welcome to report an issue or submit a pull request with Android-specific bugfixes and features, as long as it falls within the policies above. If it's a large change you might want to ping me first.

If you'd like to contribute to the actual game itself (not the Android port), please do this upstream at the [Cataclysm-DDA](https://github.com/CleverRaven/Cataclysm-DDA/) GitHub page and I'll (eventually) pull their fixes downstream when I update versions periodically.

If I get lazy and stop updating, I'm totally cool with someone else forking the project and continuing to work on it. Again, it's probably best to ping me in this situation due to the Google Play app store listing, but I'm happy to do whatever is best for the game and the community.

You can reach me at m@michaeldavies.com.au.

## Frequently Asked Questions

Please see the [Google Play app description](https://play.google.com/store/apps/details?id=com.MichaelDavies.CataclysmDDA) for common questions and answers.