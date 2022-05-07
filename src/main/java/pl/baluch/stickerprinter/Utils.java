package pl.baluch.stickerprinter;

import java.util.Comparator;
import java.util.List;

public class Utils {

    /**
     * Removes all items from oldList that aren't present in newList and adds new items to old list
     *
     * @param startIdx   - index to start from, all items from startIdx to end of list should be sorted
     * @param oldList    - list of old items
     * @param newList    - list of new items
     * @param comparator - items comparator
     */
    public static <T> void migrateLists(int startIdx, List<T> oldList, List<T> newList, Comparator<T> comparator) {
        int oldIdx = startIdx;
        for (int newIdx = startIdx; newIdx < newList.size(); ) {
            int cmp = comparator.compare(oldList.get(oldIdx), newList.get(newIdx));
            if (cmp > 0) {
                //There is new item in newList, so we are adding it to oldOne and skip this position on each list
                oldList.add(oldIdx, newList.get(newIdx));
                oldIdx++;
                newIdx++;
            } else if (cmp == 0) {
                //Items are same so we skip to next positions
                oldIdx++;
                newIdx++;
            } else {
                //Current item from oldList is missing in newList, so we must remove it
                oldList.remove(oldIdx);
            }
            if (oldIdx == oldList.size()) {
                //adding all remaining items from newList to oldList
                for (; newIdx < newList.size(); newIdx++) {
                    oldList.add(newList.get(newIdx));
                }
                return;
            }
        }
        //removing all unnecessary items from oldList
        while (oldIdx < oldList.size()) {
            oldList.remove(oldIdx);
        }
    }

    /**
     * Same as method above but with default comparator for strings
     *
     * @param startIdx - index to start from, all items from startIdx to end of list should be sorted
     * @param oldList  - list of old items
     * @param newList  - list of new items
     */
    public static void migrateLists(int startIdx, List<String> oldList, List<String> newList) {
        migrateLists(startIdx, oldList, newList, String::compareToIgnoreCase);
    }
}
