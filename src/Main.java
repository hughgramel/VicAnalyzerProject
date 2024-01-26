import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        SaveGame save = new SaveGame("/Users/hughgramelspacher/Documents/bigvicsave.txt");
        System.out.println(save.countCountries());
        save.visualizeData();
        save.printCSV(save.printEach());
     }


}