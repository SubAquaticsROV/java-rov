# Java ROV

This is the top-side program for the Lakeview Technology Academy's ROV club, written in java.

## Running the Program

To run the program, use the following commands:

```
git clone https://github.com/SubAquaticsROV/java-rov.git
cd java-rov
.\gradlew.bat run # OR ./gradlew run on linux 
```

## Issues Running the Program

### Q: Why isn't the command line interface working?

__A:__ Gradle takes control of the System.in and System.out when it is running
the program, so it may be interfering with the program. To ensure best results,
use the following commands to run the program.

```
cd java-rov              # Navigate into our local repository
.\gradlew.bat distZip    # Bundle our project as a zip
cd build\distributions   # navigate to zip file
unzip java-rov.zip
cd java-rov             
.\bin\java-rov.bat       # run the program
```