package pl.baluch.stickerprinter.windows.stickereditor;

import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.data.*;
import pl.baluch.stickerprinter.elements.ContainerStickerElement;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.elements.StickerElementTypes;
import pl.baluch.stickerprinter.events.DeleteStickerElementEvent;
import pl.baluch.stickerprinter.events.SelectStickerElementEvent;
import pl.baluch.stickerprinter.plugins.Item;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class StickerEditorController implements Initializable {
    private StickerDesign design;
    @FXML
    public Label ratioLabel;
    @FXML
    public Label selectedElementLabel;
    @FXML
    public TableView<StickerElementProperty> selectedElementProperties;
    @FXML
    private TableColumn<StickerElementProperty, String> selectedElementPropertiesKey;
    @FXML
    private TableColumn<StickerElementProperty, String> selectedElementPropertiesValue;
    @FXML
    public Slider toleranceSlider;
    @FXML
    public ChoiceBox<Orientation> orientationChoiceBox;
    @FXML
    public Pane previewPane;
    @FXML
    public Button saveButton;
    @FXML
    private ListView<StickerElement.Provider> stickerElementsList;
    private final Item item;
    private final PageStyle pageStyle;
    private StickerEditorWindow stickerEditorWindow;
    private AnchorPane stickerPane;
    private StickerElement<?> selectedElementSticker;

    public StickerEditorController(Item item, PageStyle pageStyle) {
        this.item = item;
        this.pageStyle = pageStyle;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratioLabel.setText(String.format(resources.getString("sticker.editor.ratio"), pageStyle.getCellRatio()));

        orientationChoiceBox.getItems().addAll(Orientation.values());
        orientationChoiceBox.getSelectionModel().selectFirst();
        this.design = Storage.getConfig()
                .getStickerDesign(pageStyle.getCellRatio(), item.getTypeName())
                .orElse(new StickerDesign(pageStyle.getCellRatio(), 1, orientationChoiceBox.getSelectionModel().getSelectedItem(), item.getTypeName()));
        toleranceSlider.setValue(design.getTolerance());

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
        saveButton.setOnMouseClicked(event -> {
            if (!Storage.getConfig().getStickerDesigns().contains(design)) {
                Storage.getConfig().getStickerDesigns().add(design);
            }
            Storage.saveConfig();
            AppMain.EVENT_BUS.unregister(StickerEditorController.this);
            stickerEditorWindow.close();
        });
        selectedElementPropertiesKey.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().key()));
        selectedElementPropertiesValue.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().value()));

        AppMain.EVENT_BUS.register(this);
    }

    /**
     * Update sticker elements list
     */
    private void setupStickerElements(ResourceBundle resources) {
        Arrays.stream(StickerElementTypes.values()).map(StickerElement.Provider::new).forEach(stickerElementsList.getItems()::add);
        stickerElementsList.getItems().addAll();
        setupDragSource();
    }

    /**
     * Sets listeners for dragging elements from list
     */
    private void setupDragSource() {
        stickerElementsList.setOnDragDetected(event -> {
            Dragboard db = stickerElementsList.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString(stickerElementsList.getSelectionModel().getSelectedItem().toString());
            db.setContent(content);
            stickerPane.getChildren().removeIf(node -> node instanceof DropZone);
            setupDropZones(stickerPane, (ContainerStickerElement) design.getParentNode(), 0, 0);

            event.consume();
        });
        stickerElementsList.setOnDragDone(event -> stickerPane.getChildren().removeIf(node -> node instanceof DropZone));
    }


    /**
     * Resets preview pane and adds all sticker elements to it
     */
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
        stickerPane = new PreviewPane(x, y, size.getWidth(), size.getHeight());
        previewPane.getChildren().add(stickerPane);
        design.getParentNode().setX(0);
        design.getParentNode().setY(0);
        design.getParentNode().setWidth(size.getWidth());
        design.getParentNode().setHeight(size.getHeight());

        design.getParentNode().draw(stickerPane, new DrawContext(item, true));
    }

    /**
     * For each dropzone of `parentNode` create a new dropzone with the size and position moved by vector [x,y] and add it to `previewPane`
     *
     * @param previewPane - parent pane of the dropzones
     * @param parentNode  - node to create dropzones for
     * @param x           - x offset
     * @param y           - y offset
     */
    private void setupDropZones(Pane previewPane, ContainerStickerElement<Region> parentNode, double x, double y) {
        for (DropZone dropZone : parentNode.getDropZones()) {
            if (!stickerElementsList.getSelectionModel().getSelectedItem().type().isParentValid(dropZone.getContainer())) {
                continue;
            }
            if (!dropZone.getContainer().getType().isChildValid(stickerElementsList.getSelectionModel().getSelectedItem().type())) {
                continue;
            }
            DropZone newDropZone = new DropZone(dropZone.getContainer(), dropZone.getX() + x, dropZone.getY() + y, dropZone.getWidth(), dropZone.getHeight());
            previewPane.getChildren().add(newDropZone);

            setupDragTarget(newDropZone, newDropZone.getContainer());
        }

        for (StickerElement<? extends Node> child : parentNode.getChildren()) {
            if (child instanceof ContainerStickerElement) {
                setupDropZones(previewPane, (ContainerStickerElement) child, x + parentNode.getX(), y + parentNode.getY());
            }
        }
    }

    /**
     * Create listeners for drop events on the dropzone
     *
     * @param rectangle - node of the dropzone
     * @param container - container to add to
     */
    private void setupDragTarget(Rectangle rectangle, ContainerStickerElement<?> container) {
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
                StickerElement.Provider provider = stickerElementsList.getItems().stream()
                        .filter(el -> el.toString().equals(db.getString())).findAny().orElseThrow();
                container.addChild(provider.get());
                design.getParentNode().dump().forEach(System.out::println);
                updatePreviews();
                event.setDropCompleted(true);
            }
            event.consume();
        });
    }

    public void setStage(StickerEditorWindow stickerEditorWindow) {
        this.stickerEditorWindow = stickerEditorWindow; //todo: reimplement using events
    }

    @Subscribe
    public void onStickerElementDelete(DeleteStickerElementEvent event) {
        ContainerStickerElement<?> parentNode = this.design.getParentNode();
        if (parentNode.removeChild(event.stickerElement(), true)) {
            System.out.println("Removed successfully");
        }else{
            System.out.println("Not found");
        }
        updatePreviews();
    }

    @Subscribe
    public void onSelectStickerElement(SelectStickerElementEvent event){
        if(selectedElementSticker != null){
            selectedElementSticker.setSelected(false);
        }
        this.selectedElementSticker = event.stickerElement();
        if(event.stickerElement() != null){
            selectedElementLabel.setText(String.format(Storage.getResourceBundle().getString("sticker.editor.selected"), event.stickerElement()));
            event.stickerElement().setSelected(true);
            selectedElementProperties.setDisable(false);
            selectedElementProperties.getItems().clear();

            selectedElementProperties.getItems().addAll(event.stickerElement().getProperties());

            selectedElementPropertiesValue.setCellFactory(StickerElementPropertyTableCell.createForItem(item));
            selectedElementPropertiesValue.addEventHandler(TableColumn.CellEditEvent.ANY, editEvent -> {
                TableColumn.CellEditEvent<StickerElementProperty, String> cellEditEvent = (TableColumn.CellEditEvent<StickerElementProperty, String>) editEvent;
                cellEditEvent.getRowValue().update(cellEditEvent.getNewValue());
            });
        }else{
            selectedElementLabel.setText(Storage.getResourceBundle().getString("sticker.editor.noselection"));
            selectedElementProperties.setDisable(true);
            selectedElementProperties.getItems().clear();
        }
        updatePreviews();
    }
}
