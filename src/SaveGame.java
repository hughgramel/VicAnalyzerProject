import com.sun.jdi.Value;

import java.io.*;
import java.util.HashSet;
import java.util.*;
import java.util.Scanner;
import java.util.Set;


public class SaveGame {
    private File save;
    private Set<String> countries;

    private Map<String, Country> countryMap = new HashMap<>();;
    private int bracketCount = 0;

    private boolean isProcessingProvince;

    /**
     * Constructs the save game using the file location
     * @param save
     */
    public SaveGame(String save) {
        this.save = new File(save);
    }

    /**
     * Counts the total number of countries in a save game using their unique tags.
     * Only takes into account countries that are on the map and own provinces
     * @return map that contains all country tags
     * @throws IOException
     */
    public Map<String, Country> countCountries() throws IOException {
        Scanner fileReader = new Scanner(save);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(save)); // This encoding seems to work for ö
        BufferedReader scanner = new BufferedReader(reader);
        String line;
        String currentOwner = null;

        int countryCount = 0;
        while ((line = scanner.readLine()) != null) {
            line = line.replaceAll("\t", "");
//            System.out.println(line);

            if (line.contains("owner=\"")) {
                currentOwner = extractName(line, 7, true);
                if (!countryMap.containsKey(currentOwner)) {
                    Country country = new Country(currentOwner);
                    this.countryMap.put(currentOwner, country);
                }
                bracketCount = 1;
                isProcessingProvince = true;
            }

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

    public void provinceSize(String line, String owner) {
        // beingin this method means we already know the bracket count is 1
        if (line.startsWith("size=")) {
            String size = extractName(line, 5, false);
            countryMap.get(owner).addPopCount(Integer.parseInt(size));
        }
    }

    public Map<String, Country> printEach() {
        Scanner console = new Scanner(System.in);
        System.out.println("Input a list of countries");
        String line = console.nextLine();
        Map<String, Country> getCountryMap = new HashMap<>();

        Scanner lineScanner = new Scanner(line);
        String tag = null;
        while (lineScanner.hasNext()) {
            tag = lineScanner.next();
            getCountryMap.put(tag, countryMap.get(tag));
            // here new map made with specific tags and country objects from the OG one
        }
        return getCountryMap;
    }

    public void countBrackets(String line) {
        if (line.contains("{")) {
            bracketCount++;
        } else if (line.contains("}")) {
            bracketCount--;
        }
    }


    /**
     * Iterates through a set containing the tags and objects of countries,
     * printing them out in a formatted manner
     */
    public void visualizeData() {
        int count = 1;
        for (Map.Entry<String, Country> entry : countryMap.entrySet()) {
            String key = entry.getKey();
            Country country = entry.getValue();
            System.out.printf("%d\t: %s, %,d\n", count++, key, country.getPopulationSizeInt());
        }
    }

    public void printCSV(Map<String, Country> specificCountries) {
        String eol = System.getProperty("line.separator");

        try (Writer writer = new FileWriter("somefile.csv")) {
            for (Map.Entry<String, Country> entry : specificCountries.entrySet()) {
                writer.append(entry.getKey())
                        .append(',')
                        .append(entry.getValue().getPopulationSize())
                        .append(eol);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

    }

    public void printCSV() {
        String eol = System.getProperty("line.separator");

        try (Writer writer = new FileWriter("somefile.csv")) {
            for (Map.Entry<String, Country> entry : countryMap.entrySet()) {
                writer.append(entry.getKey())
                        .append(',')
                        .append(entry.getValue().getPopulationSize())
                        .append(eol);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

    }



}
