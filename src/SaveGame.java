import java.io.*;
import java.util.*;

public class SaveGame {
    private File save;
    private int year;
    private Map<String, Country> countryMap = new HashMap<>();;
    private int bracketCount = 0;
    private boolean isProcessingProvince;
    private boolean dateSet = false;
    private String date;
    private boolean isOnEndingBracket;

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
     * @throws IOException
     */
    public Map<String, Country> countCountries() throws IOException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(save)); // This encoding seems to work for ö
        BufferedReader scanner = new BufferedReader(reader);
        String line;
        String currentOwner = null;

        int countryCount = 0;
        while ((line = scanner.readLine()) != null) {
            line = line.replaceAll("\t", "");
//            System.out.println(line);
            if (!dateSet) {
                date = extractName(line, 6, true);
                this.year = Integer.parseInt(date.substring(0, 4));
                dateSet = true;
            }

            if (line.contains("owner=\"")) {
                currentOwner = extractName(line, 7, true);
                if (!countryMap.containsKey(currentOwner)) {
                    Country country = new Country(currentOwner);
                    this.countryMap.put(currentOwner, country);
                }
                bracketCount = 1;
                isProcessingProvince = true;
            }

//            if (isOnEndingBracket && line.contains())

            if (isProcessingProvince && bracketCount != 0) {
                countBrackets(line);
                provinceSize(line, currentOwner);
            }
        }
//        System.out.println("bracketCount = " + bracketCount);
        scanner.close();
        this.countryMap = countryMap;
        return countryMap;
    }

    /**
     * Removes the last value in a given String by reducing the length by 1
     * @param line - Line being edited
     * @return Edited String value
     */
    public String removeLast(String line) {
        StringBuilder sb = new StringBuilder(line);
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Extracts text data depending on which word to be removed, the index of where is to be removed,
     * and if the last value is to be removed
     * @param line - represents the line the inputStreamReader is on, the one to be removed
     * @param index
     * @param removeLast
     * @return
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
    public void provinceSize(String line, String owner) {
        // being this method means we already know the bracket count is 1
        if (line.startsWith("size=")) {
            String size = extractName(line, 5, false);
            countryMap.get(owner).addPopCount(Integer.parseInt(size));
        }
    }

    /**
     * Keeps track of the global bracket count, updating fields in the class
     * @param line - Line currently being parsed
     */
    public void countBrackets(String line) {
        if (line.contains("{")) {
            bracketCount++;
            isOnEndingBracket = false;
        } else if (line.contains("}")) {
            bracketCount--;
            isOnEndingBracket = true;
        }
    }


    /**
     * Returns a String of the date of the save
     * @return String that represents the saves date
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns a country from a Map<String, Country> given a String which
     * is the key to the value
     * @param tag - Tag used as the key in the map
     * @return the Country value associated with the tag key
     */
    public Country getCountry(String tag) {
        return countryMap.get(tag);
    }

    /**
     * Gets the map that holds all tags and countries in a savegame
     * @return the map that holds all tags and countries
     */
    public Map<String, Country> getCountryMap() {
        return countryMap;
    }

    /**
     * Accesses the year of the save game, represented bv an int
     * @return int that represents the year of the save game
     */
    public int getYear() {
        return year;
    }
}
