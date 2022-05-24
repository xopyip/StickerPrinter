package pl.baluch.stickerprinter.data;

import javafx.scene.control.TextInputDialog;
import pl.baluch.stickerprinter.Storage;
import pl.baluch.stickerprinter.exceptions.FulfillItemException;
import pl.baluch.stickerprinter.plugins.Item;

import java.util.Optional;

public class FulfilledItem extends Item {
    public FulfilledItem(Item item) {
        super(item.getName(), item.getTypeName());
        for (StickerProperty previewProperty : item.getPreviewProperties()) {
            if (item.isCustomProperty(previewProperty.key())) {
                TextInputDialog inputDialog = new TextInputDialog("");
                inputDialog.setHeaderText(String.format(Storage.getResourceBundle().getString("fullfill.item.property.header"), previewProperty.key()));
                Optional<String> dialogOutput = inputDialog.showAndWait();

                if (dialogOutput.isPresent()) {
                    addProperty(previewProperty.key(), dialogOutput.get());
                }else{
                    throw new FulfillItemException("Canceled");
                }
            } else {
                addProperty(previewProperty.key(), previewProperty.value());
            }
        }
    }
}
