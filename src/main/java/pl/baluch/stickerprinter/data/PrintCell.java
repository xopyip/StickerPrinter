package pl.baluch.stickerprinter.data;

import javafx.scene.Cursor;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.events.PrintCellClickedEvent;
import pl.baluch.stickerprinter.plugins.Item;

import java.util.Objects;

public class PrintCell extends AnchorPane {
    private StickerDesign design;
    private Item item;
    private final Pane parent;

    public PrintCell(double x, double y, double w, double h, double scaleRatio, Pane parent) {
        super();
        this.parent = parent;
        this.setPrefSize(w * scaleRatio, h * scaleRatio);
        this.setLayoutX(x * scaleRatio);
        this.setLayoutY(y * scaleRatio);
        this.setViewOrder(-10);

        this.hoverProperty().addListener((observable, oldValue, newValue) -> {
            this.setBackground(new Background(new BackgroundFill(newValue ? Color.color(0, 0, 0, 0.1f) : Color.TRANSPARENT, null, null)));
            this.setCursor(newValue ? Cursor.HAND : Cursor.DEFAULT);
        });
        this.setOnMouseClicked(event -> AppMain.EVENT_BUS.post(new PrintCellClickedEvent(this)));
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    public void setSticker(StickerDesign stickerDesign, Item item) {
        this.design = stickerDesign;
        this.item = item;
        drawPreview();
    }

    public boolean hasDesign() {
        return this.design != null && this.item != null;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void drawPreview() {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setId("preview-" + this.hashCode());
        double width = this.getPrefWidth();
        double height = this.getPrefHeight();
        Rotate rotate = new Rotate();
        Translate translate = new Translate();
        Orientation orientation = width > height ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        if(orientation != design.getOrientation()){
            double tmp = width;
            width = height;
            height = tmp;
            rotate.setAngle(90);
            translate.setY(-height);
        }
        anchorPane.setLayoutX(this.getLayoutX());
        anchorPane.setLayoutY(this.getLayoutY());
        anchorPane.setPrefSize(width, height);

        anchorPane.setMouseTransparent(true);
        this.design.renderPreview(anchorPane, this.item);

        anchorPane.getTransforms().addAll(rotate, translate);

        parent.getChildren().removeIf(node -> Objects.equals(node.getId(), "preview-" + this.hashCode()));
        parent.getChildren().add(anchorPane);
    }
}
