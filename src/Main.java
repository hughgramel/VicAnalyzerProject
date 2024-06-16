import java.io.IOException;
////TAI TUR MUS ARI ARC PLA PLC DUA BRG HLR SPA INC JAP BTV VEN RUS good tags

/**
 * Main body of a SaveGameParser that reads one / a collection of Vic 2 Save games, and returns
 * the population / "accepted culture" population of countries in each save as a CSV
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Victoria 2 Population Parser");
        System.out.println();   
        
//        System.out.println("This program analyzes the save data of either a folder of Victoria 2 save files");
//        System.out.println("or an individual save, and outputs 2 CSV files: 1 full of the accepted pop counts, and");
//        System.out.println("one full of the total pop counts for a certain
//        amount of countries in each save");
        Long time = System.currentTimeMillis();
        SaveAnalyzer saveAnalyzer = new SaveAnalyzer();
        saveAnalyzer.getFiles();
        saveAnalyzer.readFiles();
        System.out.println("Total Time: " + (System.currentTimeMillis() - time));
        System.out.println("Setting specific countries...");
        saveAnalyzer.setInputtingSpecificCountries();
        System.out.println("Printing files...");
        saveAnalyzer.printFiles();

    }
}