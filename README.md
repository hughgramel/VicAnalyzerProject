# Victoria 2 Save Game Analyzer

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Overview

**Victoria 2 Save Game Analyzer** is a graphical tool designed to analyze Victoria 2 save game files. It provides a user-friendly interface to analyze various aspects of your Victoria 2 saves, making it easy to understand the state of your game.

## Download and Verification

1. Go to the [Releases](https://github.com/yourusername/vic-analyzer/releases) page
2. Download the latest `vic-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar`
3. Download the `SHA256SUMS.txt` file
4. Verify the JAR file's integrity:
   - Windows (PowerShell):
     ```powershell
     Get-FileHash vic-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar -Algorithm SHA256 | Format-List
     ```
   - macOS/Linux:
     ```bash
     sha256sum vic-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar
     ```
5. Compare the output hash with the one in `SHA256SUMS.txt`
6. Verify the JAR signature (optional but recommended):
   ```bash
   jarsigner -verify -verbose -certs vic-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Requirements

- **Java**: Java 17 or higher is required
  - Windows: Download and install from [Oracle's website](https://www.oracle.com/java/technologies/downloads/#java17) or [OpenJDK](https://adoptium.net/)
  - macOS: Install via [Oracle's website](https://www.oracle.com/java/technologies/downloads/#java17) or using Homebrew: `brew install openjdk@17`
  - Linux: Install using your package manager (e.g., `sudo apt install openjdk-17-jre` for Ubuntu/Debian)

## Running the Application

1. Open a terminal/command prompt
2. Navigate to the folder containing the JAR file
3. Run the following command:
   ```bash
   java -jar vic-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```
4. The graphical interface will open, allowing you to select and analyze your Victoria 2 save files

## Verifying Java Installation

To verify that Java is installed correctly, open a terminal/command prompt and run:
```bash
java -version
```

You should see output indicating Java 17 or higher is installed. If you get a "command not found" error, make sure Java is installed and added to your system's PATH environment variable.

## For Developers: Creating Signed Releases

1. Generate a keystore (one-time setup):
   ```bash
   keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release
   ```

2. Set up environment variables:
   ```bash
   export KEYSTORE_PATH=path/to/release-key.jks
   export KEYSTORE_ALIAS=release
   export KEYSTORE_PASSWORD=your-keystore-password
   export KEY_PASSWORD=your-key-password
   ```

3. Build and sign the JAR:
   ```bash
   mvn clean package
   ```

4. Generate SHA-256 checksum:
   ```bash
   sha256sum target/vic-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar > SHA256SUMS.txt
   ```

5. Create a new GitHub release:
   - Tag the version
   - Upload the signed JAR and SHA256SUMS.txt
   - Include release notes
   - Mark as pre-release or production release as appropriate

## Security Notes

- Always download releases from the official GitHub releases page
- Verify the SHA-256 checksum before running
- The JAR is signed with our release certificate
- Report security concerns via GitHub issues or security advisories
