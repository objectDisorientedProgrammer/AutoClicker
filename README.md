# Auto Clicker
This stand-alone program will click automatically given coordinates and a click delay time.

Author: Doug Chidester

## Dependencies
This project uses Gradle 4.4.1+ as a build system.

## Building
From the project root directory use this build command:

    make all

or

    gradle jar

The generated jar file will be in /build/libs/AutoClicker-x.y.z.jar by default.

To remove build artifacts:

    make clean

or

    gradle clean

## Running
Either double click the jar file or open a terminal and use:

    make run

or

    java -jar AutoClicker.jar

Note: you may need to make the .jar executable on some systems:

    chmod u+x AutoClicker.jar

