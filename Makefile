CC=gradle

all: src/main/java/com/localarea/network/doug/Driver.java
	$(CC) jar

run: all
	java -jar build/libs/AutoClicker.jar

clean:
	$(CC) clean

