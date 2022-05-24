package pl.baluch.stickerprinter.windows.main;

import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.data.StickerDesign;
import pl.baluch.stickerprinter.data.StickerProperty;
import pl.baluch.stickerprinter.events.StickerDesignChangedEvent;
import pl.baluch.stickerprinter.plugins.Item;
import pl.baluch.stickerprinter.windows.stickereditor.StickerEditorWindow;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PreviewController implements Initializable {
    @FXML
    private Pane stickerPreviewPane;
    @FXML
    private TableView<StickerProperty> stickerPreviewDataTable;
    @FXML
    private TableColumn<StickerProperty, String> stickerPreviewKeyColumn;
    @FXML
    private TableColumn<StickerProperty, String> stickerPreviewValueColumn;
    @FXML
    private Text stickerPreviewPropertyText;
    @FXML
    private Label stickerPreviewItemTypeText;
    @FXML
    private Button stickerDesignEdit;
    private ResourceBundle resources;
    private AppController appController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        stickerPreviewKeyColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().key()));
        stickerPreviewValueColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().value()));
        stickerPreviewDataTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> stickerPreviewPropertyText.setText(newValue.key() + ": " + newValue.value()));
        stickerDesignEdit.setOnMouseClicked(event -> {
            StickerEditorWindow editorWindow = new StickerEditorWindow(appController.getItemListController().getItem().orElseThrow(), appController.getPageStyle().orElseThrow());
            editorWindow.showAndWait();
            updateStickerPreview(resources);
        });
        AppMain.EVENT_BUS.register(this);
    }

    private void updateStickerPreview(ResourceBundle resourceBundle) {
        Optional<Item> selectedItem = appController.getItemListController().getItem();

        stickerPreviewDataTable.getItems().clear();
        stickerPreviewItemTypeText.setText("");
        stickerDesignEdit.setVisible(selectedItem.isPresent());

        StickerDesign currentStickerDesign = appController.getStickerDesign();
        stickerDesignEdit.setText(currentStickerDesign != null ? resourceBundle.getString("sticker.preview.edit") : resourceBundle.getString("sticker.preview.create"));
        stickerPreviewPane.getChildren().clear();
        if (currentStickerDesign != null && selectedItem.isPresent()) {
            Item item = selectedItem.get();
            stickerPreviewDataTable.getItems().addAll(item.getPreviewProperties());
            stickerPreviewItemTypeText.setText(String.format(resourceBundle.getString("sticker.preview.itemtype"), item.getTypeName()));
            currentStickerDesign.renderPreview(stickerPreviewPane, selectedItem.get());
        } else {
            TextFlow textFlow = new TextFlow(new Text(resourceBundle.getString("sticker.preview.no_design")));
            textFlow.setLayoutX(10);
            textFlow.setLayoutY(10);
            textFlow.setPrefWidth(stickerPreviewPane.getWidth() - 20);
            stickerPreviewPane.getChildren().add(textFlow);
        }
    }

    @Subscribe
    public void onUpdate(StickerDesignChangedEvent event) {
        updateStickerPreview(resources);
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }


}
