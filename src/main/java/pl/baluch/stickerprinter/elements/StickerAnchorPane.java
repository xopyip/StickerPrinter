package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.Pane;

import java.util.HashSet;
import java.util.Set;

public class StickerAnchorPane extends StickerElement {
    private Set<StickerElement> children = new HashSet<>();

    @Override
    public void draw(Pane pane) {
        for (StickerElement child : children) {
            child.draw(pane);
        }
    }

    public void addChild(StickerElement o) {
        this.children.add(o);
    }

    public int countChildren() {
        return this.children.size();
    }

    public Set<StickerElement> getChildren() {
        return children;
    }
}
