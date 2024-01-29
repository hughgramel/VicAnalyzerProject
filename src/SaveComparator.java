/**
 * Used to compare save games using their date
 */
class SaveComparator implements java.util.Comparator<SaveGame> {
    @Override
    public int compare(SaveGame a, SaveGame b) {
        if (a.getYear() == b.getYear()) {
            if (a.getMonth() == b.getMonth()) {
                return a.getDay() - b.getDay();
            } else {
                return a.getMonth() - b.getMonth();
            }
        } else {
            return a.getYear() - b.getYear();
        }
    }
}