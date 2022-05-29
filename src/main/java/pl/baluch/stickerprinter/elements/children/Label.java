package pl.baluch.stickerprinter.elements.children;

import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.children.base.TextBase;

public class Label extends TextBase<javafx.scene.control.Label> {
    public Label() {
        super(javafx.scene.control.Label::new);
    }

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
    protected void setText(javafx.scene.control.Label node, String text) {
        node.setText(text);
    }
}
