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
# shaded JAR file will be written to ./target/csci6511-proj2-tictactoe-0.1-SNAPSHOT-shaded.jar
```

To run the program:

```bash
# java -jar ./target/csci6511-proj2-tictactoe-0.1-SNAPSHOT-shaded.jar <args>

# For example:
java -jar ./target/csci6511-proj2-tictactoe-0.1-SNAPSHOT-shaded.jar --dim 3 --win-length 3 --state _ _ _ _ O X X _ _ O _ _ X X O

# Or, for usage info:
java -jar ./target/csci6511-proj2-tictactoe-0.1-SNAPSHOT-shaded.jar --help
```
