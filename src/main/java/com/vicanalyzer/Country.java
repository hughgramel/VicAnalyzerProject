package com.vicanalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Represents one country in a save game, identified by its tag, and containing
 * data regarding its size, accepted cultures, accepted culture size, and whether it's a human or not.
 */
public class Country {
    private String countryName;
    private boolean isHuman;
    private int populationSize = 0;

    private HashSet<String> acceptedPopList = new HashSet();
    private int acceptedPopTotal = 0;

    public Country(String name) {
        this.countryName = name;
    }

    public void setIsHuman() {
        this.isHuman = true;
    }

    /**
     * Increases the total of the countries pop count given an int
     * @param size - size for the pop count to be increased by
     */
    public void addPopCount(int size) {
        populationSize += size;
    }

    /**
     * Returns a list of accepted pops to be used to determine whether to
     * add a pop group to a total or not
     * @return list of all accepted pops of a country
     */
    public HashSet<String> getAcceptedPopList() {
        return acceptedPopList;
    }

    /**
     * Adds accepted pop count to a countries total accepted pop number
     * @param populationSize - how much is being added
     */
    public void addAcceptedPopTotal(int populationSize) {
        this.acceptedPopTotal += populationSize;
    }

    /**
     * Adds accepted pop culture to a countries accepted pop
     * @param culture - culture to be added
     */
    public void addAcceptedPopType(String culture) {
        acceptedPopList.add(culture);
    }

    /**
     * Gets int for total of pop size
     * @return int for total of pop size
     */
    public int getPopulationSizeNumber() {
        return populationSize;
    }

    /**
     * Gets int for total of accepted pop
     * @return int for total of accepted pop
     */
    public int getAcceptedPopTotal() {
        return acceptedPopTotal;
    }
}
