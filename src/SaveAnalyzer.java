import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class SaveAnalyzer {
    private final Scanner console = new Scanner(System.in);
    private File[] fileArray;
    private final ArrayList<SaveGame> saveGameArrayList = new ArrayList<>();
    private final Set<String> allTotalTags = new TreeSet<>();
    private Set<String> specificTagSet;
    private Set<String> allHumanTags = new TreeSet<>();
    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t',
            '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

    /**
     * Method constructor
     */
    public SaveAnalyzer() {

    }

    /**
     * Gets files from user input using the console, and reports if it exists
     * or not. If the file is a directory, then will be list files, otherwise
     * will be an array that contains only the inidividual file
     */
    public void getFiles() {
        System.out.println("Enter the directory or file containing the Vic 2 Save Games: ");
        File mainFile = new File(console.nextLine());
        while (!mainFile.exists()) {
            System.out.println("That file doesn't exist. Input again");
            mainFile = new File(console.nextLine());
        }
        if (mainFile.isDirectory()) {
            this.fileArray = mainFile.listFiles();
        } else {
            this.fileArray = new File[1];
            this.fileArray[0] = mainFile;
        }
    }

    /**
     * Gets a valid file name from user input, excluding characters that won't work
     * when creating a new file
     * @return String representing the valid file name
     */
    public String getValidFileName() {
        String fileName = console.nextLine();
        while (checkIfContains(fileName, ILLEGAL_CHARACTERS)) {
            System.out.println(fileName + " is not a valid file name. Please try again.");
            fileName = console.nextLine();
        }
        return fileName;
    }

    /**
     * Checks if a given String contains any of a given set of characters.
     * @param toCheck - String to be parsed for the characters searched for
     * @param chars - Array that contains the chars to look for
     * @return true if the String contains any of the given characters
     */
    public boolean checkIfContains(String toCheck, char[] chars) {
        for (char eachChar : chars)
            if (toCheck.contains(String.valueOf(eachChar))) {
                return true;
            }
        return false;
    }

    /**
     * Method that reads each of the save game files, and registers which 
     * countries are analyzed and saved, along with which countries are 
     * human / real players. Additionally prints what stages the process is
     * at. 
     * @throws IOException - if I/O operation failed / interrupted
     */
    public void readFiles() throws IOException {
        System.out.println("Reading files");
        for (File eachFile : fileArray) {
            if (!eachFile.getName().endsWith(".DS_Store")) {
                SaveGame save = new SaveGame(eachFile);
                System.out.println("Adding accepted cultures...");
                save.countAccepted();
                System.out.println("Counting total pops...");
                Map<String, Country> allCountries = save.countCountries();
                // adds all keys to be able to track which ones are available in the future
                Set<String> keySet = allCountries.keySet();
                allTotalTags.addAll(keySet);
                allHumanTags.addAll(save.getHumanSet());
                System.out.println("Registering save game...");
                this.saveGameArrayList.add(save);
            }
        }
        saveGameArrayList.sort(new SaveComparator());
    }

    /**
     * Determines what file output the user would like for their CSV file.
     * Takes user input on whether the outputted CSV file should represent 
     * a certain subset of countries, all countries, or only player countries. 
     */
    public void setInputtingSpecificCountries() {
        System.out.println("Press 1 to output all countries");
        System.out.println("Press 2 to specify countries (may not be found if tag changes)");
        System.out.println("Press 3 to output all human countries");
        String input = console.nextLine();
        while(!input.equals("1") && !input.equals("2") && !input.equals("3")) {
            System.out.println("Invalid input. Please try again");
            input = console.nextLine();
        }
        if (input.equals("1")) {
            // makes the specific tags to be printed in the next step the sorted order of all total tags
            this.specificTagSet = allTotalTags;
        } else if (input.equals("2")){
            this.specificTagSet = new TreeSet<>(Arrays.asList(getCountriesToInput()));
        } else {
            this.specificTagSet = allHumanTags;
        }
    }

    /**
     * Receives user input on which specific countries should be ouputted. 
     * Receives the tags of countries from the user, and reasks the user
     * if any of the tokens don't match a given tag. 
     * @return String[] array that represented each tag to be used by the user
     */
    public String[] getCountriesToInput() {
        System.out.println("Input a list of countries (by tag, separated by space)");
        String line = (console.nextLine()).toUpperCase();
        String[] tagArray = line.split("\\s+");
        for (String eachTag : tagArray) {
            if(!allTotalTags.contains(eachTag)) {
                System.out.println("There is no " + eachTag + " in any save game.");
                tagArray = getCountriesToInput();
            }
        }
        return tagArray;
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
    public static void eachSavePrintCSV(List<SaveGame> saves, Set<String> tagSet, String fileName, boolean isAccepted) {
        String eol = System.lineSeparator();
        boolean tagHasPops = false;
        boolean tagPlaced = false;
        boolean hasValues;
        try (Writer writer = new FileWriter(fileName + ".csv") ) {
            writer.append("Tag")
                    .append(',');
            for (SaveGame save : saves) {
                writer.append(save.getDate())
                        .append(',');
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
                            writer.append(tag)
                                    .append(',');
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
        }  catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Method that retrieves the users input to determine the names of the 
     * outputted CSV files, and calls the method to output them
     */
    public void printFiles() {
        System.out.println("Accepted pop file name: ");
        String acceptedFileName = getValidFileName();
        System.out.println("Total pops file name: ");
        String totalPopsName = getValidFileName();
        eachSavePrintCSV(saveGameArrayList, specificTagSet, acceptedFileName, true);
        eachSavePrintCSV(saveGameArrayList, specificTagSet, totalPopsName, false);
    }
}
