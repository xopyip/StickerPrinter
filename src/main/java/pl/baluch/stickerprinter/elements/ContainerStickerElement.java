package pl.baluch.stickerprinter.elements;

import pl.baluch.stickerprinter.data.DropZone;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public List<String> dump() {
        List<String> collect = children.stream().flatMap(children -> children.dump().stream()).map(s -> "    " + s).collect(Collectors.toList());
        collect.addAll(0, super.dump());
        return collect;
    }
}
