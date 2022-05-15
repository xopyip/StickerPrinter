package pl.baluch.stickerprinter.data;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
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
        previewPane.getChildren().clear();
        previewPane.getChildren().add(new Text(item.getName()));
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
