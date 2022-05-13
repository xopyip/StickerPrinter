package pl.baluch.stickerprinter.elements;

import pl.baluch.stickerprinter.data.DropZone;

import java.util.HashSet;
import java.util.Set;

public abstract class ContainerStickerElement extends StickerElement {
    protected Set<StickerElement> children = new HashSet<>();
    protected transient Set<DropZone> dropZones = new HashSet<>();

    public ContainerStickerElement(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public ContainerStickerElement() {
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

    public Set<DropZone> getDropZones() {
        return dropZones;
    }
}
