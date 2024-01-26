import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        System.out.println("Enter the directory containing the Vic 2 Save Games: ");
        File f = new File(console.nextLine());
///Users/hughgramelspacher/Documents/SaveGames
        while (!f.exists()) {
            System.out.println("That file doesn't exist. Input again");
            f = new File(console.nextLine());
        }

        File[] files = f.listFiles();
        List<SaveGame> saveList = new ArrayList<SaveGame>();
        SaveGame save = null;
        int count = 0;


        List<String> tagList = getCoutriesToInput(console);

        for (File eachFile : files) {
            if (!eachFile.getName().endsWith(".DS_Store")) {
                save = new SaveGame(eachFile);
                Map<String, Country> allCountries = save.countCountries();
                saveList.add(save);
                for (String eachTag : tagList) {
                    if (!allCountries.containsKey(eachTag)) {
                        System.out.println("Error: " + eachTag + " not found in savegame");
                        return;
                    }
                }
            }
        }

        Collections.sort(saveList, new SaveComparator());

        eachSavePrintCSV(saveList, tagList, "grinding");
        System.out.println("CSV ouputted");
     }

    /**
     * Creates a list of the countries that a user wants to interact with
      * @param console - Receives user input
     * @return list of country tags that the user inputs
     */
    public static List<String> getCoutriesToInput(Scanner console) {
        System.out.println("Input a list of countries");
        String line = (console.nextLine()).toUpperCase();
        List<String> countryList = new ArrayList<>();
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
     * @param saves
     * @param tagList
     * @param fileName
     */
    public static void eachSavePrintCSV(List<SaveGame> saves, List<String> tagList, String fileName) {

        String eol = System.getProperty("line.separator");

        //print dates

            try (Writer writer = new FileWriter(fileName + ".csv") ) {
                writer.append("Tag")
                        .append(',');
                for (SaveGame save : saves) {
                    writer.append(String.valueOf(save.getYear()))
                            .append(',');
                }
                writer.append(eol);
                for (String tag : tagList) {
                    writer.append(tag);
                    writer.append(',');
                    for (SaveGame save : saves) {
                        writer.append(String.valueOf(save.getCountry(tag).getPopulationSizeNumber()))
                                .append(',');
                    }
                    writer.append(eol);
                }
            }  catch (IOException ex) {
                ex.printStackTrace(System.err);
            }





//        for (String tag : tagList) {
//            System.out.print();
//        }
//
//
//        for (SaveGame s : saves) {
//            try (Writer writer = new FileWriter(fileName + ".csv") ) {
//                writer.append(s.getDate())
//                        .append(',')
//                        .append(s.getDate())
//                        .append(eol);
//                for (Map.Entry<String, Country> entry : s.getCountryMap().entrySet()) {
//                    writer.append(entry.getKey())
//                            .append(',')
//                            .append(entry.getValue().getPopulationSizeString())
//                            .append(eol);
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace(System.err);
//            }
        }
    public static void printCSV(List<SaveGame> saves, String fileName) {

        String eol = System.getProperty("line.separator");
        for (SaveGame s : saves) {
            try (Writer writer = new FileWriter(fileName + ".csv") ) {
                writer.append(s.getDate())
                        .append(',')
                        .append(s.getDate())
                        .append(eol);
                for (Map.Entry<String, Country> entry : s.getCountryMap().entrySet()) {
                    writer.append(entry.getKey())
                            .append(',')
                            .append(entry.getValue().getPopulationSizeString())
                            .append(eol);
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}