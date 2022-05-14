package pl.baluch.stickerprinter.elements.children;

import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import pl.baluch.stickerprinter.elements.StickerElement;

public class Text extends StickerElement<TextFlow> {
    public Text() {
        super(new TextFlow(new javafx.scene.text.Text("Test")));
    }

    @Override
    public void draw(Pane pane) {
        super.setupPositionAndSize(node);
        super.setupNode(pane, node);
        pane.getChildren().add(node);
    }
}
