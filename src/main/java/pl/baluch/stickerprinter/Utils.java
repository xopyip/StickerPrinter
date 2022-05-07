package pl.baluch.stickerprinter;

import javafx.collections.ObservableList;

import java.util.List;

public class Utils {

    /**
     * Updates all items in oldList to match newList (including removing excess)
     *
     * @param oldList    - list of old items
     * @param newList    - list of new items
     */
    public static <T> void migrateLists(ObservableList<T> oldList, List<T> newList) {
        oldList.retainAll(newList);
        newList.stream().filter(el -> !oldList.contains(el)).forEach(oldList::add);
    }

}
