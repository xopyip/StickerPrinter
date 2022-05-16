package pl.baluch.stickerprinter.data;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import pl.baluch.stickerprinter.elements.containers.StickerAnchorPane;
import pl.baluch.stickerprinter.plugins.Item;

public class StickerDesign {
    //longer to shorter edge ratio
    private float ratio;
    //how many times ratio can be multiplied/divided and stay valid
    //should be at least 1.0
    private float tolerance;
    private Orientation orientation;
    //name of item group
    private String itemGroup;
    private final StickerAnchorPane node = new StickerAnchorPane();

    public StickerDesign() {
    }

    public StickerDesign(float ratio, float tolerance, Orientation orientation, String itemGroup) {
        this.ratio = ratio;
        this.tolerance = tolerance;
        this.orientation = orientation;
        if(tolerance < 1){
            throw new IllegalArgumentException("Tolerance must be at least 1");
        }
        this.itemGroup = itemGroup;
    }

    public void setTolerance(float tolerance) {
        this.tolerance = tolerance;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public boolean matches(float ratio, String itemGroup){
        float maxRatio = this.ratio * this.tolerance;
        float minRatio = this.ratio / this.tolerance;
        return (ratio >= minRatio && ratio <= maxRatio) && this.itemGroup.equals(itemGroup);
    }

    public void renderPreview(Pane previewPane, Item item) {
        previewPane.setPrefWidth(node.getWidth());
        previewPane.setPrefHeight(node.getHeight());
        previewPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        previewPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        previewPane.getChildren().clear();

        this.getParentNode().draw(previewPane, new DrawContext(item, false));

        double scaleFactor = previewPane.getWidth()/node.getWidth();
        Scale scale = new Scale(scaleFactor, scaleFactor, 0, 0);
        Translate translate = new Translate(0, 0);
        previewPane.getTransforms().add(scale);
        previewPane.getTransforms().add(translate);
        previewPane.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                scale.setX(scaleFactor * 1.95);
                scale.setY(scaleFactor * 1.95);
                translate.setX(-previewPane.getWidth()/2.05);
            }else{
                scale.setX(scaleFactor);
                scale.setY(scaleFactor);
                translate.setX(0);
            }
        });
    }

    public StickerAnchorPane getParentNode() {
        return this.node;
    }

    @Override
    public String toString() {
        return "StickerDesign{" +
                "ratio=" + ratio +
                ", tolerance=" + tolerance +
                ", orientation=" + orientation +
                ", itemGroup='" + itemGroup + '\'' +
                ", node=" + node +
                '}';
    }

    public double getTolerance() {
        return tolerance;
    }
}
