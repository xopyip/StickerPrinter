package pl.baluch.stickerprinter.windows.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.data.PageStyle;
import pl.baluch.stickerprinter.windows.PageStyleWindow;

import java.net.URL;
import java.util.ResourceBundle;

public class PrintController implements Initializable {
    @FXML
    private ChoiceBox<PageStyle> pageStyle;
    @FXML
    private Pane previewPane;
    @FXML
    private Button deletePageStyleButton;
    @FXML
    private Label cellRatioLabel;
    @FXML
    private Button printButton;
    private AppController appController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPageStyles(resources);
        setupStickerPreview();
    }

    private void setupStickerPreview() {
        pageStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> appController.setStickerDesign(appController.matchStickerDesign().orElse(null)));

        printButton.setOnMouseClicked(event -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                job.showPrintDialog(AppMain.getStage());
                PageLayout pageLayout = job.getPrinter().createPageLayout(Paper.A4, PageOrientation.PORTRAIT, 0, 0, 0, 0);
                AnchorPane anchorPane = new AnchorPane();
                //todo: remove border and background from cell
                anchorPane.setPrefSize(previewPane.getPrefWidth(), previewPane.getPrefHeight());
                anchorPane.getChildren().addAll(previewPane.getChildren().stream()
                        .filter(node -> node.getId() != null && node.getId().startsWith("preview-")).toList());
                double scaleFactor = pageLayout.getPrintableWidth() / anchorPane.getPrefWidth();
                Scale scale = new Scale(scaleFactor, scaleFactor);
                anchorPane.getTransforms().add(scale);
                job.printPage(pageLayout, anchorPane);
                job.endJob();
            }
        });
    }

    /**
     * Setups right column with page styles
     */
    private void setupPageStyles(ResourceBundle resourceBundle) {
        //initialize page styles
        pageStyle.getItems().addAll(Storage.getConfig().getPageStyles());
        pageStyle.getSelectionModel().selectFirst();
        pageStyle.getItems().add(new PageStyle.New());

        //draw preview
        Platform.runLater(() -> {
            appController.getPageStyle().ifPresent(style -> style.drawPreview(previewPane, true));
            cellRatioLabel.setText(String.format(resourceBundle.getString("pagestyle.ratio"), pageStyle.getSelectionModel().getSelectedItem().getCellRatio()));
        });

        //add change handler
        pageStyle.setOnAction(event -> {
            SingleSelectionModel<PageStyle> selectionModel = pageStyle.getSelectionModel();
            if (selectionModel.getSelectedItem() instanceof PageStyle.New) {
                PageStyleWindow pageStyleWindow = new PageStyleWindow();
                PageStyle pageStyle = pageStyleWindow.createPageStyle();
                if (pageStyle == null) {
                    return;
                }
                this.pageStyle.getItems().add(this.pageStyle.getItems().size() - 1, pageStyle);
                selectionModel.select(pageStyle);
                Storage.getConfig().getPageStyles().add(pageStyle);
                Storage.saveConfig();
            }
            selectionModel.getSelectedItem().drawPreview(previewPane, true);
            cellRatioLabel.setText(String.format(resourceBundle.getString("pagestyle.ratio"), pageStyle.getSelectionModel().getSelectedItem().getCellRatio()));
        });

        //add delete handler
        deletePageStyleButton.setOnMouseClicked(event -> {
            PageStyle selectedItem = this.pageStyle.getSelectionModel().getSelectedItem();
            pageStyle.getItems().remove(selectedItem);
            Storage.getConfig().getPageStyles().remove(selectedItem);
            pageStyle.getSelectionModel().selectFirst();
            Storage.saveConfig();
        });
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public ChoiceBox<PageStyle> getPageStyle() {
        return pageStyle;
    }
}
