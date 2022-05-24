package pl.baluch.stickerprinter.elements.children;

import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.data.DrawContext;
import pl.baluch.stickerprinter.data.StickerElementProperty;
import pl.baluch.stickerprinter.data.StickerProperty;
import pl.baluch.stickerprinter.elements.StickerElement;
import pl.baluch.stickerprinter.plugins.Item;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QRCode extends StickerElement<ImageView> {
    private final SimpleStringProperty content = new SimpleStringProperty("test");

    public QRCode() {
        super(ImageView::new, 10, 10, 200, 200);
        addProperty(StickerElementProperty.builder("Content")
                .value(content::get)
                .onChange(value -> {
                    if (value == null) {
                        return;
                    }
                    if (value.equals(Storage.getResourceBundle().getString("custom.property.name"))) {
                        TextInputDialog inputDialog = new TextInputDialog("");
                        inputDialog.setHeaderText(Storage.getResourceBundle().getString("custom.property.header"));
                        Optional<String> dialogOutput = inputDialog.showAndWait();
                        if (dialogOutput.isPresent()) {
                            value = dialogOutput.get();
                        }
                    }
                    content.set(value);
                    updateNode();
                })
                .setChoices(item -> {
                    List<String> strings = new ArrayList<>(
                            item.getPreviewProperties().stream()
                                    .map(StickerProperty::key)
                                    .map(s -> ":" + s)
                                    .toList()
                    );
                    strings.add(Storage.getResourceBundle().getString("custom.property.name"));
                    return strings;
                })
                .build());
    }

    private void updateNode() {
        getNode().ifPresent(node -> getContext().ifPresent(ctx -> updateNode(node, ctx.item())));
    }

    private void updateNode(ImageView node, Item item) {
        try {
            String text = formatPropertyValue(content.get(), item);
            byte[] data = generateQRCodeImage(text, (int) Math.min(getWidth(), getHeight()));
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            node.setImage(new Image(is));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static byte[] generateQRCodeImage(String barcodeText, int size) throws Exception {
        if(barcodeText.isEmpty()) {
            throw new Exception("Empty barcode text");
        }
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, size, size);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream, new MatrixToImageConfig(0xff000000, 0xffffffff));
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public void draw(Pane pane, DrawContext drawContext) {
        ImageView node = nodeSupplier.get();
        bindBounds(node);
        super.setupNode(pane, node, drawContext);
        updateNode(node, drawContext.item());
        pane.getChildren().add(node);
    }

    @Override
    public void deserialize(JsonObject properties) {
        if (properties.has("content")) content.set(properties.get("content").getAsString());
    }

    @Override
    public JsonObject serialize() {
        JsonObject properties = new JsonObject();
        properties.addProperty("content", content.get());
        return properties;
    }
}
