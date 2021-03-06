package pl.baluch.stickerprinter.windows.stickereditor;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.util.Callback;
import pl.baluch.stickerprinter.data.StickerElementProperty;
import pl.baluch.stickerprinter.plugins.Item;

public class StickerElementPropertyTableCell extends ChoiceBoxTableCell<StickerElementProperty, String> {
    private static Item item;
    private boolean needInitialization = true;
    public StickerElementPropertyTableCell() {
        super();
        this.getStyleClass().add("choice-box-table-cell");
    }

    public static Callback<TableColumn<StickerElementProperty, String>, TableCell<StickerElementProperty, String>> createForItem(Item item) {
        StickerElementPropertyTableCell.item = item;
        return column -> new StickerElementPropertyTableCell();
    }

    @Override
    public void startEdit() {
        if(needInitialization){
            StickerElementProperty property = getTableView().getItems().get(getIndex());
            assert property != null;
            getItems().addAll(property.getValues(item));
            needInitialization = false;
        }
        super.startEdit();
    }
}
