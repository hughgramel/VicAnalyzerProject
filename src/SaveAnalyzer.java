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

    public SaveAnalyzer() {

    }

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


    public String getValidFileName() {
        String fileName = console.nextLine();
        while (checkIfContains(fileName, ILLEGAL_CHARACTERS)) {
            System.out.println(fileName + " is not a valid file name. Please try again.");
            fileName = console.nextLine();
        }
        return fileName;
    }

    public boolean checkIfContains(String toCheck, char[] chars) {
        for (char eachChar : chars)
            if (toCheck.contains(String.valueOf(eachChar))) {
                return true;
            }
        return false;
    }

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

    public void printFiles() {
        System.out.println("Accepted pop file name: ");
        String acceptedFileName = getValidFileName();
        System.out.println("Total pops file name: ");
        String totalPopsName = getValidFileName();
        eachSavePrintCSV(saveGameArrayList, specificTagSet, acceptedFileName, true);
        eachSavePrintCSV(saveGameArrayList, specificTagSet, totalPopsName, false);
    }
}
