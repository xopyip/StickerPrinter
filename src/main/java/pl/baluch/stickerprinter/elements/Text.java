package pl.baluch.stickerprinter.elements;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class Text extends StickerElement {
    @Override
    public void draw(Pane pane) {
        Label text = new Label("Test");
        if(x >= 0){
            text.setLayoutX(x);
        }
        if(y >= 0){
            text.setLayoutY(y);
        }
        if(width >= 0){
            text.setPrefWidth(width);
        }
        //super.setupMovable(pane, text); //todo: implementation
        pane.getChildren().add(text);
    }
}
