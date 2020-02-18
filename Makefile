CC=gradle

all: src/main/java/com/localarea/network/doug/Driver.java
	$(CC) jar

run: all
	java -jar build/libs/AutoClicker-0.99.9.jar

clean:
	$(CC) clean

