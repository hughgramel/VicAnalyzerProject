public class Country {
    String countryName;
    private int populationSize = 0;

    public Country(String name) {
        this.countryName = name;
    }

    public String toString() {
        return(countryName);
    }

    public void addPopCount(int size) {
        populationSize += size;
    }

    public int getPopulationSizeInt() {
        return populationSize;
    }
    public String getPopulationSize() {
        return String.valueOf(populationSize);
    }

}
