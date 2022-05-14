package pl.baluch.stickerprinter.elements;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import pl.baluch.stickerprinter.data.DropZone;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ContainerStickerElement<T extends Region> extends StickerElement<T> {
    protected Set<StickerElement<Node>> children = new HashSet<>();
    protected transient Set<DropZone> dropZones = new HashSet<>();

    public ContainerStickerElement(T node, int x, int y, int w, int h) {
        super(node, x, y, w, h);
    }

    public ContainerStickerElement(T node) {
        super(node);
    }

    public void addChild(StickerElement<Node> o) {
        this.children.add(o);
    }

    public int countChildren() {
        return this.children.size();
    }

    public Set<StickerElement<Node>> getChildren() {
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
