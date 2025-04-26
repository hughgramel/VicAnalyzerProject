package com.vicanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Consumer;

public class SaveAnalyzer {
    private File[] fileArray;
    private final ArrayList<SaveGame> saveGameArrayList = new ArrayList<>();
    private final Set<String> allTotalTags = new TreeSet<>();
    private Set<String> specificTagSet;
    private final Set<String> allHumanTags = new TreeSet<>();
    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t',
            '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

    /**
     * Method constructor
     */
    public SaveAnalyzer() {

    }

    public void setFiles(File[] files) {
        this.fileArray = files;
    }

    /**
     * Method that reads each of the save game files, and registers which 
     * countries are analyzed and saved, along with which countries are 
     * human / real players. Additionally prints what stages the process is
     * at. 
     * @throws IOException - if I/O operation failed / interrupted
     */
    public void readFilesWithProgress(Consumer<Integer> progressCallback) throws IOException {
        System.out.println("Reading files");
        int num = fileArray.length;
        int total = num;
        
        for (File eachFile : fileArray) {
            if (!eachFile.getName().endsWith(".DS_Store")) {
                SaveGame save = new SaveGame(eachFile);
                save.countAccepted();
                Map<String, Country> allCountries = save.countCountries();
                Set<String> keySet = allCountries.keySet();
                allTotalTags.addAll(keySet);
                allHumanTags.addAll(save.getHumanSet());
                this.saveGameArrayList.add(save);
            }
            num--;
            int progress = (int)(((double) (total - num) / total) * 100);
            progressCallback.accept(progress);
        }
        saveGameArrayList.sort(new SaveComparator());
    }

    /**
     * Determines what file output the user would like for their CSV file.
     * Takes user input on whether the outputted CSV file should represent 
     * a certain subset of countries, all countries, or only player countries. 
     */
    public void setAnalysisType(int choice) {
        switch (choice) {
            case 0: // All countries
                this.specificTagSet = allTotalTags;
                break;
            case 2: // Human countries
                this.specificTagSet = allHumanTags;
                break;
            default: // Specific countries - handled by setSpecificTags
                break;
        }
    }

    public void setSpecificTags(Set<String> tags) {
        this.specificTagSet = new TreeSet<>(tags);
    }

    public Set<String> getAllTotalTags() {
        return allTotalTags;
    }

    /**
     * Prints a representation of the data given in each save to a CSV file, 
     * one with the accepted population of a given set of countries and one 
     * with the total population of each one in each save, 
     * @param saves - Set of SaveGames that the data is parsed from
     * @param tagSet - Set of the specified tags to have data reported from
     * @param fileName - Name of the CSV file to be outputted
     * @param isAccepted - Boolean that determines if the outputted data is
     *                     meant to be the countries "accepted" population\
     *                     or standart population
     */
    private static void eachSavePrintCSV(List<SaveGame> saves, Set<String> tagSet, String fileName, boolean isAccepted) throws IOException {
        String eol = System.lineSeparator();
        boolean tagHasPops = false;
        boolean tagPlaced = false;
        boolean hasValues;
        
        try (Writer writer = new FileWriter(fileName)) {
            writer.append("Tag").append(',');
            for (SaveGame save : saves) {
                writer.append(save.getDate()).append(',');
            }
            writer.append(eol);
            
            for (String tag : tagSet) {
                hasValues = false;
                for (SaveGame save : saves) {
                    if (save.getCountry(tag).getPopulationSizeNumber() != 0) {
                        hasValues = true;
                    }
                }
                if (hasValues) {
                    for (SaveGame save : saves) {
                        if (!tagPlaced) {
                            writer.append(tag).append(',');
                            tagPlaced = true;
                        }
                        if (isAccepted) {
                            writer.append(String.valueOf(save.getCountry(tag).getAcceptedPopTotal()));
                        } else {
                            writer.append(String.valueOf(save.getCountry(tag).getPopulationSizeNumber()));
                        }
                        writer.append(',');
                        tagHasPops = true;
                    }
                    if (tagHasPops) {
                        writer.append(eol);
                    }
                    tagHasPops = false;
                    tagPlaced = false;
                }
            }
        }
    }

    /**
     * Method that retrieves the users input to determine the names of the 
     * outputted CSV files, and calls the method to output them
     */
    public void printFiles(String acceptedFileName, String totalPopsName) throws IOException {
        eachSavePrintCSV(saveGameArrayList, specificTagSet, acceptedFileName, true);
        eachSavePrintCSV(saveGameArrayList, specificTagSet, totalPopsName, false);
    }
}
