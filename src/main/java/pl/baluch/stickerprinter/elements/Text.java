package pl.baluch.stickerprinter.elements;

import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

public class Text extends StickerElement {
    @Override
    public void draw(Pane pane) {
        javafx.scene.text.Text text = new javafx.scene.text.Text("Test");
        TextFlow textFlow = new TextFlow(text);
        super.setPositionAndSize(textFlow);
        super.setupNode(pane, this, textFlow);
        pane.getChildren().add(textFlow);
    }
}
