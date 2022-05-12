package pl.baluch.stickerprinter.data;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import pl.baluch.stickerprinter.plugins.Item;

public class StickerDesign {
    //longer to shorter edge ratio
    private float ratio;
    //how many times ratio can be multiplied/divided and stay valid
    //should be at least 1.0
    private float tolerance;
    private boolean isVertical;
    //name of item group
    private String itemGroup;

    public StickerDesign() {
    }

    public StickerDesign(float ratio, float tolerance, boolean isVertical, String itemGroup) {
        this.ratio = ratio;
        this.tolerance = tolerance;
        this.isVertical = isVertical;
        if(tolerance < 1){
            throw new IllegalArgumentException("Tolerance must be at least 1");
        }
        this.itemGroup = itemGroup;
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
}
