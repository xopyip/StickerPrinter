package pl.baluch.stickerprinter.windows;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pl.baluch.stickerprinter.data.Orientation;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.data.StickerDesign;
import pl.baluch.stickerprinter.elements.HBox;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.elements.Text;
import pl.baluch.stickerprinter.elements.VSpacer;
import pl.baluch.stickerprinter.plugins.Item;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class StickerEditorController implements Initializable {
    private StickerDesign design;
    @FXML
    public Label ratioLabel;
    @FXML
    public Slider toleranceSlider;
    @FXML
    public ChoiceBox<Orientation> orientationChoiceBox;
    @FXML
    public Pane previewPane;
    @FXML
    public Button saveButton;
    @FXML
    private ListView<StickerElement.Provider<?>> stickerElementsList;
    private Item item;
    private PageStyle pageStyle;
    private StickerEditorWindow stickerEditorWindow;

    public StickerEditorController(Item item, PageStyle pageStyle) {
        this.item = item;
        this.pageStyle = pageStyle;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratioLabel.setText(String.format(resources.getString("sticker.editor.ratio"), pageStyle.getCellRatio()));

        orientationChoiceBox.getItems().addAll(Orientation.values());
        orientationChoiceBox.getSelectionModel().selectFirst();
        this.design = new StickerDesign(pageStyle.getCellRatio(), 1, orientationChoiceBox.getSelectionModel().getSelectedItem(), item.getTypeName());

        toleranceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            design.setTolerance(newValue.floatValue());
            updatePreviews();
        });
        orientationChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            design.setOrientation(newValue);
            updatePreviews();
        });
        updatePreviews();
        setupStickerElements(resources);
        saveButton.setOnMouseClicked(event -> System.err.println("Not implemented yet"));

    }

    private void setupStickerElements(ResourceBundle resources) {
        stickerElementsList.getItems().addAll(
                new StickerElement.Provider<>(resources.getString("sticker.elements.text"), Text::new),
                new StickerElement.Provider<>(resources.getString("sticker.elements.vSpacer"), VSpacer::new),
                new StickerElement.Provider<>(resources.getString("sticker.elements.hBox"), HBox::new)
        );
        setupDragSource();
    }

    private void setupDragSource() {

        stickerElementsList.setOnDragDetected(event -> {

            Dragboard db = stickerElementsList.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString(stickerElementsList.getSelectionModel().getSelectedItem().name());
            db.setContent(content);

            event.consume();
        });
    }

    private void setupDragTarget(Rectangle rectangle, Consumer<StickerElement.Provider<?>> onDrop) {
        rectangle.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            event.consume();
        });
        rectangle.setOnDragEntered(event -> {
            rectangle.setFill(Color.LIGHTBLUE);
            event.consume();
        });
        rectangle.setOnDragExited(event -> {
            rectangle.setFill(Color.TRANSPARENT);
            event.consume();
        });
        rectangle.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                StickerElement.Provider<?> provider = stickerElementsList.getItems().stream().filter(el -> el.name().equals(db.getString())).findAny().get();
                onDrop.accept(provider);
                event.setDropCompleted(true);
            }
            event.consume();
        });
    }

    private void updatePreviews() {
        Orientation orientation = orientationChoiceBox.getSelectionModel().getSelectedItem();
        float cellRatio = pageStyle.getCellRatio();
        double minRatio = cellRatio / toleranceSlider.getValue();
        double maxRatio = cellRatio * toleranceSlider.getValue();

        double previewPaneWidth = previewPane.getPrefWidth();
        double previewPaneHeight = previewPane.getPrefHeight();

        previewPane.getChildren().clear();

        //default preview
        Dimension2D size = switch (orientation) {
            case HORIZONTAL -> new Dimension2D(previewPaneWidth - 20, (previewPaneWidth - 20) / cellRatio);
            case VERTICAL -> new Dimension2D((previewPaneHeight - 20) / cellRatio, previewPaneHeight - 20);
        };
        double x = (previewPaneWidth - size.getWidth()) / 2;
        double y = (previewPaneHeight - size.getHeight()) / 2;
        AnchorPane stickerPane = newPreviewPane(x, y, size.getWidth(), size.getHeight());
        previewPane.getChildren().add(stickerPane);

        design.getParentNode().draw(stickerPane);

        if (design.getParentNode().countChildren() == 0) {
            Rectangle dropRectangle = newDropRectangle(x, y, size.getWidth(), size.getHeight());
            previewPane.getChildren().add(dropRectangle);
            setupDragTarget(dropRectangle, provider -> {
                StickerElement o = provider.get();
                design.getParentNode().addChild(o);
                updatePreviews();
            });
        }
    }

    private AnchorPane newPreviewPane(double x, double y, double width, double height) {
        AnchorPane rectangle = new AnchorPane();
        rectangle.setLayoutX(x);
        rectangle.setLayoutY(y);
        rectangle.setPrefSize(width, height);
        rectangle.setMaxSize(width, height);
        rectangle.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        return rectangle;
    }

    private Rectangle newDropRectangle(double x, double y, double width, double height) {
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(Color.TRANSPARENT);
        return rectangle;
    }

    public void setStage(StickerEditorWindow stickerEditorWindow) {
        this.stickerEditorWindow = stickerEditorWindow;
    }
}
