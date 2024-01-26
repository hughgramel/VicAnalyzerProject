/**
 * Used to compare save games using their year
 */
class SaveComparator implements java.util.Comparator<SaveGame> {
    @Override
    public int compare(SaveGame a, SaveGame b) {
        return a.getYear() - b.getYear();
    }
}