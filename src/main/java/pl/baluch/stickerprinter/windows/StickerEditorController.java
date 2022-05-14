package pl.baluch.stickerprinter.windows;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pl.baluch.stickerprinter.data.DropZone;
import pl.baluch.stickerprinter.data.Orientation;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.data.StickerDesign;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.elements.children.HSpacer;
import pl.baluch.stickerprinter.elements.children.Text;
import pl.baluch.stickerprinter.elements.children.VSpacer;
import pl.baluch.stickerprinter.elements.containers.HBox;
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
    private AnchorPane stickerPane;

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
                new StickerElement.Provider<>(resources.getString("sticker.elements.hSpacer"), HSpacer::new),
                new StickerElement.Provider<>(resources.getString("sticker.elements.hBox"), HBox::new),
                new StickerElement.Provider<>(resources.getString("sticker.elements.label"), pl.baluch.stickerprinter.elements.children.Label::new)
        );
        setupDragSource();
    }

    private void setupDragSource() {

        stickerElementsList.setOnDragDetected(event -> {

            Dragboard db = stickerElementsList.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString(stickerElementsList.getSelectionModel().getSelectedItem().name());
            db.setContent(content);
            stickerPane.getChildren().removeIf(node -> node instanceof DropZone);
            setupDropZones(stickerPane, (ContainerStickerElement) design.getParentNode(), 0, 0);

            event.consume();
        });
        stickerElementsList.setOnDragDone(event -> {
            stickerPane.getChildren().removeIf(node -> node instanceof DropZone);
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
        stickerPane = newPreviewPane(x, y, size.getWidth(), size.getHeight());
        previewPane.getChildren().add(stickerPane);
        design.getParentNode().setX(0);
        design.getParentNode().setY(0);
        design.getParentNode().setWidth(size.getWidth());
        design.getParentNode().setHeight(size.getHeight());

        design.getParentNode().draw(stickerPane);
    }

    private void setupDropZones(Pane previewPane, ContainerStickerElement<Region> parentNode, double x, double y) {
        for (DropZone dropZone : parentNode.getDropZones()) {
            DropZone newDropZone = new DropZone(dropZone.getContainer(), dropZone.getX() + x, dropZone.getY() + y, dropZone.getWidth(), dropZone.getHeight());
            previewPane.getChildren().add(newDropZone);

            setupDragTarget(newDropZone, provider -> {
                StickerElement<?> o = provider.get();
                newDropZone.getContainer().addChild(o);
                design.getParentNode().dump().forEach(System.out::println);
                updatePreviews();
            });

            for (StickerElement<Node> child : parentNode.getChildren()) {
                if(child instanceof ContainerStickerElement) {
                    setupDropZones(previewPane, (ContainerStickerElement) child, x + parentNode.getX(), y + parentNode.getY());
                }
            }
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

    public void setStage(StickerEditorWindow stickerEditorWindow) {
        this.stickerEditorWindow = stickerEditorWindow;
    }
}
