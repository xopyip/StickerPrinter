package pl.baluch.stickerprinter.elements.children;

import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElementTypes;
import pl.baluch.stickerprinter.elements.children.base.TextBase;

public class Label extends TextBase<javafx.scene.control.Label> {

    @Override
    protected void setFont(javafx.scene.control.Label node, Font font) {
        node.setFont(font);
    }

    @Override
    public void draw(Pane pane, DrawContext drawContext, ContainerStickerElement<?> parent) {
        super.draw(pane, drawContext, parent);
        getNode().ifPresent(node -> {
            node.setWrapText(false);
        });
    }

    @Override
    protected javafx.scene.control.Label createTextNode() {
        return new javafx.scene.control.Label();
    }

    @Override
    public StickerElementTypes getType() {
        return StickerElementTypes.LABEL;
    }

    @Override
    protected void setText(javafx.scene.control.Label node, String text) {
        node.setText(text);
    }
}
