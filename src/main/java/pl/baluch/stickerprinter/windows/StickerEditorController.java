package pl.baluch.stickerprinter.windows;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pl.baluch.stickerprinter.data.Orientation;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.plugins.Item;

import java.net.URL;
import java.util.ResourceBundle;

public class StickerEditorController implements Initializable {
    @FXML
    public Label ratioLabel;
    @FXML
    public Slider toleranceSlider;
    @FXML
    public ChoiceBox<Orientation> orientationChoiceBox;
    @FXML
    public Pane previewPane;
    private Item item;
    private PageStyle pageStyle;

    public StickerEditorController(Item item, PageStyle pageStyle) {
        this.item = item;
        this.pageStyle = pageStyle;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratioLabel.setText(String.format(resources.getString("sticker.editor.ratio"), pageStyle.getCellRatio()));

        orientationChoiceBox.getItems().addAll(Orientation.values());
        orientationChoiceBox.getSelectionModel().selectFirst();

        toleranceSlider.valueProperty().addListener((observable, oldValue, newValue) -> updatePreviews());
        orientationChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> updatePreviews());
        updatePreviews();
    }

    private void updatePreviews() {
        Orientation orientation = orientationChoiceBox.getSelectionModel().getSelectedItem();
        float cellRatio = pageStyle.getCellRatio();
        double minRatio = cellRatio / toleranceSlider.getValue();
        double maxRatio = cellRatio * toleranceSlider.getValue();
        System.out.println(minRatio + " " + maxRatio);
        double previewPaneWidth = previewPane.getPrefWidth();
        double previewPaneHeight = previewPane.getPrefHeight();

        previewPane.getChildren().clear();

        //default preview
        Dimension2D size = switch (orientation) {
            case HORIZONTAL -> new Dimension2D(previewPaneWidth - 20, (previewPaneWidth - 20)/cellRatio);
            case VERTICAL -> new Dimension2D((previewPaneHeight - 20)/cellRatio, previewPaneHeight - 20);
        };
        double x = (previewPaneWidth - size.getWidth())/2;
        double y = (previewPaneHeight - size.getHeight())/2;
        previewPane.getChildren().add(newPreviewRectangle(x, y, size.getWidth(), size.getHeight()));
    }

    private Node newPreviewRectangle(double x, double y, double width, double height) {
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.WHITE);
        return rectangle;
    }
}
