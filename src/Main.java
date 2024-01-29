import java.io.*;
import java.util.*;
////TAI TUR MUS ARI ARC PLA PLC DUA BRG HLR SPA INC JAP BTV VEN RUS good tags
public class Main {
    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the Vic 2 pop parser, made by Hgram for my boy Entara");
        System.out.println("Enter the directory or file containing the Vic 2 Save Games: ");
        File f = new File(console.nextLine());
        while (!f.exists()) {
            System.out.println("That file doesn't exist. Input again");
            f = new File(console.nextLine());
        }
        File[] files;
        files = f.listFiles();
        List<SaveGame> saveList = new ArrayList<SaveGame>();
        SaveGame save;
        Set<String> tagList = new HashSet<>();
        boolean specificCountries = false;
        System.out.println("Press 1 to output all countries");
        System.out.println("Press 2 to specify countries (may not be found if tag changes)");
        String input = console.nextLine();
        while(!input.equals("1") && !input.equals("2")) {
            System.out.println("Invalid input. Please try again");
            input = console.nextLine();
        }
        Set<String> inputtedTags = null;
        if (input.equals("2")) {
            inputtedTags = getCoutriesToInput(console);
            specificCountries = true;
        }
        if (f.isDirectory()) {
            for (File eachFile : files) {
                if (!eachFile.getName().endsWith(".DS_Store")) {
                    save = new SaveGame(eachFile);
                    save.countAccepted();
                    Map<String, Country> allCountries = save.countCountries();
                    if (specificCountries) {
                        tagList = inputtedTags;
                    } else {
                        tagList = allCountries.keySet();
                    }
                    saveList.add(save);
                    for (String eachTag : tagList) {
                        if (!allCountries.containsKey(eachTag)) {
                            System.out.println("Error: " + eachTag + " not found in savegame");
                            return;
                        }
                    }
                }
            }
        } else {
            save = new SaveGame(f);
            save.countAccepted();
            Map<String, Country> allCountries = save.countCountries();
            if (specificCountries) {
                tagList = inputtedTags;
            } else {
                tagList = allCountries.keySet();
            }
            saveList.add(save);
            for (String eachTag : tagList) {
                if (!allCountries.containsKey(eachTag)) {
                    System.out.println("Error: " + eachTag + " not found in save game");
                    return;
                }
            }
        }
        Collections.sort(saveList, new SaveComparator());
        System.out.println("Accepted pop file name: ");
        String acceptedFileName = console.nextLine();
        System.out.println("Total pops file name: ");
        String totalPopsName = console.nextLine();
        eachSavePrintCSV(saveList, tagList, acceptedFileName, true);
        eachSavePrintCSV(saveList, tagList, totalPopsName, false);
        System.out.println("CSV ouputted");
     }
    /**
     * Creates a list of the countries that a user wants to interact with
      * @param console - Receives user input
     * @return list of country tags that the user inputs
     */
    public static Set<String> getCoutriesToInput(Scanner console) {
        System.out.println("Input a list of countries (by tag, separated by space)");
        String line = (console.nextLine()).toUpperCase();
        Set<String> countryList = new TreeSet<>();
        Scanner lineScanner = new Scanner(line);
        String tag = null;
        while (lineScanner.hasNext()) {
            tag = lineScanner.next();
            countryList.add(tag);
        }
        return countryList;
    }

    /**
     * Given a list of tags in each save game, prints out each tag in the given years
     * @param saves - List of the save games to be read
     * @param tagList - List of tags to be outputted and saved in the CSV
     * @param fileName - name of CSV file to be ouputted to
     */
    public static void eachSavePrintCSV(List<SaveGame> saves, Set<String> tagList, String fileName, boolean isAccepted) {
        String eol = System.getProperty("line.separator");
        boolean tagHasPops = false;
        boolean tagPlaced = false;
        boolean hasValues = false;
        List<String> myArray = new ArrayList<>();
        myArray.addAll(tagList);
        try (Writer writer = new FileWriter(fileName + ".csv") ) {
            writer.append("Tag")
                    .append(',');
            for (SaveGame save : saves) {
                writer.append(save.getDate())
                        .append(',');
            }
            writer.append(eol);
            for (String tag : myArray) {
                hasValues = false;
                for (SaveGame save : saves) {
                    if (save.getCountry(tag).getPopulationSizeNumber() != 0) {
                        hasValues = true;
                    }
                }
                if (hasValues) {
                    for (SaveGame save : saves) {
//                    if (save.getCountry(tag).getPopulationSizeNumber() > 0) {
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
}