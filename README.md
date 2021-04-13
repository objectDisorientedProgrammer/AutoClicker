# Auto Clicker
This stand-alone program will click automatically given coordinates and a click delay time.

Author: Doug Chidester

## Dependencies
Java 8+

Gradle 4.4.1+

## Building
From the project root directory use this build command:

    gradle build

The generated jar file will be in **/build/libs/AutoClicker.jar** by default.

To remove build artifacts:

    gradle clean

## Running
Double click the jar file or open a terminal and use:

    gradle run
    
or

    java -jar /build/libs/AutoClicker.jar

Note: you may need to make the .jar executable on some systems:

    chmod u+x /build/libs/AutoClicker.jar
    

