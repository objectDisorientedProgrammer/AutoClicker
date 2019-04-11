# Auto Clicker
This stand-alone program will click automatically given coordinates and a click delay time.

Author: Doug Chidester

## Dependencies
This project uses Gradle 3.4.1 (or newer?) as a build system.

## Building
From the project root directory use this build command:

    gradle jar

The generated jar file will be in /build/libs/AutoClicker-x.y.z.jar by default.

To clean:

    gradle clean

## Running
Either double click the jar file or open a terminal and use:

    java -jar AutoClicker.jar

Note: you may need to make the .jar executable on some systems:

    chmod u+x AutoClicker.jar

