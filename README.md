# Victoria 2 Save Game Analyzer

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/yourusername/VicAnalyzerProject)](https://github.com/yourusername/VicAnalyzerProject/releases/latest).

## Requirements

- **Java**: Java 17 or higher is required (install guide below)
  - Windows: Download and install from [Oracle's website](https://www.oracle.com/java/technologies/downloads/#java17) or [OpenJDK](https://adoptium.net/)


## Quick Start

1. **Download**: Get the latest `vic-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar` from our [Releases Page](https://github.com/yourusername/VicAnalyzerProject/releases/latest)

2. **Install Java** (if you don't have it):

   **Easiest Method (Recommended)**:
   a. Download Eclipse Temurin JRE installer:
      - [Windows](https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jre_x64_windows_hotspot_17.0.10_7.msi)
     This will auto-install it (It doesn't redirect to a page, just downloads the java installer)
   b. Double-click the downloaded installer
   c. Follow the simple installation steps

3. Run the application:
   ```bash
   java -jar vic-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Output Files

The analysis will create two CSV files in the same folder where the JAR file is located:
- `accepted_pops.csv`: Contains the accepted population data
- `total_pops.csv`: Contains the total population data

For example, if your JAR file is in `C:\Games\Victoria2\Analyzer`, the output files will also be created in `C:\Games\Victoria2\Analyzer`.

NOTE: In the .csv files if a nation changes tag the data will be on separate rows; e.g. Prussia (PRU), North German Confederation (NGF), and Germany (GER) will all be on different rows and you'll have to merge them.

## Security

This application is distributed as a signed JAR file with SHA-256 checksums for verification. Always download from the official [Releases Page](https://github.com/yourusername/VicAnalyzerProject/releases/latest) and verify the signature and checksum if possible!
