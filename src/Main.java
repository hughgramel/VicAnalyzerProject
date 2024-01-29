import java.io.IOException;
////TAI TUR MUS ARI ARC PLA PLC DUA BRG HLR SPA INC JAP BTV VEN RUS good tags
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the Vic 2 pop parser, made by Hgram for my boy Entara");
        SaveAnalyzer saveAnalyzer = new SaveAnalyzer();
        saveAnalyzer.getFiles();
        System.out.println("Reading files");
        saveAnalyzer.readFiles();
        System.out.println("Setting specific countries...");
        saveAnalyzer.setInputtingSpecificCountries();
        System.out.println("Printing files...");
        saveAnalyzer.printFiles();
    }
}