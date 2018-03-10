# CSCI 6511 - Project 2 - Generalized Tic Tac Toe Agent

* Professor: Amrinder Arora
* Semester: Spring 2018
* Group: The Code Knights

This program implements a rational agent that tries to win at a game of generalized Tic Tac Toe.

## Requirements

* Java JDK 8
* Maven 3

## Build Instructions

Clean project and compile:

```bash
cd /path/to/csci6511-proj2-tictactoe/
mvn clean compile
```

To just run unit tests:

```bash
cd /path/to/csci6511-proj2-tictactoe/
mvn test
```

To create shaded JAR file (runnable JAR file with all dependencies included):

```bash
cd /path/to/csci6511-proj2-tictactoe/
mvn package
# shaded JAR file will be written to ./target/csci6511-proj2-tictactoe-<version>-shaded.jar
```

To clean, compile, package, and install into your local maven repo:

```bash
cd /path/to/csci6511-proj2-tictactoe/
mvn clean install
```

## Usage Instructions

To run the program from the command line:

```bash
# java -jar ./target/csci6511-proj2-tictactoe-<version>-shaded.jar <cmd> <args>

# For general usage info, use the '--help' flag:
java -jar ./target/csci6511-proj2-tictactoe-1.2-SNAPSHOT-shaded.jar --help

# For usage info for a particular command, also use the '--help' flag:
java -jar ./target/csci6511-proj2-tictactoe-1.2-SNAPSHOT-shaded.jar <cmd> --help
```

## Creating and Playing an Online Game

```bash
# 1. Create a new game
java -jar ./target/csci6511-proj2-tictactoe-1.2-SNAPSHOT-shaded.jar \
  create-online-game --user-id <your userId> --api-key <your apiKey> \
    --player1-id <your team id> --player2-id <opponent team id>
# Make note of output, which contains gameId

# 2. In first session, play as player1
java -jar ./target/csci6511-proj2-tictactoe-1.2-SNAPSHOT-shaded.jar \
  play-online-game --user-id <your userId> --api-key <your apiKey> \
    --player1-id <your team id> --player2-id <opponent team id> \
    --game-id <gameId from step 1> \
    --dim 18 --win-length 8 # or whatever board size you agreed upon

# 3. In another session, play as player2
java -jar ./target/csci6511-proj2-tictactoe-1.2-SNAPSHOT-shaded.jar \
  play-online-game --user-id <your userId> --api-key <your apiKey> \
    --player1-id <your team id> --player2-id <opponent team id> \
    --game-id <gameId from step 1> \
    --dim 18 --win-length 8 \
    --player2
```

## Screenshot

![Screenshot](/screenshot.png?raw=true "Screenshot")
