package pl.baluch.stickerprinter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMigrateList {
    @Test
    public void testSameLists() {
        ObservableList<String> a = FXCollections.observableList(new ArrayList<>(Arrays.asList("a", "b", "c", "d")));
        List<String> b = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));

        List<String> copyOfA = new ArrayList<>(a);
        Utils.migrateLists(a, b);

        Assertions.assertLinesMatch(a, copyOfA, "Merge two identical lists shouldn't modify it");
    }

    @Test
    public void testDifferentLists() {
        ObservableList<String> a = FXCollections.observableList(new ArrayList<>(Arrays.asList("a", "b", "c", "d")));
        List<String> b = new ArrayList<>(Arrays.asList("e", "f", "g", "h"));

        Utils.migrateLists(a, b);

        Assertions.assertLinesMatch(a, b, "A should be equal to B");
    }

    @Test
    public void testAddition() {
        ObservableList<String> a = FXCollections.observableList(new ArrayList<>(Arrays.asList("a", "b", "c", "d")));
        List<String> b = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));

        Utils.migrateLists(a, b);

        Assertions.assertLinesMatch(a, b, "A should be equal to B");
    }

    @Test
    public void testSubtraction() {
        ObservableList<String> a = FXCollections.observableList(new ArrayList<>(Arrays.asList("a", "b", "c", "d")));
        List<String> b = new ArrayList<>(Arrays.asList("a", "b"));

        Utils.migrateLists(a, b);

        Assertions.assertLinesMatch(a, b, "A should be equal to B");
    }

    @Test
    public void testMixed() {
        ObservableList<String> a = FXCollections.observableList(new ArrayList<>(Arrays.asList("a", "b", "c", "d")));
        List<String> b = new ArrayList<>(Arrays.asList("a", "b", "d", "e"));

        Utils.migrateLists(a, b);

        Assertions.assertLinesMatch(a, b, "A should be equal to B");
    }
}
