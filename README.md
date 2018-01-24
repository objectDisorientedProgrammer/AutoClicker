# AutoClicker
This stand-alone program will click automatically given coordinates and a click delay time.

author Doug Chidester

## Building
To build from the command line use:

	mkdir bin/
	javac src/com/localarea/network/doug/*.java -d bin/

To create an executable jar file use:

	cd src
	jar cmvf ../META-INF/MANIFEST.MF AutoClicker.jar ../bin/com/localarea/network/doug/*.class -C ../res/

Run with:

	java -jar AutoClicker.jar


