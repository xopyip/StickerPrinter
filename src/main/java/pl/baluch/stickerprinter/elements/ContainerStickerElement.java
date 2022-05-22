package pl.baluch.stickerprinter.elements;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import pl.baluch.stickerprinter.data.DropZone;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class ContainerStickerElement<T extends Region> extends StickerElement<T> {
    protected Set<StickerElement<? extends Node>> children = new HashSet<>();
    protected transient Set<DropZone> dropZones = new HashSet<>();

    public ContainerStickerElement(Supplier<T> node, int x, int y, int w, int h) {
        super(node, x, y, w, h);
    }

    public ContainerStickerElement(Supplier<T> node) {
        super(node);
    }

    public void addChild(StickerElement<? extends Node> o) {
        this.children.add(o);
    }

    public int countChildren() {
        return this.children.size();
    }

    public Set<StickerElement<? extends Node>> getChildren() {
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "children=" + Arrays.toString(children.toArray()) +
                '}';
    }

    public boolean removeChild(StickerElement<?> stickerElement, boolean deep) {
        if(this.getChildren().contains(stickerElement)) {
            this.getChildren().remove(stickerElement);
            return true;
        }else if(deep) {
            return this.getChildren().stream()
                    .filter(child -> child instanceof ContainerStickerElement)
                    .anyMatch(child -> ((ContainerStickerElement<?>) child).removeChild(stickerElement, true));
        }
        return false;
    }
}
