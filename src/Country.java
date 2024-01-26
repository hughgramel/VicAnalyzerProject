import java.util.ArrayList;
import java.util.List;

public class Country {
    private String countryName;
    private int populationSize = 0;

    private List<String> acceptedPopList = new ArrayList<>();
    private int acceptedPopTotal = 0;

    public Country(String name) {
        this.countryName = name;
    }

    public String toString() {
        return(countryName);
    }

    /**
     * Increases the total of the countries pop count given an int
     * @param size - size for the pop count to be increased by
     */
    public void addPopCount(int size) {
        populationSize += size;
    }

    public int getPopulationSizeNumber() {
        return populationSize;
    }
    public String getPopulationSizeString() {
        return String.valueOf(populationSize);
    }

}
