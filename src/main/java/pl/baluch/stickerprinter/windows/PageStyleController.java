package pl.baluch.stickerprinter.windows;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.data.PageStyle;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class PageStyleController implements Initializable {
    @FXML
    private Button saveButton;
    @FXML
    private Pane previewPane;
    @FXML
    private Spinner<Integer> rowAmount;
    @FXML
    private Spinner<Integer> columnAmount;
    @FXML
    private Spinner<Integer> verticalMargin;
    @FXML
    private Spinner<Integer> horizontalMargin;
    @FXML
    private TextField name;

    private static final int pageWidth = 210;
    private static final int pageHeight = 297;

    private final PageStyle pageStyle = new PageStyle("");

    private Consumer<PageStyle> exitHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rowAmount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 32, 1));
        columnAmount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 32, 1));
        verticalMargin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, pageHeight / 2, 0));
        horizontalMargin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, pageWidth / 2, 0));
        rowAmount.valueProperty().addListener((observable, oldValue, newValue) -> {
            pageStyle.setRows(newValue);
            pageStyle.drawPreview(previewPane);
        });
        columnAmount.valueProperty().addListener((observable, oldValue, newValue) -> {
            pageStyle.setColumns(newValue);
            pageStyle.drawPreview(previewPane);
        });
        verticalMargin.valueProperty().addListener((observable, oldValue, newValue) -> {
            pageStyle.setMarginVertical(newValue);
            pageStyle.drawPreview(previewPane);
        });
        horizontalMargin.valueProperty().addListener((observable, oldValue, newValue) -> {
            pageStyle.setMarginHorizontal(newValue);
            pageStyle.drawPreview(previewPane);
        });
        Platform.runLater(() -> pageStyle.drawPreview(previewPane));
        saveButton.setOnMouseClicked(event -> {
            pageStyle.setName(name.getText());
            ResourceBundle resourceBundle = Storage.getResourceBundle();
            if(pageStyle.getName().length() < 3){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(resourceBundle.getString("error"));
                alert.setHeaderText(resourceBundle.getString("nametooshort.text"));
                alert.showAndWait();
                return;
            }
            if(exitHandler != null){
                exitHandler.accept(pageStyle);
            }
        });
    }

    public void setExitHandler(Consumer<PageStyle> exitHandler) {
        this.exitHandler = exitHandler;
    }
}
