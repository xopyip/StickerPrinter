package pl.baluch.stickerprinter.elements;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import pl.baluch.stickerprinter.data.DropZone;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class ContainerStickerElement<T extends Region> extends StickerElement<T> {
    protected List<StickerElement<? extends Node>> children = new ArrayList<>();
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

    public List<StickerElement<? extends Node>> getChildren() {
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
        System.out.println("looking for " + stickerElement + " in " + this);
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

    public List<StickerElement<?>> getPath(StickerElement<?> element) {
        List<StickerElement<?>> stickerElements = new ArrayList<>();

        if(this.getChildren().contains(element)) {
            stickerElements.add(this);
            return stickerElements;
        }else {
            Optional<List<StickerElement<?>>> first = this.getChildren().stream()
                    .filter(child -> child instanceof ContainerStickerElement)
                    .map(child -> ((ContainerStickerElement<?>) child).getPath(element))
                    .filter(Objects::nonNull)
                    .findFirst();
            if(first.isPresent()) {
                stickerElements.add(this);
                stickerElements.addAll(first.get());
                return stickerElements;
            }
        }
        return null;
    }
}
