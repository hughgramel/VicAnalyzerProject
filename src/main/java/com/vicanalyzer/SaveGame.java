package com.vicanalyzer;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents the data of one save game, and all of its corresponding countries with
 * their population count
 */
public class SaveGame {
    private final File save;
    private int year;
    private int month;
    private int day;
    private final int[] dateArray = new int[3];
    private final Map<String, Country> countryMap = new TreeMap<>();

    private int bracketCount = 0;
    private boolean isProcessingProvince;
    private boolean dateSet = false;
    public Set<String> humanSet = new TreeSet<>();
    private String date;
    private final Pattern pattern = Pattern.compile("\t");
    private static final String regex = "\\b[A-Z]{3}=";

    /**
     * Constructs the save game using the file location
     * @param saveFile - File to be parsed
     */
    public SaveGame(File saveFile) {
        this.save = saveFile;
    }

    /**
     * Counts the total number of countries in a save game using their unique tags.
     * Only takes into account countries that are on the map and own provinces
     * @return map that contains all country tags
     * @throws IOException - if no input
     */
    public Map<String, Country> countCountries() throws IOException {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(Files.newInputStream(save.toPath())));
        String line;
        String currentOwner = null;
        boolean gettingAccepted = false;
        int lastSizeRecorded = 0;
        while ((line = scanner.readLine()) != null) {
            line = pattern.matcher(line).replaceAll("");
            if (!dateSet) {
                date = extractName(line, 6, true);
                setDateArray();
                dateSet = true;
            }
            if (line.contains("owner=\"")) {
                currentOwner = extractName(line, 7, true);
                if (!countryMap.containsKey(currentOwner)) {
                    // if country map doesn't already have the country, add it from
                    // country already created in acceptedDatabase
                    Country country = new Country(currentOwner);
                    this.countryMap.put(currentOwner, country);
                }
                bracketCount = 1;
                isProcessingProvince = true;
            }

            if (gettingAccepted) {
                // happens on next line, so now on "yankee=protestant"
                String[] parts = line.split("=");
                //splits along regex
                //checks if accepted pop list of country has this accepted
                // MAYBE CHANGE BACK TO ACCEPTED
                if (countryMap.get(currentOwner).getAcceptedPopList().contains(parts[0])) {
                    countryMap.get(currentOwner).addAcceptedPopTotal(lastSizeRecorded);
                }
            }
            if (isProcessingProvince && bracketCount != 0) {
                countBrackets(line);
                lastSizeRecorded = provinceSize(line, currentOwner);
                gettingAccepted = true;
            }
        }
        scanner.close();
        return this.countryMap;
    }

    /**
     * Registers the accepted cultures of each country in a given save game,
     * creating a country if it doesn't exist yet
     * @throws IOException if an I/O exception of some sort has occurred
     */
    public void countAccepted() throws IOException {
        InputStreamReader reader = new InputStreamReader(Files.newInputStream(save.toPath()));
        BufferedReader scanner = new BufferedReader(reader);
        String line;
        bracketCount = 0;
        String currentOwner = null;
        boolean findTagOnce = true;
        boolean tagFound = false;
        boolean insideTagData = false;
        boolean cultureReading = false;
        int bracketPositionSave = 0;

        while ((line = scanner.readLine()) != null) {
            line = pattern.matcher(line).replaceAll("");
            if (line.matches(regex) && !countryMap.containsKey(extractName(line, 0, true))) {
                String tagName = extractName(line, 0, true);
                Country country = new Country(tagName);
                this.countryMap.put(tagName, country);
            }
            if (insideTagData && bracketCount != 0) {
                countBrackets(line);
                if (line.contains("primary_culture=\"")) {
                    String culture = extractName(line, 17, true);
                    countryMap.get(currentOwner).addAcceptedPopType(culture);
                    bracketPositionSave = bracketCount;
                }
                if (cultureReading) {
                    if (bracketCount == bracketPositionSave) {
                        cultureReading = false;
                    } else if (line.startsWith("\"")){
                        String culture = extractName(line, 1, true);
                        countryMap.get(currentOwner).addAcceptedPopType(culture);
                    }
                }

                if (line.startsWith("culture=")) {
                    cultureReading = true;
                }
            }

            if (line.contains("human") || line.contains("tax_base")) {
                insideTagData = true;
                bracketCount = 1;
                if (line.contains("human")) {
                    countryMap.get(currentOwner).setIsHuman();
                    humanSet.add(currentOwner);
                }
            }

            if(tagFound && findTagOnce) {
                bracketCount = 1;
                findTagOnce = false;
            }

            if (line.length() > 3) {
                if (countryMap.containsKey(extractName(line, 0, true)) && (bracketCount == 0)) {
                    tagFound = true;
                    currentOwner = line.substring(0, 3);
                }
            }
        }
        scanner.close();
    }

    /**
     * Extracts text data depending on which word to be removed, the index of where is to be removed,
     * and if the last value is to be removed
     * @param line - represents the line the inputStreamReader is on, the one to be removed
     * @param index - index of how many characters to remove
     * @param removeLast - set to true if wanting to remove last value in a char
     * @return the extracted string
     */
    public String extractName(String line, int index, boolean removeLast) {
        StringBuilder sb = new StringBuilder(line);
        sb.delete(0, index);
        if (removeLast) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Returns how many people of any group are in a certain province
     * @param line - line that is currently being parsed
     * @param owner - key of a country object whose total will be added to
     */
    public int provinceSize(String line, String owner) {
        // being this method means we already know the bracket count is 1
        int sizeInt = 0;
        if (line.startsWith("size=")) {
            String size = extractName(line, 5, false);
            sizeInt = Integer.parseInt(size);
            countryMap.get(owner).addPopCount(sizeInt);
        }
        return sizeInt;
    }

    /**
     * Keeps track of the global bracket count, updating fields in the class
     * @param line - Line currently being parsed
     */
    public void countBrackets(String line) {
        if (line.contains("{")) {
            bracketCount++;
        }
        if (line.contains("}")) {
            bracketCount--;
        }
    }

    /**
     * Sets date array based on the date separated by "."
     */
    public void setDateArray() {
        String[] stringDateArray = date.split("\\.");
        for (int i = 0; i <= 2; i++) {
            dateArray[i] = Integer.parseInt(stringDateArray[i]);
        }
        this.year = dateArray[0];
        this.month = dateArray[1];
        this.day = dateArray[2];
    }

    public String getDate() {
        return date;
    }

    public Country getCountry(String tag) {
        return countryMap.get(tag);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public Set<String> getHumanSet() {
        return humanSet;
    }
}
