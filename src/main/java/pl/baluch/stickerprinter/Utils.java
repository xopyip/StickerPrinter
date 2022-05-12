package pl.baluch.stickerprinter;

import java.util.List;

public class Utils {

    /**
     * Updates all items in oldList to match newList (including removing excess)
     *
     * @param oldList    - list of old items
     * @param newList    - list of new items
     */
    public static <T> void migrateLists(List<T> oldList, List<T> newList) {
        oldList.retainAll(newList);
        newList.stream().filter(el -> !oldList.contains(el)).forEach(oldList::add);
    }

    public static float getRatio(float width, float height) {
        float ratio = width / height;
        if (ratio < 1) {
            return 1 / ratio;
        }
        return ratio;
    }
}
